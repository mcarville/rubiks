package com.rubiks.command;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.common.command.AbstractCommand;
import com.rubiks.objects.Cube;
import com.rubiks.objects.CubeFactory;
import com.rubiks.objects.CubeMagicMove;

public class RetrieveCube extends AbstractCommand {

	private static final String CURRENT_CUBE_SESSION = "CURRENT_CUBE";
	private static Cube DEV_CUBE = CubeFactory.createCube();
	
	
	@Override
	public String getCommandId() {
		return "RetrieveCube";
	}

	@Override
	public JSONObject executeCommand(Map<String, String[]> parameterMap, HttpServletRequest request, JSONObject jsonResponse) throws Exception {
		
		String devMode = retrieveParamValue(parameterMap, "devMode", false);
		
		Cube cube = null;
		
		if(StringUtils.isNotEmpty(devMode)) {
			cube = DEV_CUBE;
		}
		else 
		{
			cube = (Cube)request.getSession().getAttribute(CURRENT_CUBE_SESSION);
			if(cube == null) {
				cube = CubeFactory.createCube();
				request.getSession().setAttribute(CURRENT_CUBE_SESSION, cube);
				
				System.out.println("Initialize new Cube, to session: " + request.getSession());
			}
		}

		addHttpParameters(request, jsonResponse);
		
		String axe = retrieveParamValue(parameterMap, "axe", false);
		String level = retrieveParamValue(parameterMap, "level", false);
		String direction = retrieveParamValue(parameterMap, "direction", false);
		
		String magicMove = retrieveParamValue(parameterMap, "magicMove", false);
		
		if(StringUtils.isNotEmpty(magicMove))
			new CubeMagicMove().executeMagicMode(magicMove, cube);
		if(StringUtils.isNotEmpty(axe))
			cube.move(axe, level, direction);
		
		jsonResponse.put("cube_faces", cube.toCubeFacesJSON());
		
		return jsonResponse;
		
	}

	public static void main (String args[]) throws JSONException {
		
		Cube cube = CubeFactory.createCube();
		
		System.out.println(cube.getSquares());
		// cube.move("vertical", "clockwise");
		
		System.out.println(cube.toCubeFacesJSON());
		
		
	}

}
