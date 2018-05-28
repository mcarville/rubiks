package com.rubiks.robot;

import java.io.IOException;
import java.util.ArrayList;
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
import com.rubiks.objects.CubeMagicMove;
import com.rubiks.objects.CubeMove;
import com.rubiks.utils.DockerKafkaTest;


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
	
	public void testMultiRobots() throws InterruptedException, ExecutionException, TimeoutException, JSONException, IOException, CloneNotSupportedException {
		List<CubeKafkaRobot> cubeKafkaRobots = startCubeRobots(3);
		
		for(int i = 0 ; i < 15 ; i++) {
			Cube startCube = CubeFactory.createCube();
			CubeMove cubeMove = CubeMagicMove.retrieveRamdomMove(); 

			KafkaRequester kafkaRequester = new KafkaRequester();
			CubeKafkaMessage cubeKafkaMessage = kafkaRequester.executeQuery(startCube, cubeMove);
			
			Cube fromRobotCube = cubeKafkaMessage.getCube();

			startCube.executeMove(cubeMove);

//			assertEquals(startCube.getCubeAnalysis(), fromRobotCube.getCubeAnalysis());
//			TODO: analyse why this is not working
			
			assertEquals(startCube, fromRobotCube);
		}
		
		stopCubeKafkaRobots(cubeKafkaRobots);
	}
	
	public void testMultiRobotsMultiPlayer() throws InterruptedException, ExecutionException, TimeoutException, JSONException {
		List<CubeKafkaRobot> cubeKafkaRobots = startCubeRobots(3);
		
		int numberOfPlayers = 5;
		int numberOfMoves = 15;
		List<KafkaRequesterTestPlayingThread> kafkaRequesterTestPlayingThreads = new ArrayList<KafkaRequesterTestPlayingThread>();
		ThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(numberOfPlayers);
		for(int i = 0 ; i < numberOfPlayers ; i++) {
			KafkaRequesterTestPlayingThread kafkaRequesterTestPlayingThread = new KafkaRequesterTestPlayingThread(numberOfMoves);
			kafkaRequesterTestPlayingThreads.add(kafkaRequesterTestPlayingThread);
			threadPoolExecutor.submit(kafkaRequesterTestPlayingThread);
		}
		
		while(areRunningWriteRequesterRobots(kafkaRequesterTestPlayingThreads)) {
			Thread.sleep(1000);
		}
		
		assertEquals(numberOfPlayers * numberOfMoves, countTestWriteRequesterValid(kafkaRequesterTestPlayingThreads));
		
		stopCubeKafkaRobots(cubeKafkaRobots);
	}

	private List<CubeKafkaRobot>  startCubeRobots(int cubeKafkaRobotNumber) {
		ThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(1 + cubeKafkaRobotNumber);
		return startCubeKafkaRobots(threadPoolExecutor, cubeKafkaRobotNumber);
	}
	
}
