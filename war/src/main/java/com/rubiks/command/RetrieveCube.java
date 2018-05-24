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
		
		String cubeJSON = retrieveParamValue(parameterMap, "cubeJSON", false);

		Cube cube = null;
		if(StringUtils.isNotEmpty(cubeJSON))
			cube = Cube.fromJSON(cubeJSON, Cube.class);
		else
			cube = CubeFactory.createCube();
		
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
			
			CubeKafkaMessage cubeKafkaMessage = kafkaRequester.executeQuery(cube, cubeMove);
			
			jsonResponse.put("cube_kafka_message", cubeKafkaMessage.toJSON());
		}
		
		jsonResponse.put("cube_faces", cube.toCubeFacesJSON());
		jsonResponse.put("cubeJSON", cube.toJSON());
		
		return jsonResponse;
		
	}

//	public static void main (String args[]) throws JSONException {
//		
//		Cube cube = CubeFactory.createCube();
//		
//		System.out.println(cube.getSquares());
//	}

}
