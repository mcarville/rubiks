package com.rubiks.robot;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.PartitionInfo;

import com.rubiks.utils.DockerKafkaTest;

public class CubeKafkaRobotTest extends DockerKafkaTest {



	public void testKafkaProducer() {
		CubeKafkaRobot.writeMessageToQueue("response", UUID.randomUUID().toString(), "One test", new TestWriteCallback());
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
		
		List<CubeKafkaRobot> cubeKafkaRobots = startCubeKafkaRobots(threadPoolExecutor, cubeKafkaRobotNumber);
		
		TestRobotResponseConsumer testRobotResponseConsumer = new TestRobotResponseConsumer();
		threadPoolExecutor.submit(testRobotResponseConsumer);
		
		while( ! testRobotResponseConsumer.isListening())
			Thread.sleep(1000);
		logger.info(String.format("testRobotResponseConsumer.isListening: %s", testRobotResponseConsumer.isListening()));
		
		int i = 0;
		while(i < todoRequestNumber) {
			CubeKafkaRobot.writeMessageToQueue("request", UUID.randomUUID().toString(), "One test", new TestWriteCallback());

			i++;
		}
		
		Thread.sleep(20 * 1000);
		
		logger.info(String.format("TestWriteCallback.getCOMPLETED_TASKS_COUNT(): %s", TestWriteCallback.getCOMPLETED_TASKS_COUNT()));
		
		logger.info(String.format("TestRobotResponseConsumer.getREAD_MESSAGES_COUNT(): %s", TestRobotResponseConsumer.getREAD_MESSAGES_COUNT()));
		
		stopCubeKafkaRobots(cubeKafkaRobots);
		
		testRobotResponseConsumer.stop();
		
		return testRobotResponseConsumer;
	}

	public void testKafkaConfig() {
		
		List<String> requiredTopics = Arrays.asList("request,response".split(",")); 
		
		Consumer<String, String> consumer = CubeKafkaRobot.buildConsumer();
		Map<String, List<PartitionInfo>> topicsMap = consumer.listTopics();
		
		logger.info(String.format("topicsMap: %s", topicsMap));
		
		for(String topic : requiredTopics) {
			assertTrue(topicsMap.containsKey(topic));
		}
		
		consumer.close();
	}
	
}
