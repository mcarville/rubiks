package com.rubiks.robot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.log4j.Logger;

public class TestRobotResponseConsumer implements Runnable {
	
	protected Logger logger = Logger.getLogger(getClass());
	
	protected boolean isRunning = true;
	protected boolean isListening = false;
	
	protected Map<String,CubeKafkaMessage> responseMap = new HashMap<String, CubeKafkaMessage>();
	
	private static int READ_MESSAGES_COUNT = 0;
	
	@Override
	public void run() {
		String topic = "response";
		List<String> topics = Arrays.asList(new String[]{topic});
		Consumer<String, String> consumer = CubeKafkaRobot.buildConsumer(topics, UUID.randomUUID().toString());
		
		while(isRunning) {

			ConsumerRecords<String, String> records = consumer.poll(1000);
			
			isListening = true;
			
			if(! records.isEmpty()) {
				
				for(ConsumerRecord<String, String> record : records.records(topic)) {
					logger.info(String.format("Record from topic (%s) %s => %s", topic, record.key(), record.value()));
					
					responseMap.put(record.key(), CubeKafkaMessage.fromJSON(record.value(), CubeKafkaMessage.class));
					
					incrementReadMessagesCount();
				}
			}
			
			consumer.commitAsync();
		}
		
		consumer.close();
	}
	
	public void waitForListening() throws InterruptedException {
		int maxWaitingIterations = 30;
		int i = 0;
		while( ! isListening()) {
			if(i > maxWaitingIterations)
				throw new IllegalStateException(String.format("TestRobotResponseConsumer has waited for %s iterations and it is still not ready", i));
			Thread.sleep(1000);
			i++;
		}
		logger.info(String.format("testRobotResponseConsumer.isListening: %s", isListening()));
	}
	
	public boolean isListening() {
		return isListening;
	}
	
	public void stop() {
		isListening = false;
		isRunning = false;
	}
	
	public Map<String, CubeKafkaMessage> getResponseMap() {
		return responseMap;
	}
	
	private synchronized void incrementReadMessagesCount() {
		READ_MESSAGES_COUNT++;
	}

	public static int getREAD_MESSAGES_COUNT() {
		return READ_MESSAGES_COUNT;
	}
	
	public int countOnMessagesByStatus(boolean onError) {
		int count = 0;
		for(CubeKafkaMessage cubeKafkaMessage : responseMap.values()) {
			if(cubeKafkaMessage.getCubeTaskReport().isOnError() == onError)
				count++;
		}
		return count;
	}
}
