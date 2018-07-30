package com.rubiks.objects;

import java.io.IOException;

import org.codehaus.jettison.json.JSONException;

import junit.framework.TestCase;

public class CubeMoveTest extends TestCase {

	public void testReferenceGame() throws IOException, JSONException {
		
		Cube cube = CubeFactory.createCube();
		
		CubeMove cubeMove = new CubeMove(null, null, null, CubeMagicMove.REFERENCE_CUBE_MIX);
		
		new CubeMoveHandler().executeMove(cube, cubeMove);
		
		assertFalse(cube.getCubeAnalysis().isFrontCrossDone());
		assertFalse(cube.getCubeAnalysis().isFirstFloorDone());
		assertFalse(cube.getCubeAnalysis().isSecondFloorDone());
		assertFalse(cube.getCubeAnalysis().isCubeDone());
		
		new CubeMagicMove().executeCubeMoveFromResourceJSON(cube, "reference_game_1.json");
		
		assertTrue(cube.getCubeAnalysis().isFrontCrossDone());
		assertTrue(cube.getCubeAnalysis().isFirstFloorDone());
		assertTrue(cube.getCubeAnalysis().isSecondFloorDone());
		assertTrue(cube.getCubeAnalysis().isCubeDone());
	}
}
