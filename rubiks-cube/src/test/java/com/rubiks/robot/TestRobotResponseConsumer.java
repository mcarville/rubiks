package com.rubiks.robot;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public class TestRobotResponseConsumer extends KafkaTopicListener {

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
					logger.debug(String.format("Record from topic (%s) %s => %s", topic, record.key(), record.value()));
					
					putKafkaReponse(record.key(), record.value());
					
					incrementReadMessagesCount();
				}
			}
			
			consumer.commitAsync();
		}
		
		consumer.close();
	}
	
	private synchronized void incrementReadMessagesCount() {
		READ_MESSAGES_COUNT++;
	}

	public static int getREAD_MESSAGES_COUNT() {
		return READ_MESSAGES_COUNT;
	}
	
	public int countOnMessagesByStatus(boolean onError) {
		int count = 0;
		for(String value : getResponseFromKafkaMap().values()) {
			CubeKafkaMessage cubeKafkaMessage = CubeKafkaMessage.fromJSON(value, CubeKafkaMessage.class);
			if(cubeKafkaMessage.getCubeTaskReport().isOnError() == onError)
				count++;
		}
		return count;
	}
}
