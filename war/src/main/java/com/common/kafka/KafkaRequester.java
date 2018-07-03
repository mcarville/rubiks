package com.common.kafka;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.codehaus.jettison.json.JSONException;

import com.rubiks.objects.Cube;
import com.rubiks.objects.CubeMove;
import com.rubiks.robot.CubeKafkaMessage;

public class KafkaRequester {

	public CubeKafkaMessage executeQuery(Cube cube, CubeMove cubeMove) throws InterruptedException, ExecutionException, TimeoutException, JSONException {
		
		CubeKafkaMessage cubeKafkaMessage = new CubeKafkaMessage(cube, cubeMove);
		
		KafkaResponseManager kafkaResponseManager = KafkaResponseManager.getInstance();
		
		Future<String> future = kafkaResponseManager.retrieveKafkaResponse(cubeKafkaMessage);
		
		String response = future.get(30, TimeUnit.SECONDS);
		
		return CubeKafkaMessage.fromJSON(response, CubeKafkaMessage.class);
	}
	
}
