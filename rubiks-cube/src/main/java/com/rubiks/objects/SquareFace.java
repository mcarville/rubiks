package com.rubiks.objects;

import java.util.Arrays;
import java.util.List;

import com.rubiks.objects.Cube.AXE;

public class SquareFace {

	public static final String RED = "red";
	public static final String WHITE = "white";
	public static final String BLUE = "blue";
	public static final String ORANGE = "orange";
	public static final String GREEN = "green";
	public static final String YELLOW = "yellow";

	public static final String FRONT = "front";
	public static final String BACK = "back";
	public static final String NORTH = "north";
	public static final String SOUTH = "south";
	public static final String EAST = "east";
	public static final String WEST = "west";
	
	public static final String[] HORIZONTAL_ORIENTATIONS = {WEST, NORTH, EAST, SOUTH};
	public static final String[] VERTICAL_EAST_WEST_ORIENTATIONS = {FRONT, EAST, BACK, WEST};
	public static final String[] VERTICAL_NORTH_SOUTH_HORIZONTAL_ORIENTATIONS = {FRONT, SOUTH, BACK, NORTH};
	
	private final String color;
	private String orientation;
	
	public SquareFace(String color, String orientation) {
		super();
		this.color = color;
		this.orientation = orientation;
	}
	
	public String getColor() {
		return color;
	}
	
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}
	
	public String getOrientation() {
		return orientation;
	}
	
	@Override
	public String toString() {
		return color + ":" + orientation;
	}
	
	public void changeOrientation(String axe, String direction) {
		String[] orientations = (axe.equalsIgnoreCase(Cube.AXE.HORIZONTAL)) ? HORIZONTAL_ORIENTATIONS : 
			(axe.equalsIgnoreCase(Cube.AXE.VERTICAL_EAST_WEST)) ? VERTICAL_EAST_WEST_ORIENTATIONS :
				(axe.equalsIgnoreCase(Cube.AXE.VERTICAL_NORTH_SOUTH)) ? VERTICAL_NORTH_SOUTH_HORIZONTAL_ORIENTATIONS :	null;
		
		setOrientation(retrieveNextOrientation(orientations, getOrientation(), direction));
	}
	
	public String retrieveNextOrientation(String[] orientations, String orientation, String direction) {
		List<String> orientationList = Arrays.asList(orientations);
		if( ! orientationList.contains(orientation)) {
			return orientation;
		}
		else
		{
			int index = -1;
			if(direction.equals(Cube.CLOCKWISE)) 
			{
				index = (orientationList.indexOf(orientation) == orientationList.size() - 1) ? 0 : orientationList.indexOf(orientation) + 1; 
			}
			else
			{
				index = (orientationList.indexOf(orientation) == 0) ? orientationList.size() - 1 : orientationList.indexOf(orientation) - 1;
			}
			return orientationList.get(index);
		}
	}
}
