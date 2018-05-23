package com.rubiks.command;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONObject;

import com.common.command.AbstractCommand;
import com.common.kafka.KafkaRequester;
import com.rubiks.objects.Cube;
import com.rubiks.objects.CubeAnalysis;
import com.rubiks.objects.CubeFactory;
import com.rubiks.objects.CubeMove;
import com.rubiks.robot.CubeKafkaMessage;

public class RetrieveCube extends AbstractCommand {

	private static final String CURRENT_CUBE_SESSION = "CURRENT_CUBE";
//	private static Cube DEV_CUBE = CubeFactory.createCube();
	
	
	@Override
	public String getCommandId() {
		return "RetrieveCube";
	}

	@Override
	public JSONObject executeCommand(Map<String, String[]> parameterMap, HttpServletRequest request, JSONObject jsonResponse) throws Exception {
		
//		String devMode = retrieveParamValue(parameterMap, "devMode", false);
		
		Cube cube = (Cube)request.getSession().getAttribute(CURRENT_CUBE_SESSION);
		if(cube == null) {
			cube = CubeFactory.createCube();
			request.getSession().setAttribute(CURRENT_CUBE_SESSION, cube);
			
			System.out.println("Initialize new Cube, to session: " + request.getSession());
		}

		addHttpParameters(request, jsonResponse);
		
		String axe = retrieveParamValue(parameterMap, "axe", false);
		String level = retrieveParamValue(parameterMap, "level", false);
		String direction = retrieveParamValue(parameterMap, "direction", false);
		
		String magicMove = retrieveParamValue(parameterMap, "magicMove", false);
		
		String highAvailabilityMode = retrieveParamValue(parameterMap, "highAvailability", false);
		
		CubeMove cubeMove = new CubeMove(axe, level, direction, magicMove);
		
		if(StringUtils.isEmpty(highAvailabilityMode)) {
			cube.executeMove(cubeMove);
			cube.setCubeAnalysis(CubeAnalysis.buildCubeAnalysis(cube));
		}
		else {
			KafkaRequester kafkaRequester = new KafkaRequester();
			JSONObject jsonObject = kafkaRequester.executeQuery(cube, cubeMove);
			
			CubeKafkaMessage cubeKafkaMessage = CubeKafkaMessage.fromJSON(jsonObject.toString(), CubeKafkaMessage.class);
			
			request.getSession().setAttribute(CURRENT_CUBE_SESSION, cubeKafkaMessage.getCube());
			
			jsonResponse.put("cube_kafka_message", cubeKafkaMessage.toJSON());
		}
		
		Cube responseCube =  (Cube)request.getSession().getAttribute(CURRENT_CUBE_SESSION);
		jsonResponse.put("cube_faces", responseCube.toCubeFacesJSON());
		
		return jsonResponse;
		
	}

//	public static void main (String args[]) throws JSONException {
//		
//		Cube cube = CubeFactory.createCube();
//		
//		System.out.println(cube.getSquares());
//	}

}