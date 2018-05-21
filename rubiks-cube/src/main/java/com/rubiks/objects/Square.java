package com.rubiks.objects;

import java.util.Arrays;
import java.util.List;

public class Square {

	private final List<SquareFace> squareFaces;

	public Square(List<SquareFace> squareFaces) {
		super();
		this.squareFaces = squareFaces;
	}
	
	public Square(SquareFace ... squareFaces) {
		super();
		this.squareFaces = Arrays.asList(squareFaces);
	}
	
	public List<SquareFace> getSquareFaces() {
		return squareFaces;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(squareFaces.toArray());
	}
	
	public void changeOrientation(String axe, String direction) {
		for(SquareFace squareFace : squareFaces) {
			squareFace.changeOrientation(axe, direction);
		}
	}
	
	public SquareFace retrieveSquareFaceByOrientation(String orientation) {
		for(SquareFace squareFace : squareFaces) {
			if(squareFace.getOrientation().equalsIgnoreCase(orientation))
				return squareFace;
		}
		return null;
	}
}
