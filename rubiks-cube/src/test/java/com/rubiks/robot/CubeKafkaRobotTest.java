package com.rubiks.robot;

import java.util.UUID;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.PullImageResultCallback;

public class CubeKafkaRobotTest extends TestCase {

	private static final String HA_KAFKA_HUB_NAME = "mcarville/sandbox:ha_kafka_3";

	protected Logger logger = Logger.getLogger(getClass());
	
	protected String containerId;
	protected DockerClient dockerClient;
	
	@Override
	protected void setUp() throws Exception {

		dockerClient = DockerClientBuilder.getInstance().build();
		
		AuthConfig authConfig = new AuthConfig();
		
		String dockerUsername = System.getenv().get("DOCKER_USERNAME");
		String dockerPassword = System.getenv().get("DOCKER_PASSWORD");
		
		if(StringUtils.isNotEmpty(dockerUsername) && StringUtils.isNotEmpty(dockerPassword))
			authConfig.withUsername(dockerUsername).withPassword(dockerPassword);
		
        dockerClient.authCmd().withAuthConfig(authConfig).exec();
        
		CreateContainerCmd containerConfig = 
				dockerClient.createContainerCmd(HA_KAFKA_HUB_NAME)
				.withPortBindings(PortBinding.parse("9092:9092"), PortBinding.parse("9000:9000"));
		
		String osName = System.getProperty("os.name");
		if(StringUtils.isNotEmpty(osName) && osName.equalsIgnoreCase("Windows 7"))
			containerConfig.withEnv(String.format("KAFKA_ADVERTISED_HOST_NAME=%s", "192.168.71.131")); // DEV Config
		else
			containerConfig.withEnv(String.format("KAFKA_ADVERTISED_HOST_NAME=%s", "127.0.0.1"));
	
		CreateContainerResponse container = null;
		try 
		{ 
			container = containerConfig.exec();
		}
		catch(NotFoundException e) {
			
			logger.info(String.format("Docker image: %s does not exist locally => trying to get it from the registry", HA_KAFKA_HUB_NAME));
			
			dockerClient.pullImageCmd(HA_KAFKA_HUB_NAME)
	        	.withAuthConfig(authConfig)
	        	.exec(new PullImageResultCallback()).awaitSuccess();
			
			logger.info(String.format("Docker image: %s has been downloaded", HA_KAFKA_HUB_NAME));
			
			container = containerConfig.exec();
		}
		
		containerId = container.getId();
		
		dockerClient.startContainerCmd(containerId).exec();
		
		logger.info(String.format("Start container with containerId: %s", containerId));
	}
	
	@Override
	protected void tearDown() throws Exception {
		if(dockerClient != null && StringUtils.isNotEmpty(containerId)) {
			dockerClient.stopContainerCmd(containerId).exec();
			
			logger.info(String.format("Stop container with containerId: %s", containerId));
			
			dockerClient.close();
		}
	}
	
	public void testKafkaProducer() {
		CubeKafkaRobot.deliverResponse(UUID.randomUUID().toString(), "One test", new TestCallback());
	}
	
	private class TestCallback implements Callback {
	       @Override
	       public void onCompletion(RecordMetadata recordMetadata, Exception exception) {
	           if (exception != null) {
	        	   throw new IllegalStateException("Error while producing message to topic :" + recordMetadata);
	           } else {
	        	   throw new IllegalStateException(String.format("sent message to topic:%s partition:%s  offset:%s", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset()));
	           }
	       }
	   }
	
	public void testConsumer() {
		Consumer<String, String> consumer = CubeKafkaRobot.buildConsumer();
		String topic = "request";
		
		ConsumerRecords<String, String> records = consumer.poll(1000);
		if(! records.isEmpty()) {
			
			for(ConsumerRecord<String, String> record : records.records(topic)) {
				logger.info(String.format("Record from topic (%s) %s => %s", topic, record.key(), record.value()));
			}
		}
		consumer.close();
	}
}
