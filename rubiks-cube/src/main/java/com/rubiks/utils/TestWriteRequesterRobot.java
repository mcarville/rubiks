package com.rubiks.utils;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;

import com.rubiks.objects.Cube;
import com.rubiks.objects.CubeFactory;
import com.rubiks.objects.CubeMove;
import com.rubiks.robot.CubeKafkaMessage;
import com.rubiks.robot.CubeKafkaRobot;
import com.rubiks.robot.TestWriteCallback;

public class TestWriteRequesterRobot implements Runnable {

	protected Logger logger = Logger.getLogger(getClass());
	
	protected final int todoRequestNumber;
	
	public TestWriteRequesterRobot(int todoRequestNumber) {
		super();
		this.todoRequestNumber = todoRequestNumber;
	}

	protected boolean isRunning = true;
	protected int validRequestCount = 0;
	protected int invalidRequestCount = 0;
	
	@Override
	public void run() {
		for(int i = 0 ; i < todoRequestNumber ; i++) {
			try {
	
				if(ThreadLocalRandom.current().nextInt(0, 100 + 1) % 2 == 0) {
					CubeKafkaRobot.writeMessageToQueue("request", UUID.randomUUID().toString(), "An Invalid query", new TestWriteCallback());
					invalidRequestCount++;
				}
				else {
					CubeKafkaRobot.writeMessageToQueue("request", UUID.randomUUID().toString(), retrieveValidQuery(), new TestWriteCallback());
					validRequestCount++;
				}
			} 
			catch (JSONException e) {
				logger.error(e.toString(), e);
			}
		}
		isRunning = false;
	}

	protected String retrieveValidQuery() throws JSONException {
		Cube cube = CubeFactory.createCube();
		
		CubeMove cubeMove = new CubeMove(null, null, null, "mixCube");
		return new CubeKafkaMessage(cube, cubeMove).toJSON().toString();
	}

	public boolean isRunning() {
		return isRunning;
	}
	
	public int getValidRequestCount() {
		return validRequestCount;
	}
	
	public int getInvalidRequestCount() {
		return invalidRequestCount;
	}
}
