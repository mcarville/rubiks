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
	
	protected Map<String,CubeKafkaMessage> responseMap = new HashMap<String, CubeKafkaMessage>();
	
	@Override
	public void run() {
		String topic = "response";
		List<String> topics = Arrays.asList(new String[]{topic});
		Consumer<String, String> consumer = CubeKafkaRobot.buildConsumer(topics, UUID.randomUUID().toString());
		
		
		while(isRunning) {

			ConsumerRecords<String, String> records = consumer.poll(1000);
			if(! records.isEmpty()) {
				
				for(ConsumerRecord<String, String> record : records.records(topic)) {
					logger.info(String.format("Record from topic (%s) %s => %s", topic, record.key(), record.value()));
					
					responseMap.put(record.key(), CubeKafkaMessage.fromJSON(record.value(), CubeKafkaMessage.class));
				}
			}
			
			consumer.commitAsync();
		}
		
		consumer.close();
	}
	
	public void stop() {
		isRunning = false;
	}
	
	public Map<String, CubeKafkaMessage> getResponseMap() {
		return responseMap;
	}
}
