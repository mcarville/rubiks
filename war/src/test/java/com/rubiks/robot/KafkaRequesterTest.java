package com.rubiks.robot;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

import org.codehaus.jettison.json.JSONException;

import com.common.kafka.KafkaRequester;
import com.rubiks.objects.Cube;
import com.rubiks.objects.Cube.AXE;
import com.rubiks.objects.CubeFactory;
import com.rubiks.objects.CubeMove;


public class KafkaRequesterTest extends DockerKafkaTest {

	public void testRequesterValid() throws InterruptedException, ExecutionException, TimeoutException, JSONException {
	
		CubeMove cubeMove = new CubeMove(null, null, null, "mixCube");
		
		CubeKafkaMessage cubeKafkaMessage = executeCubeMoveRequest(cubeMove);
		
		assertFalse(cubeKafkaMessage.getCubeTaskReport().isOnError());
	}

	public void testRequesterInvalid() throws InterruptedException, ExecutionException, TimeoutException, JSONException {
		
		CubeMove cubeMove = new CubeMove(AXE.HORIZONTAL, "bad_level", "", null);
		
		CubeKafkaMessage cubeKafkaMessage = executeCubeMoveRequest(cubeMove);
		
		assertTrue(cubeKafkaMessage.getCubeTaskReport().isOnError());
	}
	
	protected CubeKafkaMessage executeCubeMoveRequest(CubeMove cubeMove) throws InterruptedException, ExecutionException, TimeoutException, JSONException {
		List<CubeKafkaRobot> cubeKafkaRobots = startCubeRobots(1);
		
		KafkaRequester kafkaRequester = new KafkaRequester();
		
		Cube cube = CubeFactory.createCube();
		
		CubeKafkaMessage cubeKafkaMessage = kafkaRequester.executeQuery(cube, cubeMove);
		
		stopCubeKafkaRobots(cubeKafkaRobots);
		
		return cubeKafkaMessage;
	}

	private List<CubeKafkaRobot>  startCubeRobots(int cubeKafkaRobotNumber) {
		ThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(1 + cubeKafkaRobotNumber);
		return startCubeKafkaRobots(threadPoolExecutor, cubeKafkaRobotNumber);
	}
	
}
