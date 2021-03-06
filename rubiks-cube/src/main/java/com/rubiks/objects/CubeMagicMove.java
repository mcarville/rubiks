package com.rubiks.objects;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

public class CubeMagicMove {

	public static final String BELGIUM_MOVE_LEFT = "belgiumMoveLeft";
	public static final String BELGIUM_MOVE_RIGHT = "belgiumMoveRight";
	public static final String BACK_CROSS_BUILDING = "backCrossBuilding";
	public static final String BACK_CROSS_ORIENTING = "backCrossOrienting";
	public static final String ELEVATOR_MOVE = "elevatorMove";
	public static final String DOUBLE_CHAIR_MOVE = "doubleChairMove";
	public static final String MIX_CUBE = "mixCube";
	public static final String FRONT_CROSS_SIDE_REVERSE = "frontCrossSideReverse";
	public static final String SEXY_MOVE_RIGHT = "sexyMoveRight";
	public static final String SEXY_MOVE_LEFT = "sexyMoveLeft";
	public static final String REFERENCE_CUBE_MIX = "referenceCubeMix";
	
	public void executeMagicMode(String magicMove, Cube cube) throws IOException, JSONException {
		switch(magicMove){
			case FRONT_CROSS_SIDE_REVERSE:
				executeFrontCrossSideReverse(cube);
				break;
			case SEXY_MOVE_RIGHT :
				executeSexyMoveRight(cube);
				break;
			case SEXY_MOVE_LEFT :
				executeSexyMoveLeft(cube);
				break;
			case BELGIUM_MOVE_LEFT :
				executeBelgiumMoveLeft(cube);
				break;
			case BELGIUM_MOVE_RIGHT :
				executeBelgiumMoveRight(cube);
				break;
			case BACK_CROSS_BUILDING :
				executeBackCrossBuilding(cube);
				break;
			case BACK_CROSS_ORIENTING :
				executeBackCrossOrienting(cube);
				break;
			case ELEVATOR_MOVE :
				executeElevatorMove(cube);
				break;	
			case DOUBLE_CHAIR_MOVE :
				executeDoubleChairMove(cube);
				break;
			case MIX_CUBE :
				executeCubeMix(cube);
				break;
			case REFERENCE_CUBE_MIX :
				executeReferenceGameMix(cube);
				break;
		}
		
	}

	private void executeFrontCrossSideReverse(Cube cube) {
		cube.move(Cube.AXE.VERTICAL_EAST_WEST, Cube.RIGHT, "reverse");
		cube.move(Cube.AXE.HORIZONTAL, "top", Cube.CLOCKWISE);
		cube.move(Cube.AXE.VERTICAL_NORTH_SOUTH, Cube.LEFT, "reverse");
		cube.move(Cube.AXE.HORIZONTAL, "top", "reverse");
	}
	
	private void executeSexyMoveRight(Cube cube) {
		cube.move(Cube.AXE.VERTICAL_EAST_WEST, Cube.RIGHT, Cube.CLOCKWISE);
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, "reverse");
		cube.move(Cube.AXE.VERTICAL_EAST_WEST, Cube.RIGHT, "reverse");
	}
	
	private void executeSexyMoveLeft(Cube cube) {
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, "reverse");
		cube.move(Cube.AXE.VERTICAL_NORTH_SOUTH, Cube.LEFT, Cube.CLOCKWISE);
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, Cube.CLOCKWISE);
		cube.move(Cube.AXE.VERTICAL_NORTH_SOUTH, Cube.LEFT, "reverse");
	}
	
	private void executeBelgiumMoveLeft(Cube cube) {
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, "reverse");
		cube.move(Cube.AXE.VERTICAL_NORTH_SOUTH, Cube.LEFT, Cube.CLOCKWISE);
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, Cube.CLOCKWISE);
		cube.move(Cube.AXE.VERTICAL_NORTH_SOUTH, Cube.LEFT, "reverse");
		
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, Cube.CLOCKWISE);
		cube.move(Cube.AXE.VERTICAL_EAST_WEST, Cube.RIGHT, "reverse");
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, "reverse");
		cube.move(Cube.AXE.VERTICAL_EAST_WEST, Cube.RIGHT, Cube.CLOCKWISE);
	}
	
	private void executeBelgiumMoveRight(Cube cube) {
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, Cube.CLOCKWISE);
		cube.move(Cube.AXE.VERTICAL_NORTH_SOUTH, Cube.RIGHT, Cube.CLOCKWISE);
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, "reverse");
		cube.move(Cube.AXE.VERTICAL_NORTH_SOUTH, Cube.RIGHT, "reverse");
		
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, "reverse");
		cube.move(Cube.AXE.VERTICAL_EAST_WEST, Cube.RIGHT, Cube.CLOCKWISE);
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, Cube.CLOCKWISE);
		cube.move(Cube.AXE.VERTICAL_EAST_WEST, Cube.RIGHT, "reverse");
	}
	
	private void executeBackCrossBuilding(Cube cube) {
		cube.move(Cube.AXE.VERTICAL_EAST_WEST, Cube.RIGHT, Cube.CLOCKWISE);
		cube.move(Cube.AXE.VERTICAL_NORTH_SOUTH, Cube.LEFT, Cube.CLOCKWISE);
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, "rev");
		
		cube.move(Cube.AXE.VERTICAL_NORTH_SOUTH, Cube.LEFT, "reverse");
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, Cube.CLOCKWISE);
		cube.move(Cube.AXE.VERTICAL_EAST_WEST, Cube.RIGHT, "reverse");
	}
	
	private void executeBackCrossOrienting(Cube cube) {
		cube.move(Cube.AXE.VERTICAL_NORTH_SOUTH, Cube.RIGHT, "reverse");
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, "rev");
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, "rev");
		cube.move(Cube.AXE.VERTICAL_NORTH_SOUTH, Cube.RIGHT, Cube.CLOCKWISE);
		
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, Cube.CLOCKWISE);
		cube.move(Cube.AXE.VERTICAL_NORTH_SOUTH, Cube.RIGHT, "reverse");
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, Cube.CLOCKWISE);
		cube.move(Cube.AXE.VERTICAL_NORTH_SOUTH, Cube.RIGHT, Cube.CLOCKWISE);
	}
	
	private void executeDoubleChairMove(Cube cube) {
		executeBackCrossOrienting(cube);
		
		cube.move(Cube.AXE.VERTICAL_NORTH_SOUTH, Cube.LEFT, "reverse");
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, "rev");
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, "rev");
		cube.move(Cube.AXE.VERTICAL_NORTH_SOUTH, Cube.LEFT, Cube.CLOCKWISE);
		
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, "rev");
		cube.move(Cube.AXE.VERTICAL_NORTH_SOUTH, Cube.LEFT, "reverse");
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, "rev");
		cube.move(Cube.AXE.VERTICAL_NORTH_SOUTH, Cube.LEFT, Cube.CLOCKWISE);
	}

	
	private void executeElevatorMove(Cube cube) {
		cube.move(Cube.AXE.VERTICAL_NORTH_SOUTH, Cube.RIGHT, Cube.CLOCKWISE);
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, "reverse");
		cube.move(Cube.AXE.VERTICAL_NORTH_SOUTH, Cube.LEFT, Cube.CLOCKWISE);
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, Cube.CLOCKWISE);
		
		cube.move(Cube.AXE.VERTICAL_NORTH_SOUTH, Cube.RIGHT, "reverse");
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, "reverse");
		cube.move(Cube.AXE.VERTICAL_NORTH_SOUTH, Cube.LEFT, "reverse");
		cube.move(Cube.AXE.HORIZONTAL, Cube.BOTTOM, Cube.CLOCKWISE);
	}
	
	private  final static String[] AXES = {Cube.AXE.VERTICAL_EAST_WEST, Cube.AXE.VERTICAL_NORTH_SOUTH, Cube.AXE.HORIZONTAL};
	private  final static String[] VERTICAL_LEVELS = {Cube.AXE.VERTICAL_EAST_WEST, Cube.AXE.VERTICAL_NORTH_SOUTH, Cube.LEFT, Cube.CENTER, Cube.RIGHT};
	private  final static String[] HORIZONTAL_LEVELS = {Cube.AXE.HORIZONTAL, Cube.TOP, Cube.MIDDLE, Cube.BOTTOM};
	private  final static String[] DIRECTIONS = {Cube.CLOCKWISE, "reverse"};
	
	public static CubeMove retrieveRamdomMove() {
		String axe = AXES[new Random().nextInt(AXES.length)];
		String level = axe.equals(Cube.AXE.HORIZONTAL) ? HORIZONTAL_LEVELS[new Random().nextInt(HORIZONTAL_LEVELS.length)]
				: VERTICAL_LEVELS[new Random().nextInt(VERTICAL_LEVELS.length)];
		return new CubeMove(axe, level, DIRECTIONS[new Random().nextInt(DIRECTIONS.length)], null);
	}
	
	private void executeCubeMix(Cube cube) throws IOException, JSONException {		
		int moveCount = 100 + new Random().nextInt(50);
		for(int i = 0 ; i < moveCount; i++) {

			CubeMove cubeMove = retrieveRamdomMove();
			new CubeMoveHandler().executeMove(cube, cubeMove);
		}
	}
	
	private void executeReferenceGameMix(Cube cube) throws IOException, JSONException {
		Cube initialCube = CubeFactory.createCube();
		for(int i = 0 ; i < initialCube.getSquares().size() ; i++) {
			cube.getSquares().set(i, initialCube.getSquares().get(i));
		}
		
		executeCubeMoveFromResourceJSON(cube, "reference_mix.json");
	}

	public void executeCubeMoveFromResourceJSON(Cube cube, String jsonFileName) throws IOException, JSONException {

		InputStream in = this.getClass().getClassLoader().getResourceAsStream(jsonFileName);
                		
		String jsonContent = IOUtils.toString(in, Charset.defaultCharset());
		JSONArray jsonArray = new JSONArray(jsonContent);
		for(int i = 0 ; i < jsonArray.length() ; i++) {
			CubeMove cubeMove = CubeMove.fromJSON(jsonArray.getString(i), CubeMove.class);
			new CubeMoveHandler().executeMove(cube, cubeMove);
		}
	}
}
