package com.common.kafka;

import java.net.UnknownHostException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.rubiks.objects.Cube;
import com.rubiks.objects.CubeFactory;
import com.rubiks.objects.CubeMove;
import com.rubiks.robot.CubeKafkaMessage;

public class KafkaRequester {

    private final static String BOOTSTRAP_SERVERS ="mathias-virtual-machine:9092";

    private static Properties properties;
    
    static {
        properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, 0);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    }
	
	public JSONObject executeQuery(Cube cube, CubeMove cubeMove) throws InterruptedException, ExecutionException, TimeoutException, JSONException {
		String queryId = UUID.randomUUID().toString();
		
		CubeKafkaMessage cubeKafkaMessage = new CubeKafkaMessage(cube, cubeMove);
		
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
		ProducerRecord<String, String> data = new ProducerRecord<String, String>("request", queryId, cubeKafkaMessage.toJSON().toString());
		producer.send(data);	
		producer.close();
		
		Future<JSONObject> future = KafkaResponseManager.getInstance().retrieveKafkaResponse(queryId);
		return future.get(30, TimeUnit.SECONDS);
	}
	
//	public static void main (String args[]) throws InterruptedException, ExecutionException, TimeoutException, JSONException, UnknownHostException {
//		Cube cube = CubeFactory.createCube();
//		CubeMove cubeMove = new CubeMove(null, null, null, "mixCube");
//		
//		System.out.println(new KafkaRequester().executeQuery(cube, cubeMove));
//		
//	}
}
