package com.common.kafka;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
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
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.rubiks.robot.DockerKafkaUtils;

public class KafkaResponseManager implements Runnable {

	protected Logger logger = Logger.getLogger(getClass());
	
	private KafkaResponseManager(){}
	private static KafkaResponseManager instance;
	
	protected Map<String, JSONObject> responseFromKafkaMap = new ConcurrentHashMap<String, JSONObject>();
	
	private boolean isRunning = true;
	
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
	
	public synchronized static KafkaResponseManager getInstance() {
		if(instance == null) {
			instance = new KafkaResponseManager();
			Thread thread = new Thread(instance);
			thread.start();
			
			instance.executor = Executors.newFixedThreadPool(10);
		}
		return instance;
	}

	public Map<String, JSONObject> getResponseFromKafkaMap() {
		return responseFromKafkaMap;
	}
	
	public Future<JSONObject> retrieveKafkaResponse(final String queryId) throws InterruptedException {

		Callable<JSONObject> callable = new Callable<JSONObject>() {
			@Override
			public JSONObject call() throws Exception {

				logger.debug(String.format("Start waiting for response for query: %s", queryId));
				
				long waitStart = System.currentTimeMillis();
				while((System.currentTimeMillis() - waitStart) < (30 * 1000)) {
					JSONObject jsonObject = KafkaResponseManager.getInstance().getResponseFromKafkaMap().get(queryId);
					if(jsonObject != null) {
						KafkaResponseManager.getInstance().getResponseFromKafkaMap().remove(queryId);						
						logger.debug("Response is ready going to return jsonObject");
						return jsonObject;
					}
					else {
						logger.debug("No jsonObject found => Going to sleep");
						Thread.sleep(500);
					}
				}
				throw new IllegalStateException(String.format("Can not get a jsonObject after waiting for %s ms", (System.currentTimeMillis() - waitStart)));
			}
		};
		
		Future<JSONObject> future = executor.submit(callable);
		
		return future;
	}

	
	@Override
	public void run() {
		
		Consumer<String, String> consumer = new KafkaConsumer<>(properties);
		consumer.subscribe(Pattern.compile(TOPIC));
		
		while(isRunning) {
			ConsumerRecords<String, String> records = consumer.poll(1000);

			if(! records.isEmpty()) {
				for(ConsumerRecord<String, String> record : records.records(TOPIC)) {
					
					logger.debug(String.format("Record received with key: %s", record.key()));
					
					try {
						responseFromKafkaMap.put(record.key(), new JSONObject(record.value()));
					} catch (JSONException e) {
						logger.error(e.toString(), e);
					}
				}
			}
			consumer.commitAsync();
		}
		
		consumer.close();
	}
	
	
}
