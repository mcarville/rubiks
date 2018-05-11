package com.rubiks.objects;

import java.util.ArrayList;
import java.util.List;

public class CubeFactory {

	public static Cube createCube() {
		
		List<Square> squares = new ArrayList<Square>();
		
		// top: 0 to 9
		squares.add(new Square(new SquareFace(SquareFace.GREEN, SquareFace.NORTH), new SquareFace(SquareFace.WHITE, SquareFace.FRONT), new SquareFace(SquareFace.RED, SquareFace.WEST)));
		squares.add(new Square(new SquareFace(SquareFace.WHITE, SquareFace.FRONT), new SquareFace(SquareFace.GREEN, SquareFace.NORTH)));
		squares.add(new Square(new SquareFace(SquareFace.ORANGE, SquareFace.EAST), new SquareFace(SquareFace.WHITE, SquareFace.FRONT), new SquareFace(SquareFace.GREEN, SquareFace.NORTH)));
		
		squares.add(new Square(new SquareFace(SquareFace.WHITE, SquareFace.FRONT), new SquareFace(SquareFace.RED, SquareFace.WEST)));
		squares.add(new Square(new SquareFace(SquareFace.WHITE, SquareFace.FRONT)));
		squares.add(new Square(new SquareFace(SquareFace.WHITE, SquareFace.FRONT), new SquareFace(SquareFace.ORANGE, SquareFace.EAST)));
		
		squares.add(new Square(new SquareFace(SquareFace.RED, SquareFace.WEST), new SquareFace(SquareFace.WHITE, SquareFace.FRONT), new SquareFace(SquareFace.BLUE, SquareFace.SOUTH)));
		squares.add(new Square(new SquareFace(SquareFace.WHITE, SquareFace.FRONT), new SquareFace(SquareFace.BLUE, SquareFace.SOUTH)));
		squares.add(new Square(new SquareFace(SquareFace.BLUE, SquareFace.SOUTH), new SquareFace(SquareFace.WHITE, SquareFace.FRONT), new SquareFace(SquareFace.ORANGE, SquareFace.EAST)));

		// middle: 9 to 17
		squares.add(new Square(new SquareFace(SquareFace.GREEN, SquareFace.NORTH), new SquareFace(SquareFace.RED, SquareFace.WEST)));
		squares.add(new Square(new SquareFace(SquareFace.GREEN, SquareFace.NORTH)));
		squares.add(new Square(new SquareFace(SquareFace.ORANGE, SquareFace.EAST), new SquareFace(SquareFace.GREEN, SquareFace.NORTH)));
		
		squares.add(new Square(new SquareFace(SquareFace.RED, SquareFace.WEST)));
		// center
		squares.add(new Square());
		squares.add(new Square(new SquareFace(SquareFace.ORANGE, SquareFace.EAST)));
		
		squares.add(new Square(new SquareFace(SquareFace.RED, SquareFace.WEST), new SquareFace(SquareFace.BLUE, SquareFace.SOUTH)));
		squares.add(new Square(new SquareFace(SquareFace.BLUE, SquareFace.SOUTH)));
		squares.add(new Square(new SquareFace(SquareFace.BLUE, SquareFace.SOUTH), new SquareFace(SquareFace.ORANGE, SquareFace.EAST)));
		
		// middle: 18 to 26
		squares.add(new Square(new SquareFace(SquareFace.GREEN, SquareFace.NORTH), new SquareFace(SquareFace.RED, SquareFace.WEST), new SquareFace(SquareFace.YELLOW, SquareFace.BACK)));
		squares.add(new Square(new SquareFace(SquareFace.GREEN, SquareFace.NORTH), new SquareFace(SquareFace.YELLOW, SquareFace.BACK)));
		squares.add(new Square(new SquareFace(SquareFace.ORANGE, SquareFace.EAST), new SquareFace(SquareFace.GREEN, SquareFace.NORTH), new SquareFace(SquareFace.YELLOW, SquareFace.BACK)));
		
		squares.add(new Square(new SquareFace(SquareFace.RED, SquareFace.WEST), new SquareFace(SquareFace.YELLOW, SquareFace.BACK)));
		squares.add(new Square(new SquareFace(SquareFace.YELLOW, SquareFace.BACK)));
		squares.add(new Square(new SquareFace(SquareFace.YELLOW, SquareFace.BACK), new SquareFace(SquareFace.ORANGE, SquareFace.EAST)));
		
		squares.add(new Square(new SquareFace(SquareFace.BLUE, SquareFace.SOUTH), new SquareFace(SquareFace.YELLOW, SquareFace.BACK), new SquareFace(SquareFace.RED, SquareFace.WEST)));
		squares.add(new Square(new SquareFace(SquareFace.YELLOW, SquareFace.BACK), new SquareFace(SquareFace.BLUE, SquareFace.SOUTH)));
		squares.add(new Square(new SquareFace(SquareFace.BLUE, SquareFace.SOUTH), new SquareFace(SquareFace.YELLOW, SquareFace.BACK), new SquareFace(SquareFace.ORANGE, SquareFace.EAST)));
		
		return new Cube(squares);
	}
}
