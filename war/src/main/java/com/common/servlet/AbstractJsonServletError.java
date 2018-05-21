package com.common.servlet;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class AbstractJsonServletError {

	protected static Logger logger = Logger.getLogger(AbstractJsonServletError.class);
	
	private static final String JSON_EXCEPTION = "exception";
	private static final String JSON_MESSAGE = "message";
	private static final String JSON_STACKTRACE = "stacktrace";
	

	public void addExceptionFields(Exception exception, JSONObject jsonResponse) throws JSONException {
		JSONObject jsonException = new JSONObject();
		jsonResponse.put(JSON_EXCEPTION, jsonException);
		jsonException.put(JSON_MESSAGE, exception.getMessage());
		jsonException.put(JSON_STACKTRACE, ExceptionUtils.getStackTrace(exception));
	}

}
