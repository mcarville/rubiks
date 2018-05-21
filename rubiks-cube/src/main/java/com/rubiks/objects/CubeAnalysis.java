package com.rubiks.objects;


public class CubeAnalysis extends AbstractJSONSerialiazer {

	protected final boolean frontCrossDone;
	protected final boolean firstFloorDone;
	protected final boolean secondFloorDone;
	
	public CubeAnalysis(boolean frontCrossDone, boolean firstFloorDone, boolean secondFloorDone) {
		super();
		this.frontCrossDone = frontCrossDone;
		this.firstFloorDone = firstFloorDone;
		this.secondFloorDone = secondFloorDone;
	}

	public static CubeAnalysis buildCubeAnalysis(Cube cube) {
		boolean frontCrossDone = isFrontCrossDone(cube);
		boolean firstFloorDone = isFirstFloorDone(cube);
		boolean secondFloorDone = isSecondFloorDone(cube);
		
		return new CubeAnalysis(frontCrossDone, firstFloorDone, secondFloorDone);
	}
	
	private static boolean isFrontCrossDone(Cube cube) {
		int[] CROSS_INDEXES = {1, 3, 4, 5, 7};
		return haveAllSquareFacesTheSameColor(cube, CROSS_INDEXES, Cube.FRONT);
	}
	
	private static boolean isFirstFloorDone(Cube cube) {
		int[] INDEXES = {0, 1, 2, 3, 4, 5, 6, 7, 8};
		return haveAllSquareFacesTheSameColor(cube, INDEXES, Cube.FRONT);
	}
	
	private static boolean isSecondFloorDone(Cube cube) {

		int[] NORTH_INDEXES = {0, 1, 2, 9, 10, 11};
		int[] EAST_INDEXES = {2, 5, 8, 11, 14, 17};
		int[] SOUTH_INDEXES = {6, 7, 8, 15, 16, 17};
		int[] WEST_INDEXES = {0, 3, 6, 9, 12, 15};
		
		return isFirstFloorDone(cube) && haveAllSquareFacesTheSameColor(cube, NORTH_INDEXES, Cube.NORTH)
				&& haveAllSquareFacesTheSameColor(cube, EAST_INDEXES, Cube.EAST)
				&& haveAllSquareFacesTheSameColor(cube, SOUTH_INDEXES, Cube.SOUTH)
				&& haveAllSquareFacesTheSameColor(cube, WEST_INDEXES, Cube.WEST);
	}
	
	private static boolean haveAllSquareFacesTheSameColor(Cube cube, int[] squareIndexes, String orientation) {
		String color = null;
		
		for(int i : squareIndexes) {
			Square square = cube.getSquares().get(i);
			SquareFace squareFace =	square.retrieveSquareFaceByOrientation(orientation);
			
			if(squareFace == null)
				throw new IllegalStateException(String.format("Can not find squareFace with params [index: %s, orientation: %s]", i, orientation));
			
			if(squareFace.getColor() == null)
				throw new IllegalStateException(String.format("squareFace [index: %s, orientation: %s] has no color", i, orientation));
			
			if(color == null)
				color = squareFace.getColor();
			if(color != null && ! color.equals(squareFace.getColor()))
				return false;
		}
		return true;
	}
}
