package com.rubiks.objects;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;



public class CubeAnalysis extends AbstractJSONSerialiazer {

	protected final boolean frontCrossDone;
	protected final boolean firstFloorDone;
	protected final boolean secondFloorDone;
	protected final boolean cubeDone;
	
	public CubeAnalysis(boolean frontCrossDone, boolean firstFloorDone, boolean secondFloorDone, boolean cubeDone) {
		super();
		this.frontCrossDone = frontCrossDone;
		this.firstFloorDone = firstFloorDone;
		this.secondFloorDone = secondFloorDone;
		this.cubeDone = cubeDone;
	}

	public static CubeAnalysis buildCubeAnalysis(Cube cube) {
		boolean frontCrossDone = retrieveFrontCrossDone(cube);
		boolean firstFloorDone = retrieveFirstFloorDone(cube);
		boolean secondFloorDone = retrieveSecondFloorDone(cube);
		boolean cubeDone = retrieveCubeDone(cube);
		
		return new CubeAnalysis(frontCrossDone, firstFloorDone, secondFloorDone, cubeDone);
	}
	
	private static boolean retrieveFrontCrossDone(Cube cube) {
		int[] CROSS_INDEXES = {1, 3, 4, 5, 7};
		return haveAllSquareFacesTheSameColor(cube, CROSS_INDEXES, Cube.FRONT);
	}
	
	private static boolean retrieveFirstFloorDone(Cube cube) {
		int[] INDEXES = {0, 1, 2, 3, 4, 5, 6, 7, 8};
		return haveAllSquareFacesTheSameColor(cube, INDEXES, Cube.FRONT);
	}
	
	private static boolean retrieveSecondFloorDone(Cube cube) {

		int[] NORTH_INDEXES = {0, 1, 2, 9, 10, 11};
		int[] EAST_INDEXES = {2, 5, 8, 11, 14, 17};
		int[] SOUTH_INDEXES = {6, 7, 8, 15, 16, 17};
		int[] WEST_INDEXES = {0, 3, 6, 9, 12, 15};
		
		return retrieveFirstFloorDone(cube) && haveAllSquareFacesTheSameColor(cube, NORTH_INDEXES, Cube.NORTH)
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

	private static boolean retrieveCubeDone(Cube cube) {
		Map<String, CubeFace> cubeFacesMap = cube.toCubeFaces();
		for(Entry<String, CubeFace> entry : cubeFacesMap.entrySet()) {
			
			String color = null;
			List<List<String>> cubeFaceSquares = entry.getValue().getCubeFaceSquares();
			for(int i = 0 ; i < cubeFaceSquares.size() ; i++) {
				List<String> colors = cubeFaceSquares.get(i);
				for(int j = 0 ; j < colors.size() ; j++) {
					if(color == null)
						color = colors.get(j);
					if( ! color.equals(colors.get(j)))
						return false;
				}
			}
			color = null;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (cubeDone ? 1231 : 1237);
		result = prime * result + (firstFloorDone ? 1231 : 1237);
		result = prime * result + (frontCrossDone ? 1231 : 1237);
		result = prime * result + (secondFloorDone ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CubeAnalysis other = (CubeAnalysis) obj;
		if (cubeDone != other.cubeDone)
			return false;
		if (firstFloorDone != other.firstFloorDone)
			return false;
		if (frontCrossDone != other.frontCrossDone)
			return false;
		if (secondFloorDone != other.secondFloorDone)
			return false;
		return true;
	}

	public boolean isFrontCrossDone() {
		return frontCrossDone;
	}

	public boolean isFirstFloorDone() {
		return firstFloorDone;
	}

	public boolean isSecondFloorDone() {
		return secondFloorDone;
	}

	public boolean isCubeDone() {
		return cubeDone;
	} 
}
