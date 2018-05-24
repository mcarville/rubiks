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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((squareFaces == null) ? 0 : squareFaces.hashCode());
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
		Square other = (Square) obj;
		if (squareFaces == null) {
			if (other.squareFaces != null)
				return false;
		} else if (!squareFaces.equals(other.squareFaces))
			return false;
		return true;
	}
}
