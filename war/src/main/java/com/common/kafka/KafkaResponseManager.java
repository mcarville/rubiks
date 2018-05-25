package com.common.kafka;

import java.util.Properties;
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
import org.apache.kafka.common.serialization.StringDeserializer;

import com.rubiks.robot.DockerKafkaUtils;
import com.rubiks.robot.KafkaTopicListener;

public class KafkaResponseManager extends KafkaTopicListener {

	private KafkaResponseManager(){}
	private static KafkaResponseManager instance;
	
	private final static String TOPIC = "response";
	private final static String BOOTSTRAP_SERVERS = DockerKafkaUtils.buildBrokerServersConnectionString();
	private static Properties properties;
	
	private ExecutorService executor;
	
	static {
		properties = new Properties();
	    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		properties.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaConsumer");
		properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
	}
	
	public synchronized static KafkaResponseManager getInstance() throws InterruptedException {
		if(instance == null) {
			instance = new KafkaResponseManager();
			Thread thread = new Thread(instance);
			thread.start();
			
			instance.waitForListening();
			
			instance.executor = Executors.newFixedThreadPool(10);
		}
		return instance;
	}

	public Future<String> retrieveKafkaResponse(final String queryId) throws InterruptedException {

		Callable<String> callable = new Callable<String>() {
			@Override
			public String call() throws Exception {

				logger.debug(String.format("Start waiting for response for query: %s", queryId));
				
				long waitStart = System.currentTimeMillis();
				while((System.currentTimeMillis() - waitStart) < (30 * 1000)) {
					String response = KafkaResponseManager.getInstance().getResponseFromKafkaMap().get(queryId);
					if(response != null) {
						KafkaResponseManager.getInstance().getResponseFromKafkaMap().remove(queryId);						
						logger.debug("Response is ready going to return jsonObject");
						return response;
					}
					else {
						logger.debug("No jsonObject found => Going to sleep");
						Thread.sleep(500);
					}
				}
				throw new IllegalStateException(String.format("Can not get a jsonObject after waiting for %s ms", (System.currentTimeMillis() - waitStart)));
			}
		};
		
		Future<String> future = executor.submit(callable);
		
		return future;
	}

	
	@Override
	public void run() {
		
		Consumer<String, String> consumer = new KafkaConsumer<>(properties);
		consumer.subscribe(Pattern.compile(TOPIC));
		
		while(isRunning) {
			ConsumerRecords<String, String> records = consumer.poll(1000);

			isListening = true;
			
			if(! records.isEmpty()) {
				for(ConsumerRecord<String, String> record : records.records(TOPIC)) {
					
					logger.debug(String.format("Record received with key: %s", record.key()));
					
					
					responseFromKafkaMap.put(record.key(), record.value());
				}
			}
			consumer.commitAsync();
		}
		
		consumer.close();
	}
	
	
}
