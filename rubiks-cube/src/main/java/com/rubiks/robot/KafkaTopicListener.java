package com.rubiks.robot;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

import org.apache.log4j.Logger;

public abstract class KafkaTopicListener implements Runnable {
	
	protected Logger logger = Logger.getLogger(getClass());
	
	protected boolean isRunning = true;
	protected boolean isListening = false;

	protected final Cache responseFromKafkaCache;
	protected final String cacheName;
	
	
	public KafkaTopicListener() {

		cacheName = String.format("%s_%s", "responseFromKafkaCache", UUID.randomUUID().toString());
		
		CacheManager cm = CacheManager.getInstance();
		cm.addCache(cacheName);
		responseFromKafkaCache = cm.getCache(cacheName);
		
		CacheConfiguration config = responseFromKafkaCache.getCacheConfiguration();
		config.setTimeToIdleSeconds(60);
		config.setTimeToLiveSeconds(120);
		config.setMaxEntriesLocalHeap(5000l);
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
		logger.debug(String.format("testRobotResponseConsumer.isListening: %s", isListening()));
	}
	
	public boolean isListening() {
		return isListening;
	}
	
	public void stop() {
		isListening = false;
		isRunning = false;
	}
	
	protected void putKafkaReponse(String key, String value) {
		responseFromKafkaCache.put(new Element(key, value));
	}
	
	protected Map<String, String> getResponseFromKafkaMap() {
		Map<String, String> responseFromKafkaMap = new ConcurrentHashMap<String, String>();
		for(Object key : responseFromKafkaCache.getKeys()) {
			responseFromKafkaMap.put(key.toString(), responseFromKafkaCache.get(key).getObjectValue().toString());
		}
		return responseFromKafkaMap;
	}
}
