package com.rubiks.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Cube extends AbstractJSONSerialiazer {

	public static final String FRONT = "front";
	public static final String NORTH = "north";
	public static final String EAST = "east";
	public static final String SOUTH = "south";
	public static final String WEST = "west";
	public static final String BACK = "back";
	
	public static final String LEFT = "left";
	public static final String CENTER = "center";
	public static final String RIGHT = "right";

	public static final String BOTTOM = "bottom";
	public static final String MIDDLE = "middle";
	public static final String TOP = "top";
	
	public static class AXE {
		public static final String HORIZONTAL = "horizontal";
		public static final String VERTICAL_EAST_WEST = "vertical_east_west";
		public static final String VERTICAL_NORTH_SOUTH = "vertical_north_south";
	}
	
	public static final String CLOCKWISE = "clockwise";
	
	private static Integer[][] HORIZONTAL_MOVES = {
		// CORNERS
		{0,2}, {2,8}, {8,6}, {6,0},
		// SIDES
		{1,5}, {5,7}, {7,3}, {3,1}
	};
	
	private static Integer[][] VERTICAL_EAST_WEST_MOVES = {
		// CORNERS
		{0,2}, {2,20}, {20,18}, {18,0},
		// SIDES
		{1,11}, {11,19}, {19,9}, {9,1}
	};
	
	private static Integer[][] VERTICAL_NORTH_SOUTH_MOVES = {
		// CORNERS
		{0,6}, {6,24}, {24,18}, {18,0},
		// SIDES
		{3,15}, {15,21}, {21,9}, {9,3}
	};
	
	private final List<Square> squares;

	public Cube(List<Square> squares) {
		super();
		this.squares = squares;
	}
	
	public List<Square> getSquares() {
		return squares;
	}
	
	private CubeAnalysis cubeAnalysis;
	
	public CubeAnalysis getCubeAnalysis() {
		return cubeAnalysis;
	}

	public void setCubeAnalysis(CubeAnalysis cubeAnalysis) {
		this.cubeAnalysis = cubeAnalysis;
	}

	public Map<String, CubeFace> toCubeFaces() {
		Map<String, CubeFace> cubeFaces = new HashMap<String, CubeFace>();
		
		CubeFace cubeFace = buildCubeFaceColors();
		cubeFaces.put(FRONT, cubeFace);
		
		move(AXE.VERTICAL_EAST_WEST, CLOCKWISE);
		cubeFaces.put(EAST, buildCubeFaceColors());
		cubeFaces.put("east_reverse", buildCubeFaceColors(true));
		move(AXE.VERTICAL_EAST_WEST, "reverse clockwise");
		
		move(AXE.VERTICAL_EAST_WEST, "reverse clockwise");
		cubeFaces.put(WEST, buildCubeFaceColors());
		cubeFaces.put("west_reverse", buildCubeFaceColors(true));
		move(AXE.VERTICAL_EAST_WEST, CLOCKWISE);
		
		move(AXE.VERTICAL_EAST_WEST, CLOCKWISE);
		move(AXE.VERTICAL_EAST_WEST, CLOCKWISE);
		cubeFaces.put(BACK, buildCubeFaceColors(true));
		move(AXE.VERTICAL_EAST_WEST, "reverse clockwise");
		move(AXE.VERTICAL_EAST_WEST, "reverse clockwise");
		
		move(AXE.VERTICAL_NORTH_SOUTH, CLOCKWISE); 
		cubeFaces.put(NORTH, buildCubeFaceColors());
		move(AXE.VERTICAL_NORTH_SOUTH, "reverse clockwise");
		
		move(AXE.VERTICAL_NORTH_SOUTH, "reverse clockwise");
		cubeFaces.put(SOUTH, buildCubeFaceColors());
		move(AXE.VERTICAL_NORTH_SOUTH, CLOCKWISE); 
		
		return cubeFaces;
	}
	
	public void move(String axe, String direction) {
		if(axe.equals(AXE.HORIZONTAL)) {
			moveCubeFace(axe, TOP, direction);
			moveCubeFace(axe, MIDDLE, direction);
			moveCubeFace(axe, BOTTOM, direction);
		}
		else if(axe.equals(AXE.VERTICAL_EAST_WEST) || axe.equals(AXE.VERTICAL_NORTH_SOUTH)) {
			moveCubeFace(axe, LEFT, direction);
			moveCubeFace(axe, CENTER, direction);
			moveCubeFace(axe, RIGHT, direction);
		}
	}
	
	public void move(String axe, String level, String direction) {
		if(direction == null)
			direction = CLOCKWISE;
		
		if(level == null || level.equals(AXE.HORIZONTAL) || level.equals(AXE.VERTICAL_EAST_WEST) || level.equals(AXE.VERTICAL_NORTH_SOUTH)) {
			move(axe, direction);
		}
		else {
			moveCubeFace(axe, level, direction);
		}
		setCubeAnalysis(CubeAnalysis.buildCubeAnalysis(this));
	}
	
	private void moveCubeFace(String axe, String level, String direction) {

		int startIndex = -1;
		Integer[][] moves = null;
		
		if(axe.equals(AXE.HORIZONTAL)) {
			startIndex = level.equals(TOP) ? 0 :
				level.equals(MIDDLE) ? 9 :
					level.equals(BOTTOM) ?  18 : -1;
		
			moves = direction.equals(CLOCKWISE) ? HORIZONTAL_MOVES : reverseMoves(HORIZONTAL_MOVES);
		}
		
		if(axe.equals(AXE.VERTICAL_EAST_WEST)) {
			startIndex = level.equals(LEFT) ? 0 :
				level.equals(CENTER) ? 3 :
					level.equals(RIGHT) ?  6: -1;
			
			moves = direction.equals(CLOCKWISE) ? VERTICAL_EAST_WEST_MOVES : reverseMoves(VERTICAL_EAST_WEST_MOVES);
		}
		
		if(axe.equals(AXE.VERTICAL_NORTH_SOUTH)) {
			startIndex = level.equals(LEFT) ? 0 :
				level.equals(CENTER) ? 1 :
					level.equals(RIGHT) ?  2: -1;
			
			moves = direction.equals(CLOCKWISE) ? VERTICAL_NORTH_SOUTH_MOVES : reverseMoves(VERTICAL_NORTH_SOUTH_MOVES);
		}
		
		if(startIndex < 0)
			throw new IllegalStateException("Bad startIndex for params: " + level + ", " + direction);
		
		if(moves == null)
			throw new IllegalStateException("moves is null for params: " + level + ", " + direction);
		
		List<Square> copySquares = new ArrayList<Square>(squares);

		for(Integer[] move : moves) {
			squares.set(startIndex + move[1], copySquares.get(startIndex + move[0]));
			squares.get(startIndex + move[1]).changeOrientation(axe, direction);
		}
	}

	private Integer[][] reverseMoves (Integer[][] moves) {
		Integer[][] reverseMoves = new Integer[8][2];
		for(int i = 0 ; i < moves.length ; i++) {
			reverseMoves[i] = new Integer[]{moves[i][1], moves[i][0]}; 
		}
		return reverseMoves;
	}
	
	protected CubeFace buildCubeFaceColors() {
		return buildCubeFaceColors(false);
	}
	
	protected CubeFace buildCubeFaceColors(boolean reverse) {
		List<String> cubeFaceSquares = new ArrayList<String>();

		for(int i = 0; i < 9 ; i++) {
			int index = (! reverse) ? i : 8 - i;
			Square square = squares.get(index);
			SquareFace frontSquareFace = square.retrieveSquareFaceByOrientation(SquareFace.FRONT);
			
			if(frontSquareFace == null) {
				throw new IllegalStateException(String.format("frontSquareFace can not be null, on square: %s", square.toString()));
				// cubeFaceSquares.add("black");
			}
			else
				cubeFaceSquares.add(frontSquareFace.getColor());
		}
		CubeFace cubeFace = CubeFace.buildCubeFace(cubeFaceSquares.toArray(new String[cubeFaceSquares.size()]));
		return cubeFace;
	}
	
	public JSONObject toCubeFacesJSON() throws JSONException {
		JSONObject jsonArray = new JSONObject();
		
		Map<String, CubeFace> cubeFaces = toCubeFaces();
		
		for(Entry<String, CubeFace> cubeFace : cubeFaces.entrySet()) {
			jsonArray.put(cubeFace.getKey(), cubeFace.getValue().toJSONArray());
		}
		
		return jsonArray;
	}

	public void executeMove(CubeMove cubeMove) {
		if(StringUtils.isNotEmpty(cubeMove.getMagicMove()))
			new CubeMagicMove().executeMagicMode(cubeMove.getMagicMove(), this);
		if(StringUtils.isNotEmpty(cubeMove.getAxe()))
			this.move(cubeMove.getAxe(), cubeMove.getLevel(), cubeMove.getDirection());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cubeAnalysis == null) ? 0 : cubeAnalysis.hashCode());
		result = prime * result + ((squares == null) ? 0 : squares.hashCode());
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
		Cube other = (Cube) obj;
		if (cubeAnalysis == null) {
			if (other.cubeAnalysis != null)
				return false;
		} else if (!cubeAnalysis.equals(other.cubeAnalysis))
			return false;
		if (squares == null) {
			if (other.squares != null)
				return false;
		} else if (!squares.equals(other.squares))
			return false;
		return true;
	}
	
//	public boolean isBackCross() {
//
//	}
//	
//	public boolean isCubeDone() {
//
//	}

	

}
