package com.rubiks.objects;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;

public class CubeMoveHandler {

	public void executeMove(Cube cube, CubeMove cubeMove) throws IOException, JSONException {
		if(StringUtils.isNotEmpty(cubeMove.getMagicMove()))
			new CubeMagicMove().executeMagicMode(cubeMove.getMagicMove(), cube);
		if(StringUtils.isNotEmpty(cubeMove.getAxe()))
			cube.move(cubeMove.getAxe(), cubeMove.getLevel(), cubeMove.getDirection());
	}
}
