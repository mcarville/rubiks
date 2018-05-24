package com.rubiks.robot;

import java.util.ArrayList;
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
		
		executeConsumeAndProduceTest(todoRequestNumber, 1, 2);
	}
	
	public void testConsumeAndProduceTwoRobot() throws InterruptedException {
		
		int todoRequestNumber = 10;
		
		executeConsumeAndProduceTest(todoRequestNumber, 2, 4);
	}
	
	public void testConsumeAndProduceFiveRobot() throws InterruptedException {
		
		int todoRequestNumber = 10;
		
		executeConsumeAndProduceTest(todoRequestNumber, 5, 10);
	}

	protected void executeConsumeAndProduceTest(int todoRequestNumber, int cubeKafkaRobotNumber, int requesterNumber) throws InterruptedException {
		ThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(1 + cubeKafkaRobotNumber + requesterNumber);
		
		List<CubeKafkaRobot> cubeKafkaRobots = startCubeKafkaRobots(threadPoolExecutor, cubeKafkaRobotNumber);
		
		TestRobotResponseConsumer testRobotResponseConsumer = new TestRobotResponseConsumer();
		threadPoolExecutor.submit(testRobotResponseConsumer);
		
		testRobotResponseConsumer.waitForListening();
		
		List<TestWriteRequesterRobot> testWriteRequesterRobots = startWriteRequesters(threadPoolExecutor, requesterNumber, todoRequestNumber);
		
		while(areRunningWriteRequesterRobots(testWriteRequesterRobots)) {
			Thread.sleep(1000);
		}
		
		for(int i = 0 ; i < 20 ; i++) {
			Thread.sleep(1000);
			if(TestWriteCallback.getCOMPLETED_TASKS_COUNT() == TestRobotResponseConsumer.getREAD_MESSAGES_COUNT())
				break;
		}
		
		logger.info(String.format("TestWriteCallback.getCOMPLETED_TASKS_COUNT(): %s", TestWriteCallback.getCOMPLETED_TASKS_COUNT()));
		
		logger.info(String.format("TestRobotResponseConsumer.getREAD_MESSAGES_COUNT(): %s", TestRobotResponseConsumer.getREAD_MESSAGES_COUNT()));
		
		stopCubeKafkaRobots(cubeKafkaRobots);
		
		testRobotResponseConsumer.stop();
		
		assertEquals(todoRequestNumber * requesterNumber, testRobotResponseConsumer.getResponseMap().size());
		
		assertEquals(countTestWriteRequesterValid(testWriteRequesterRobots), testRobotResponseConsumer.countOnMessagesByStatus(false));
		
		assertEquals(countTestWriteRequesterInvalid(testWriteRequesterRobots), testRobotResponseConsumer.countOnMessagesByStatus(true));
	}

	private int countTestWriteRequesterValid(List<TestWriteRequesterRobot> testWriteRequesterRobots) {
		int count = 0;

		for(TestWriteRequesterRobot testWriteRequesterRobot : testWriteRequesterRobots) {
			count += testWriteRequesterRobot.getValidRequestCount();
		}
		return count;
	}
	
	private int countTestWriteRequesterInvalid(List<TestWriteRequesterRobot> testWriteRequesterRobots) {
		int count = 0;
		for(TestWriteRequesterRobot testWriteRequesterRobot : testWriteRequesterRobots) {
			count += testWriteRequesterRobot.getInvalidRequestCount();
		}
		return count;
	}
	
	protected boolean areRunningWriteRequesterRobots(List<TestWriteRequesterRobot> testWriteRequesterRobots){
		for(TestWriteRequesterRobot testWriteRequesterRobot : testWriteRequesterRobots) {
			if(testWriteRequesterRobot.isRunning())
				return true;
		}
		return false;
	}

	public List<TestWriteRequesterRobot> startWriteRequesters(ThreadPoolExecutor threadPoolExecutor, int todoRequestNumber, int requesterNumber) {
		List<TestWriteRequesterRobot> testWriteRequesterRobots = new ArrayList<TestWriteRequesterRobot>();
		for(int i = 0 ; i < requesterNumber ; i++) {
			TestWriteRequesterRobot testWriteRequesterRobot = new TestWriteRequesterRobot(todoRequestNumber);
			threadPoolExecutor.submit(testWriteRequesterRobot);
			testWriteRequesterRobots.add(testWriteRequesterRobot);
		}
		return testWriteRequesterRobots;
	}
	
//	public void testKafkaConfig() {
//		
//		List<String> requiredTopics = Arrays.asList("request,response".split(",")); 
//		
//		Consumer<String, String> consumer = CubeKafkaRobot.buildConsumer(requiredTopics);
//		Map<String, List<PartitionInfo>> topicsMap = consumer.listTopics();
//		
//		logger.info(String.format("topicsMap: %s", topicsMap));
//		
//		for(String topic : requiredTopics) {
//			assertTrue(topicsMap.containsKey(topic));
//		}
//		
//		consumer.close();
//	}
	
}
