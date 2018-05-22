package com.rubiks.robot;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

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

public class CubeKafkaRobot {

	// com.rubiks.robot.CubeKafkaRobot
	
	protected static Logger LOGGER = Logger.getLogger(CubeKafkaRobot.class);
	
    private final static String BOOTSTRAP_SERVERS ="127.0.0.1:9092";

    private static Properties consumerProperties;
    private static Properties producerProperties;

    static {
    	
		consumerProperties = new Properties();
	    consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaConsumer");
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    	
        producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        producerProperties.put(ProducerConfig.ACKS_CONFIG, "all");
        producerProperties.put(ProducerConfig.RETRIES_CONFIG, 0);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    }
	
	private static boolean isRunning = true;
	
	public static void main (String[] args) throws JSONException {
		
		Consumer<String, String> consumer = buildConsumer();
		
		while(isRunning) {
			
			ConsumerRecords<String, String> records = consumer.poll(1000);

			if(! records.isEmpty()) {
				for(ConsumerRecord<String, String> record : records.records("request")) {
					
					String queryId = record.key();
					CubeKafkaMessage responseCubeKafkaMessage = null;
					try {
						
						LOGGER.info(String.format("Receive query: %s", queryId));
						
						CubeKafkaMessage cubeKafkaMessage = CubeKafkaMessage.fromJSON(record.value(), CubeKafkaMessage.class);
						
						Cube cube = cubeKafkaMessage.getCube();
						CubeMove cubeMove = cubeKafkaMessage.getCubeMove();
						
						cube.executeMove(cubeMove);
						cube.setCubeAnalysis(CubeAnalysis.buildCubeAnalysis(cube));
						
						LOGGER.info(String.format("Put response: %s to queue", queryId));
						
						responseCubeKafkaMessage = new CubeKafkaMessage(cube, cubeMove);
						responseCubeKafkaMessage.setCubeTaskReport(buildCubeTaskReport(false));
					}
					catch (Exception e) {
						LOGGER.error(e.toString(), e);
						
						responseCubeKafkaMessage = new CubeKafkaMessage(null, null);
						responseCubeKafkaMessage.setCubeTaskReport(buildCubeTaskReport(true));
					}
					
					deliverResponse(queryId, responseCubeKafkaMessage.toJSON().toString());
				}
			}
			consumer.commitAsync();
		}
		
		consumer.close();
	}

	protected static Consumer<String, String> buildConsumer() {
		Collection<String> topics = new ArrayList<String>();
		topics.add("request");
		topics.add("response");
		return buildConsumer(topics);
	}
	
	protected static Consumer<String, String> buildConsumer(Collection<String> topics) {
		Consumer<String, String> consumer = new KafkaConsumer<>(consumerProperties);
		consumer.subscribe(topics);
		return consumer;
	}
	
	protected static void deliverResponse(String queryId, String response) {
		deliverResponse(queryId, response, null);
	}

	protected static void deliverResponse(String queryId, String response, Callback callback) {
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(producerProperties);
		ProducerRecord<String, String> data = new ProducerRecord<String, String>("response", queryId, response);
		if(callback == null)
			producer.send(data);
		else
			producer.send(data, callback);
		producer.close();
	}

	
	private static CubeTaskReport buildCubeTaskReport(boolean onError) {
		
		String hostname = null;
		try{
			hostname = InetAddress.getLocalHost().getHostName();
		}
		catch(UnknownHostException exception) {}
		
		return new CubeTaskReport(onError,
				ManagementFactory.getRuntimeMXBean().getName(), hostname);
	}
	
}
