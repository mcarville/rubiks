package com.rubiks.robot;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;

public class TestWriteCallback implements Callback {

	protected Logger logger = Logger.getLogger(getClass());

	private static int COMPLETED_TASKS_COUNT = 0;

	@Override
	public void onCompletion(RecordMetadata recordMetadata, Exception exception) {
		if (exception != null) {
			throw new IllegalStateException(
					"Error while producing message to topic :" + recordMetadata);
		} else {
			logger.info(String.format(
					"sent message to topic:%s partition:%s  offset:%s", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset()));
		}
		incrementCompletedTaskCount();
	}
	
	private synchronized void incrementCompletedTaskCount() {
		COMPLETED_TASKS_COUNT++;
	}

	public static int getCOMPLETED_TASKS_COUNT() {
		return COMPLETED_TASKS_COUNT;
	}
}
