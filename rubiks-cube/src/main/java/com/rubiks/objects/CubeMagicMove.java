package com.rubiks.objects;

import java.util.Random;

public class CubeMagicMove {

	public void executeMagicMode(String magicMove, Cube cube) {
		switch(magicMove){
			case "frontCrossSideReverse":
				executeFrontCrossSideReverse(cube);
				break;
			case "sexyMoveRight" :
				executeSexyMoveRight(cube);
				break;
			case "sexyMoveLeft" :
				executeSexyMoveLeft(cube);
				break;
			case "belgiumMoveLeft" :
				executeBelgiumMoveLeft(cube);
				break;
			case "belgiumMoveRight" :
				executeBelgiumMoveRight(cube);
				break;
			case "backCrossBuilding" :
				executeBackCrossBuilding(cube);
				break;
			case "backCrossOrienting" :
				executeBackCrossOrienting(cube);
				break;
			case "elevatorMove" :
				executeElevatorMove(cube);
				break;	
			case "doubleChairMove" :
				executeDoubleChairMove(cube);
				break;
			case "mixCube" :
				executeCubeMix(cube);
				break;
		}
		
	}

	private void executeFrontCrossSideReverse(Cube cube) {
		cube.move(Cube.VERTICAL_EAST_WEST, Cube.RIGHT, "reverse");
		cube.move(Cube.HORIZONTAL, "top", Cube.CLOCKWISE);
		cube.move(Cube.VERTICAL_NORTH_SOUTH, Cube.LEFT, "reverse");
		cube.move(Cube.HORIZONTAL, "top", "reverse");
	}
	
	private void executeSexyMoveRight(Cube cube) {
		cube.move(Cube.VERTICAL_EAST_WEST, Cube.RIGHT, Cube.CLOCKWISE);
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, "reverse");
		cube.move(Cube.VERTICAL_EAST_WEST, Cube.RIGHT, "reverse");
	}
	
	private void executeSexyMoveLeft(Cube cube) {
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, "reverse");
		cube.move(Cube.VERTICAL_NORTH_SOUTH, Cube.LEFT, Cube.CLOCKWISE);
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, Cube.CLOCKWISE);
		cube.move(Cube.VERTICAL_NORTH_SOUTH, Cube.LEFT, "reverse");
	}
	
	private void executeBelgiumMoveLeft(Cube cube) {
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, "reverse");
		cube.move(Cube.VERTICAL_NORTH_SOUTH, Cube.LEFT, Cube.CLOCKWISE);
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, Cube.CLOCKWISE);
		cube.move(Cube.VERTICAL_NORTH_SOUTH, Cube.LEFT, "reverse");
		
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, Cube.CLOCKWISE);
		cube.move(Cube.VERTICAL_EAST_WEST, Cube.RIGHT, "reverse");
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, "reverse");
		cube.move(Cube.VERTICAL_EAST_WEST, Cube.RIGHT, Cube.CLOCKWISE);
	}
	
	private void executeBelgiumMoveRight(Cube cube) {
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, Cube.CLOCKWISE);
		cube.move(Cube.VERTICAL_NORTH_SOUTH, Cube.RIGHT, Cube.CLOCKWISE);
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, "reverse");
		cube.move(Cube.VERTICAL_NORTH_SOUTH, Cube.RIGHT, "reverse");
		
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, "reverse");
		cube.move(Cube.VERTICAL_EAST_WEST, Cube.RIGHT, Cube.CLOCKWISE);
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, Cube.CLOCKWISE);
		cube.move(Cube.VERTICAL_EAST_WEST, Cube.RIGHT, "reverse");
	}
	
	private void executeBackCrossBuilding(Cube cube) {
		cube.move(Cube.VERTICAL_EAST_WEST, Cube.RIGHT, Cube.CLOCKWISE);
		cube.move(Cube.VERTICAL_NORTH_SOUTH, Cube.LEFT, Cube.CLOCKWISE);
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, "rev");
		
		cube.move(Cube.VERTICAL_NORTH_SOUTH, Cube.LEFT, "reverse");
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, Cube.CLOCKWISE);
		cube.move(Cube.VERTICAL_EAST_WEST, Cube.RIGHT, "reverse");
	}
	
	private void executeBackCrossOrienting(Cube cube) {
		cube.move(Cube.VERTICAL_NORTH_SOUTH, Cube.RIGHT, "reverse");
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, "rev");
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, "rev");
		cube.move(Cube.VERTICAL_NORTH_SOUTH, Cube.RIGHT, Cube.CLOCKWISE);
		
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, Cube.CLOCKWISE);
		cube.move(Cube.VERTICAL_NORTH_SOUTH, Cube.RIGHT, "reverse");
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, Cube.CLOCKWISE);
		cube.move(Cube.VERTICAL_NORTH_SOUTH, Cube.RIGHT, Cube.CLOCKWISE);
	}
	
	private void executeDoubleChairMove(Cube cube) {
		executeBackCrossOrienting(cube);
		
		cube.move(Cube.VERTICAL_NORTH_SOUTH, Cube.LEFT, "reverse");
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, "rev");
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, "rev");
		cube.move(Cube.VERTICAL_NORTH_SOUTH, Cube.LEFT, Cube.CLOCKWISE);
		
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, "rev");
		cube.move(Cube.VERTICAL_NORTH_SOUTH, Cube.LEFT, "reverse");
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, "rev");
		cube.move(Cube.VERTICAL_NORTH_SOUTH, Cube.LEFT, Cube.CLOCKWISE);
	}

	
	private void executeElevatorMove(Cube cube) {
		cube.move(Cube.VERTICAL_NORTH_SOUTH, Cube.RIGHT, Cube.CLOCKWISE);
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, "reverse");
		cube.move(Cube.VERTICAL_NORTH_SOUTH, Cube.LEFT, Cube.CLOCKWISE);
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, Cube.CLOCKWISE);
		
		cube.move(Cube.VERTICAL_NORTH_SOUTH, Cube.RIGHT, "reverse");
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, "reverse");
		cube.move(Cube.VERTICAL_NORTH_SOUTH, Cube.LEFT, "reverse");
		cube.move(Cube.HORIZONTAL, Cube.BOTTOM, Cube.CLOCKWISE);
	}
	
	
	private void executeCubeMix(Cube cube) {
		
		String[] AXES = {Cube.VERTICAL_EAST_WEST, Cube.VERTICAL_NORTH_SOUTH, Cube.HORIZONTAL};
		String[] VERTICAL_LEVELS = {Cube.VERTICAL_EAST_WEST, Cube.VERTICAL_NORTH_SOUTH, Cube.LEFT, Cube.CENTER, Cube.RIGHT};
		String[] HORIZONTAL_LEVELS = {Cube.HORIZONTAL, Cube.TOP, Cube.MIDDLE, Cube.BOTTOM};
		String[] DIRECTIONS = {Cube.CLOCKWISE, "reverse"};
		
		int moveCount = 100 + new Random().nextInt(50);
		for(int i = 0 ; i < moveCount; i++) {
			
			String axe = AXES[new Random().nextInt(AXES.length)];
			String level = axe.equals(Cube.HORIZONTAL) ? HORIZONTAL_LEVELS[new Random().nextInt(HORIZONTAL_LEVELS.length)]
					: VERTICAL_LEVELS[new Random().nextInt(VERTICAL_LEVELS.length)];
			cube.move(axe, level, DIRECTIONS[new Random().nextInt(DIRECTIONS.length)]);
		}
	}
}
