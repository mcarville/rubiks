package com.rubiks.robot;

import com.rubiks.objects.AbstractJSONSerialiazer;
import com.rubiks.objects.Cube;
import com.rubiks.objects.CubeMove;

public class CubeKafkaMessage extends AbstractJSONSerialiazer {

	private final Cube cube;
	private final CubeMove cubeMove;
	private CubeTaskReport cubeTaskReport;

	public CubeKafkaMessage(Cube cube, CubeMove cubeMove) {
		super();
		this.cube = cube;
		this.cubeMove = cubeMove;
	}
	
	public Cube getCube() {
		return cube;
	}
	
	public CubeMove getCubeMove() {
		return cubeMove;
	}

	public CubeTaskReport getCubeTaskReport() {
		return cubeTaskReport;
	}

	public void setCubeTaskReport(CubeTaskReport cubeTaskReport) {
		this.cubeTaskReport = cubeTaskReport;
	}
}
