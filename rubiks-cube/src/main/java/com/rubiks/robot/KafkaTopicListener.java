package com.rubiks.robot;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public abstract class KafkaTopicListener implements Runnable {
	
	protected Logger logger = Logger.getLogger(getClass());
	protected static Logger LOGGER = Logger.getLogger(KafkaTopicListener.class);
	
	protected boolean isRunning = true;
	protected boolean isListening = false;

	protected final CacheManager cacheManager;
	protected final String cacheName;
	
	
	public KafkaTopicListener() {

		cacheName = String.format("%s_%s", "responseFromKafkaCache", UUID.randomUUID().toString());
		
		cacheManager = CacheManager.getInstance();
		cacheManager.addCache(cacheName);
		
		CacheConfiguration config = cacheManager.getCache(cacheName).getCacheConfiguration();
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
		cacheManager.getCache(cacheName).put(new Element(key, value));
	}
	
	protected String getKafkaReponse(String key) {
		Element element = cacheManager.getCache(cacheName).get(key);
		return (element != null && element.getObjectValue() != null
				? StringUtils.isNotEmpty(element.getObjectValue().toString())
				? element.getObjectValue().toString() : null : null);
	}
	
	protected Map<String, String> getResponseFromKafkaMap() {
		Map<String, String> responseFromKafkaMap = new ConcurrentHashMap<String, String>();
		for(Object key : cacheManager.getCache(cacheName).getKeys()) {
			responseFromKafkaMap.put(key.toString(), cacheManager.getCache(cacheName).get(key).getObjectValue().toString());
		}
		return responseFromKafkaMap;
	}
	
	public static void tryToSleep(long timeMs){
		try {
			Thread.sleep(timeMs);
		} catch (InterruptedException e) {
			LOGGER.error(e.toString(), e);
		}
	}
}
