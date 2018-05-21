package com.rubiks.objects;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;

public class CubeFace {

	private final List<List<String>> cubeFaceSquares;

	public CubeFace(List<List<String>> cubeFaceSquares) {
		super();
		this.cubeFaceSquares = cubeFaceSquares;
	}
	
	public List<List<String>> getCubeFaceSquares() {
		return cubeFaceSquares;
	}
	
	public static CubeFace buildCubeFace(String[] colors) {
		
		List<List<String>> cubeFaceSquares = new ArrayList<List<String>>();
		List<String> faceQuares = new ArrayList<String>();
		for(int i = 0 ; i < 9 ; i++) {
			faceQuares.add(colors[i]);
			
			if((i + 1) % 3 == 0) {
				cubeFaceSquares.add(faceQuares);
				faceQuares = new ArrayList<String>();
			}
		}
		
		return new CubeFace(cubeFaceSquares);
	}
	
	public JSONArray toJSONArray() {
		JSONArray jsonArray = new JSONArray();

		for(List<String> faceSquares : cubeFaceSquares) {
			JSONArray faceSquaresArray = new JSONArray();
			for(String color : faceSquares) {
				faceSquaresArray.put(color);
			}
			jsonArray.put(faceSquaresArray);
		}
		
		return jsonArray;
	}
}
