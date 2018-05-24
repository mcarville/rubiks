package com.common.kafka.tests;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class Main {

    private final static String TOPIC = "request";
    private final static String BOOTSTRAP_SERVERS ="127.0.0.1:9092";

    private static Properties props;
    
    static {
        props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    }
    
	public static void main(String[] args) {

		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);

		TestCallback callback = new TestCallback();
		ProducerRecord<String, String> data = new ProducerRecord<String, String>(TOPIC, "test", "test" );
		producer.send(data, callback);	
		producer.close();

		
	}
	
	private static class TestCallback implements Callback {
	       @Override
	       public void onCompletion(RecordMetadata recordMetadata, Exception exception) {
	           if (exception != null) {
	               System.out.println("Error while producing message to topic :" + recordMetadata);
	               exception.printStackTrace();
	           } else {
	               String message = String.format("sent message to topic:%s partition:%s  offset:%s", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
	               System.out.println(message);
	           }
	       }
	   }


}
