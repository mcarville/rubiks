package com.rubiks.objects;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.gson.Gson;

public class AbstractJSONSerialiazer {

	public JSONObject toJSON() throws JSONException {
		Gson gson = new Gson();
		return new JSONObject(gson.toJson(this));
	}

	@SuppressWarnings("unchecked")
	public static <T> T fromJSON(String stringJSON, Class<T> classType) {
		Gson gson = new Gson();
		return (T)gson.fromJson(stringJSON, classType);
	}
	
	@Override
	public String toString() {
		try {
			return toJSON().toString();
		} catch (JSONException e) {
			return super.toString();
		}
	}
}
