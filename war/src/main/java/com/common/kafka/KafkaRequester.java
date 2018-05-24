package com.common.kafka;

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
import com.rubiks.objects.CubeMove;
import com.rubiks.robot.CubeKafkaMessage;
import com.rubiks.robot.DockerKafkaUtils;

public class KafkaRequester {

    private final static String BOOTSTRAP_SERVERS = DockerKafkaUtils.buildBrokerServersConnectionString();

    private static Properties properties;
    
    static {
        properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, 0);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    }
	
	public CubeKafkaMessage executeQuery(Cube cube, CubeMove cubeMove) throws InterruptedException, ExecutionException, TimeoutException, JSONException {
		String queryId = UUID.randomUUID().toString();
		
		CubeKafkaMessage cubeKafkaMessage = new CubeKafkaMessage(cube, cubeMove);
		
		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
		ProducerRecord<String, String> data = new ProducerRecord<String, String>("request", queryId, cubeKafkaMessage.toJSON().toString());
		producer.send(data);	
		producer.close();
		
		Future<JSONObject> future = KafkaResponseManager.getInstance().retrieveKafkaResponse(queryId);
		
		JSONObject jsonObject = future.get(30, TimeUnit.SECONDS);
		
		return CubeKafkaMessage.fromJSON(jsonObject.toString(), CubeKafkaMessage.class);
	}
	
}
