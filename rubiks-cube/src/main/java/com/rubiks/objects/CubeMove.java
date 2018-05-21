package com.rubiks.objects;



public class CubeMove extends AbstractJSONSerialiazer {

	protected final String axe;
	protected final String level;
	protected final String direction;
	protected final String magicMove;
	
	public CubeMove(String axe, String level, String direction, String magicMove) {
		super();
		this.axe = axe;
		this.level = level;
		this.direction = direction;
		this.magicMove = magicMove;
	}

	public String getAxe() {
		return axe;
	}

	public String getLevel() {
		return level;
	}

	public String getDirection() {
		return direction;
	}

	public String getMagicMove() {
		return magicMove;
	}
	

}
