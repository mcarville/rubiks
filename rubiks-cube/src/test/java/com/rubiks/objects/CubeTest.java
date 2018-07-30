package com.rubiks.objects;

import java.io.IOException;

import junit.framework.TestCase;

import org.codehaus.jettison.json.JSONException;

public class CubeTest extends TestCase {

	public void testToCubeFaces() throws JSONException, IOException {
		Cube cube = CubeFactory.createCube();
		Cube copyCube = Cube.fromJSON(cube.toJSON().toString(), Cube.class);
		cube.toCubeFaces();
		
		assertEquals(copyCube, cube);
		
		CubeMove cubeMove = new CubeMove(null, null, null, CubeMagicMove.REFERENCE_CUBE_MIX);		
		new CubeMoveHandler().executeMove(cube, cubeMove);
		copyCube = Cube.fromJSON(cube.toJSON().toString(), Cube.class);
		
		cube.toCubeFaces();
		
		assertEquals(copyCube, cube);
	}
}
