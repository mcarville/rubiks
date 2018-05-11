package com.common.servlet;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public abstract class AbstractServlet extends HttpServlet {
	
	public static final String ENCODING_UTF_8 = "UTF-8";
	protected static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";
	
	private static final long serialVersionUID = 1L;
	
	protected Logger logger = Logger.getLogger(getClass());
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			doGetOrPost(request, response);
		} catch (Exception e) {
			logger.error(e.toString(), e);
			throw new ServletException(e);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			doGetOrPost(request, response);
		} catch (Exception e) {
			logger.error(e.toString(), e);
			throw new ServletException(e);
		}
	}

	protected abstract void doGetOrPost(HttpServletRequest request, HttpServletResponse response) throws Exception;
	
	protected String retrieveParamValue(HttpServletRequest request, String paramName, boolean mandatory) {
		String paramValue = request.getParameter(paramName);
		if (StringUtils.isEmpty(paramValue) && mandatory)
			throw new IllegalArgumentException("The param '" + paramName + "' can not be empty");
		return paramValue;
	}

	protected String retrieveParamValue(Map<String, Object> parameterMap, String paramName, boolean mandatory) {
		String paramValue = null;
		Object object = parameterMap.get(paramName);
		if (object instanceof String)
			paramValue = String.valueOf(object);

		if (StringUtils.isEmpty(paramValue) && mandatory)
			throw new IllegalArgumentException("The param '" + paramName + "' can not be empty");
		return paramValue;
	}
	
	protected void writeResponse(HttpServletResponse response, String responseBody, String contentType) throws Exception 
	{
		response.setCharacterEncoding(ENCODING_UTF_8);
		response.setContentType(contentType);
		response.getWriter().print(responseBody);
		response.getWriter().flush();
		response.getWriter().close();
	}
	
	
}
