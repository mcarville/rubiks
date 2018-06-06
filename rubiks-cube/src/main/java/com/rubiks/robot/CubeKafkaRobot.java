package com.rubiks.robot;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;

import com.rubiks.objects.Cube;
import com.rubiks.objects.CubeAnalysis;
import com.rubiks.objects.CubeMove;

public class CubeKafkaRobot implements Runnable {

	// com.rubiks.robot.CubeKafkaRobot
	
	protected static Logger LOGGER = Logger.getLogger(CubeKafkaRobot.class);
	
    private final static String BOOTSTRAP_SERVERS = DockerKafkaUtils.buildBrokerServersConnectionString();

    private static Properties DEFAULT_CONSUMER_PROPERTIES;
    private static Properties producerProperties;

    static {
    	
		DEFAULT_CONSUMER_PROPERTIES = new Properties();
	    DEFAULT_CONSUMER_PROPERTIES.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		DEFAULT_CONSUMER_PROPERTIES.put(ConsumerConfig.GROUP_ID_CONFIG, "CubeKafkaRobot");
		DEFAULT_CONSUMER_PROPERTIES.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		DEFAULT_CONSUMER_PROPERTIES.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    	
        producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        producerProperties.put(ProducerConfig.ACKS_CONFIG, "all");
        producerProperties.put(ProducerConfig.RETRIES_CONFIG, 0);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    }
	
	protected boolean isRunning = true;
	
	@Override
	public void run() {
		List<String> topics = Arrays.asList("request".split(","));
		Consumer<String, String> consumer = buildConsumer(topics);
		
		while(isRunning) {
			
			ConsumerRecords<String, String> records = consumer.poll(1000);

			if(! records.isEmpty()) {
				for(ConsumerRecord<String, String> record : records.records("request")) {
					
					String queryId = record.key();
					CubeKafkaMessage responseCubeKafkaMessage = null;
					try {
						
						LOGGER.debug(String.format("Receive query: %s", queryId));
						
						CubeKafkaMessage cubeKafkaMessage = CubeKafkaMessage.fromJSON(record.value(), CubeKafkaMessage.class);
						
						Cube cube = cubeKafkaMessage.getCube();
						CubeMove cubeMove = cubeKafkaMessage.getCubeMove();
						
						cube.executeMove(cubeMove);
						cube.setCubeAnalysis(CubeAnalysis.buildCubeAnalysis(cube));
						
						LOGGER.debug(String.format("Put response: %s to queue", queryId));
						
						responseCubeKafkaMessage = new CubeKafkaMessage(cube, cubeMove);
						responseCubeKafkaMessage.setCubeTaskReport(buildCubeTaskReport(false));
					}
					catch (Exception e) {
						LOGGER.debug(e.toString(), e);
						
						responseCubeKafkaMessage = new CubeKafkaMessage(null, null);
						responseCubeKafkaMessage.setCubeTaskReport(buildCubeTaskReport(true));
					}
					
					try {
						writeMessageToQueue("response", queryId, responseCubeKafkaMessage.toJSON().toString());
					} catch (JSONException e) {
						LOGGER.error(e.toString(), e);
					}
				}
			}
			consumer.commitAsync();
		}
		
		consumer.close();
	}

	public void stop() {
		isRunning = false;
	}
	
	public static void main (String[] args) {
		CubeKafkaRobot cubeKafkaRobot = new CubeKafkaRobot();
		cubeKafkaRobot.run();
	}

	protected static Consumer<String, String> buildConsumer() {
		Collection<String> topics = new ArrayList<String>();
		
		return buildConsumer(topics);
	}
	
	protected static Consumer<String, String> buildConsumer(Collection<String> topics) {
		return buildConsumer(topics, null);
	}
	
	protected static Consumer<String, String> buildConsumer(Collection<String> topics, String groupId) {
		
		Properties consumerProperties = (Properties)DEFAULT_CONSUMER_PROPERTIES.clone();
		if(StringUtils.isNotEmpty(groupId))
			consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		
		Consumer<String, String> consumer = new KafkaConsumer<>(consumerProperties);
		consumer.subscribe(topics);
		return consumer;	
	}
	
	public static void writeMessageToQueue(String topic, String queryId, String response) {
		writeMessageToQueue(topic, queryId, response, null);
	}

	public static void writeMessageToQueue(String topic, String queryId, String response, Callback callback) {
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(producerProperties);
		ProducerRecord<String, String> data = new ProducerRecord<String, String>(topic, queryId, response);
		if(callback == null)
			producer.send(data);
		else
			producer.send(data, callback);
		
		LOGGER.debug(String.format("Write to topic: '%s' message: [%s => %s]", topic, queryId, response.length()));
		
		producer.close();
	}

	private static String DOCKER_ALIAS;
	
	private static CubeTaskReport buildCubeTaskReport(boolean onError) {
		
		String hostname = null;
		try{
			hostname = InetAddress.getLocalHost().getHostName();
		}
		catch(UnknownHostException exception) {}
		
		String dockerAlias = System.getenv().get("DOCKER_ALIAS");
		if(StringUtils.isEmpty(dockerAlias)) {
			if(StringUtils.isEmpty(DOCKER_ALIAS))
				DOCKER_ALIAS = UUID.randomUUID().toString().substring(0, 7);
			
			dockerAlias = DOCKER_ALIAS;
		}
		
		return new CubeTaskReport(onError,
				ManagementFactory.getRuntimeMXBean().getName(), hostname, dockerAlias);
	}
	
}
