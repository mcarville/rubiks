package com.rubiks.robot;

import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaMessageCommons {

	public static final String GUI_KAFKA_CLIENT_UUID = "gui-kafka-client-uuid";
	public static final String TIMESTAMP_MS = "timestamp-ms";

	public static void addCommonHeaders(ProducerRecord<String, String> data, String guiKafkaClientUuid) {
		data.headers().add(TIMESTAMP_MS, String.valueOf(System.currentTimeMillis()).getBytes());
		data.headers().add(GUI_KAFKA_CLIENT_UUID, guiKafkaClientUuid.getBytes());
	}
}
