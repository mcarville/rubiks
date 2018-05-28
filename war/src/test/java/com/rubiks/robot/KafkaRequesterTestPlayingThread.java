package com.rubiks.robot;

import com.common.kafka.KafkaRequester;
import com.rubiks.objects.Cube;
import com.rubiks.objects.CubeFactory;
import com.rubiks.objects.CubeMagicMove;
import com.rubiks.objects.CubeMove;
import com.rubiks.utils.TestWriteRequesterRobot;

public class KafkaRequesterTestPlayingThread extends TestWriteRequesterRobot {

	public KafkaRequesterTestPlayingThread(int todoRequestNumber) {
		super(todoRequestNumber);
	}

	@Override
	public void run() {
		try {
			Cube cube = CubeFactory.createCube();
			
			for(int i = 0 ; i < todoRequestNumber ; i++) {
				
				CubeMove cubeMove = CubeMagicMove.retrieveRamdomMove();
				
				KafkaRequester kafkaRequester = new KafkaRequester();
				CubeKafkaMessage cubeKafkaMessage;
			
				cubeKafkaMessage = kafkaRequester.executeQuery(cube, cubeMove);
				
				cube.executeMove(cubeMove);
				
				if( ! cube.equals(cubeKafkaMessage.getCube())) {
					
					logger.error(cube.getSquares());
					logger.error(cubeKafkaMessage.toJSON());
					
					logger.error(String.format("cube: %s", cube.getCubeAnalysis()));
					logger.error(String.format("cubeKafkaMessage.getCube(): %s", cubeKafkaMessage.getCube().getCubeAnalysis()));
					
					throw new IllegalStateException("Incoherence between local Cube and the one done by CubeRobot");
				}
				
				validRequestCount++;
			}
		}		
		catch (Exception e) {
			logger.error(e.toString(), e);
		}
		
		isRunning = false;
	}

}
