package com.rubiks.robot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
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

	private static final String HA_KAFKA_HUB_NAME = "mcarville/sandbox:ha_kafka_5";

	protected Logger logger = Logger.getLogger(getClass());
	
	protected String containerId;
	protected DockerClient dockerClient;
	
	@Override
	protected void setUp() throws Exception {

		String osName = System.getProperty("os.name");
		if(StringUtils.isNotEmpty(osName) && osName.equalsIgnoreCase("Windows 7"))
		{
			logger.info("You are running in DEV environnement Windows");
//			containerConfig.withEnv(String.format("KAFKA_ADVERTISED_HOST_NAME=%s", "192.168.71.131")); // DEV Config
		}
		else
		{
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
		CubeKafkaRobot.writeMessageToQueue("response", UUID.randomUUID().toString(), "One test", new TestCallback());
	}

	public void testConsumer() {
		
		String topic = "request";
		List<String> topics = Arrays.asList(topic.split(","));
		Consumer<String, String> consumer = CubeKafkaRobot.buildConsumer(topics);
		
		ConsumerRecords<String, String> records = consumer.poll(1000);
		if(! records.isEmpty()) {
			
			for(ConsumerRecord<String, String> record : records.records(topic)) {
				logger.info(String.format("Record from topic (%s) %s => %s", topic, record.key(), record.value()));
			}
		}
		consumer.close();
	}
	
	public void testConsumeAndProduceSingleRobot() throws InterruptedException {
		
		int todoRequestNumber = 10;
		
		TestRobotResponseConsumer testRobotResponseConsumer = executeConsumeAndProduceTest(todoRequestNumber, 1, 2);
		
		assertEquals(todoRequestNumber, testRobotResponseConsumer.getResponseMap().size());
	}
	
	public void testConsumeAndProduceTwoRobot() throws InterruptedException {
		
		int todoRequestNumber = 10;
		
		TestRobotResponseConsumer testRobotResponseConsumer = executeConsumeAndProduceTest(todoRequestNumber, 2, 4);
		
		assertEquals(todoRequestNumber, testRobotResponseConsumer.getResponseMap().size());
	}
	
	public void testConsumeAndProduceFiveRobot() throws InterruptedException {
		
		int todoRequestNumber = 10;
		
		TestRobotResponseConsumer testRobotResponseConsumer = executeConsumeAndProduceTest(todoRequestNumber, 5, 10);
		
		assertEquals(todoRequestNumber, testRobotResponseConsumer.getResponseMap().size());
	}

	protected TestRobotResponseConsumer executeConsumeAndProduceTest(int todoRequestNumber, int cubeKafkaRobotNumber, int requesterNumber) throws InterruptedException {
		ThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(1 + cubeKafkaRobotNumber);
		
		List<CubeKafkaRobot> cubeKafkaRobots = new ArrayList<CubeKafkaRobot>();
		for(int i = 0 ; i < cubeKafkaRobotNumber ; i++)
			cubeKafkaRobots.add(new CubeKafkaRobot());
		for(CubeKafkaRobot cubeKafkaRobot : cubeKafkaRobots)
			threadPoolExecutor.submit(cubeKafkaRobot);
		
		TestRobotResponseConsumer testRobotResponseConsumer = new TestRobotResponseConsumer();
		threadPoolExecutor.submit(testRobotResponseConsumer);
		
		int i = 0;
		while(i < todoRequestNumber) {
			CubeKafkaRobot.writeMessageToQueue("request", UUID.randomUUID().toString(), "One test", new TestCallback());

			i++;
		}
		
		Thread.sleep(20 * 1000);
		
		for(CubeKafkaRobot cubeKafkaRobot : cubeKafkaRobots)
			cubeKafkaRobot.stop();
		
		testRobotResponseConsumer.stop();
		
		return testRobotResponseConsumer;
	}

	private class TestCallback implements Callback {
	       @Override
	       public void onCompletion(RecordMetadata recordMetadata, Exception exception) {
	           if (exception != null) {
	        	   throw new IllegalStateException("Error while producing message to topic :" + recordMetadata);
	           } else {
	        	   logger.info(String.format("sent message to topic:%s partition:%s  offset:%s", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset()));
	           }
	       }
	   }

	public void testKafkaConfig() {
		
		List<String> requiredTopics = Arrays.asList("request,response".split(",")); 
		
		Consumer<String, String> consumer = CubeKafkaRobot.buildConsumer();
		Map<String, List<PartitionInfo>> topicsMap = consumer.listTopics();
		for(String topic : requiredTopics) {
			if( ! topicsMap.containsKey(topic))
				throw new IllegalStateException(String.format("RequiredTopic %s is missing", topic));
		}
		
		consumer.close();
	}
	
}
