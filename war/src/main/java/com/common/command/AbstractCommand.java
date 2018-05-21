package com.common.command;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public abstract class AbstractCommand implements ICommand {

	protected Logger logger = Logger.getLogger(getClass());
	

	protected static final String REWRITTEN_URL = "rewrittenUrl";	
	protected static final String DOCUMENT_UID_JSON = "documentUid";
	
	protected static final String PARAMETER_MAP_JSON = "parameterMap";

	@Override
	public void initialize() {}
	
	@Override
	public void terminate() {}
	
	
	protected void addHttpParameters(HttpServletRequest request, JSONObject jsonResponse) throws JSONException {
		JSONObject parameterMapJSON = new JSONObject();
		@SuppressWarnings("unchecked")
		Enumeration<String> enumeration = request.getParameterNames();
		while(enumeration.hasMoreElements()) {
			String paramName = enumeration.nextElement();
			parameterMapJSON.put(paramName, new JSONArray(Arrays.asList(request.getParameterValues(paramName))));
		}
		jsonResponse.put(PARAMETER_MAP_JSON, parameterMapJSON);
	}
	
	protected String rewriteUrl(String url) {
		
		return url;
	}
	
	protected String retrieveParamValue(Map<String, String[]> parameterMap, String paramName, boolean mandatory) {
		String value = null;
		if(parameterMap != null && parameterMap.get(paramName) != null && parameterMap.get(paramName).length > 0)
		{
			if(StringUtils.isNotEmpty(parameterMap.get(paramName)[0]))
				value = parameterMap.get(paramName)[0];
		}
		
		if(mandatory && StringUtils.isEmpty(value))
			throw new IllegalStateException("value can not be empty for param: " + paramName);
		
		return value;
	}
	
	protected boolean retrieveBooleanParamValue(Map<String, String[]> parameterMap, String paramName) {
		String paramValue = retrieveParamValue(parameterMap, paramName, false);
		return Boolean.TRUE.toString().equalsIgnoreCase(paramValue);
	}
	
	protected String[] retrieveParamValues(Map<String, String[]> parameterMap, String paramName) {
		if(parameterMap != null && parameterMap.get(paramName) != null)
			return parameterMap.get(paramName);
		else
			return new String[0];
	}
}
