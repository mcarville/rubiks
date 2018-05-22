package com.common.servlet;

import java.net.ConnectException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONObject;




public abstract class AbstractJsonServlet extends AbstractServlet {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGetOrPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject jsonResponse = new JSONObject();
		try
		{
			handleRequest(request, jsonResponse);
		}
		catch(Exception exception)
		{
			logger.error(exception.toString(), (! (exception instanceof ConnectException)) ? exception : null);
			AbstractJsonServletError errorHandler = new AbstractJsonServletError();
			errorHandler.addExceptionFields(exception, jsonResponse);
		}
		writeJsonResponse(response, jsonResponse);
	}
	
	protected abstract void handleRequest(HttpServletRequest request, JSONObject jsonResponse) throws Exception;
	
	protected void writeJsonResponse(HttpServletResponse response, JSONObject jsonResponse) throws Exception 
	{
		writeResponse(response, jsonResponse.toString(), APPLICATION_JSON_CONTENT_TYPE);
	}
	
	
}
