package com.common.kafka.tests;

import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class MainConsumerThread implements Runnable {

	// https://www.safaribooksonline.com/library/view/kafka-the-definitive/9781491936153/ch04.html
	// no point in adding more consumers than you have partitions in a topic—some of the consumers will just be idle
	
	 private final static String TOPIC = "request";
	 private final static String BOOTSTRAP_SERVERS ="mathias-virtual-machine:9092";
	 private static Properties props;
	  
    static {
    	props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaExampleConsumer");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    }
	
	public MainConsumerThread() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void run() {
		
		Consumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Pattern.compile(TOPIC));

		while (true) {
			ConsumerRecords<String, String> consumerRecords = consumer.poll(1000);

			if(! consumerRecords.isEmpty()) {
				for(ConsumerRecord<String, String> record : consumerRecords.records(TOPIC)) {
					System.out.println(Thread.currentThread() + " " + record.key() + " " + record.value());
				}
			}
			consumer.commitAsync();
		}
	}
}
