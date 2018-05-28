package com.rubiks.objects;

import java.io.IOException;

import org.codehaus.jettison.json.JSONException;

import junit.framework.TestCase;

public class CubeAnalysisTest extends TestCase {

	public void testCubeAnalysis() throws IOException, JSONException {
		
		Cube cube = CubeFactory.createCube();
		
		assertTrue(cube.getCubeAnalysis().isFrontCrossDone());
		assertTrue(cube.getCubeAnalysis().isFirstFloorDone());
		assertTrue(cube.getCubeAnalysis().isSecondFloorDone());
		assertTrue(cube.getCubeAnalysis().isCubeDone());
		
		CubeMove cubeMove = new CubeMove(null, null, null, CubeMagicMove.REFERENCE_CUBE_MIX);
		
		cube.executeMove(cubeMove);
		
		assertFalse(cube.getCubeAnalysis().isFrontCrossDone());
		assertFalse(cube.getCubeAnalysis().isFirstFloorDone());
		assertFalse(cube.getCubeAnalysis().isSecondFloorDone());
		assertFalse(cube.getCubeAnalysis().isCubeDone());
	}
}
