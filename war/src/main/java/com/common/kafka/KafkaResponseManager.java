package com.common.kafka;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;

import com.rubiks.robot.CubeKafkaMessage;
import com.rubiks.robot.DockerKafkaUtils;
import com.rubiks.robot.KafkaTopicListener;

public class KafkaResponseManager extends KafkaTopicListener {

	protected static Logger LOGGER = Logger.getLogger(KafkaResponseManager.class);
	
	private KafkaResponseManager(){}
	private static KafkaResponseManager instance;
	private static Object initLock = new Object();
	
	private final static String RESPONSE_TOPIC = "response";
	private static final String REQUEST_TOPIC = "request";
	
	private final static String BOOTSTRAP_SERVERS = DockerKafkaUtils.buildBrokerServersConnectionString();
	private static Properties CONSUMER_PROPERTIES;
	private static Properties PRODUCER_PROPERTIES;
	
	private ExecutorService executor;
	private KafkaProducer<String, String> producer;
	
	static {
		
        PRODUCER_PROPERTIES = new Properties();
        PRODUCER_PROPERTIES.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        PRODUCER_PROPERTIES.put(ProducerConfig.ACKS_CONFIG, "all");
        PRODUCER_PROPERTIES.put(ProducerConfig.RETRIES_CONFIG, 0);
        PRODUCER_PROPERTIES.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        PRODUCER_PROPERTIES.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		
		CONSUMER_PROPERTIES = new Properties();
	    CONSUMER_PROPERTIES.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		CONSUMER_PROPERTIES.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaConsumer");
		CONSUMER_PROPERTIES.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		CONSUMER_PROPERTIES.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
	}
	
	public static KafkaResponseManager getInstance() throws InterruptedException {
		if(instance != null)
			return instance;
		
		synchronized (initLock) {
			if(instance == null) {
				KafkaResponseManager kafkaResponseManager = new KafkaResponseManager();
				Thread thread = new Thread(kafkaResponseManager);
				thread.start();
				
				kafkaResponseManager.waitForListening();
				
				kafkaResponseManager.executor = Executors.newFixedThreadPool(10);
				
				instance = kafkaResponseManager;
				
				LOGGER.info("KafkaResponseManager is instanciated");
			}
		}
		return instance;
	}

	public Future<String> retrieveKafkaResponse(final CubeKafkaMessage cubeKafkaMessage) throws InterruptedException, JSONException {

		final String queryId = UUID.randomUUID().toString();
		
		ProducerRecord<String, String> data = new ProducerRecord<String, String>(REQUEST_TOPIC, queryId, cubeKafkaMessage.toJSON().toString());
		
		if(producer == null)
			throw new IllegalStateException("KafkaResponseManager - producer can not be null");
		
		producer.send(data);	
		producer.flush();
		
		Callable<String> callable = new Callable<String>() {
			@Override
			public String call() throws Exception {

				logger.debug(String.format("Start waiting for response for query: %s", queryId));
				
				long waitStart = System.currentTimeMillis();
				while((System.currentTimeMillis() - waitStart) < (30 * 1000)) {
					String response = KafkaResponseManager.getInstance().getKafkaReponse(queryId);
					if(response != null) {
						logger.debug("Response is ready going to return jsonObject");
						return response;
					}
					else {
						logger.debug("No jsonObject found => Going to sleep");
						KafkaTopicListener.tryToSleep(50);
					}
				}
				throw new IllegalStateException(String.format("Can not get a jsonObject after waiting for %s ms", (System.currentTimeMillis() - waitStart)));
			}
		};
		
		if(executor == null)
			throw new IllegalStateException("KafkaResponseManager - executor can not be null");
		
		Future<String> future = executor.submit(callable);
		
		return future;
	}

	
	@Override
	public void run() {
		
		producer = new KafkaProducer<String, String>(PRODUCER_PROPERTIES);
		
		Consumer<String, String> consumer = new KafkaConsumer<>(CONSUMER_PROPERTIES);
		consumer.subscribe(Pattern.compile(RESPONSE_TOPIC));
		
		while(isRunning) {
			ConsumerRecords<String, String> records = consumer.poll(1000);

			isListening = true;
			
			if(! records.isEmpty()) {
				for(ConsumerRecord<String, String> record : records.records(RESPONSE_TOPIC)) {
					
					logger.debug(String.format("Record received with key: %s", record.key()));
					
					
					putKafkaReponse(record.key(), record.value());
				}
			}
			consumer.commitSync();
			KafkaTopicListener.tryToSleep(5);
		}
		
		producer.close();
		consumer.close();
	}
	
	
}
