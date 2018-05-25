package com.rubiks.robot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public abstract class KafkaTopicListener implements Runnable {
	
	protected Logger logger = Logger.getLogger(getClass());
	
	protected boolean isRunning = true;
	protected boolean isListening = false;

	protected Map<String, String> responseFromKafkaMap = new ConcurrentHashMap<String, String>();
	
	public void waitForListening() throws InterruptedException {
		int maxWaitingIterations = 30;
		int i = 0;
		while( ! isListening()) {
			if(i > maxWaitingIterations)
				throw new IllegalStateException(String.format("TestRobotResponseConsumer has waited for %s iterations and it is still not ready", i));
			Thread.sleep(1000);
			i++;
		}
		logger.debug(String.format("testRobotResponseConsumer.isListening: %s", isListening()));
	}
	
	public boolean isListening() {
		return isListening;
	}
	
	public void stop() {
		isListening = false;
		isRunning = false;
	}
	
	protected Map<String, String> getResponseFromKafkaMap() {
		return responseFromKafkaMap;
	}
}
