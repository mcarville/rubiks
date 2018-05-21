package com.common.command;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jettison.json.JSONObject;

public interface ICommand {

	public String getCommandId();
	
	public void initialize();
	
	public JSONObject executeCommand(Map<String, String[]> parameterMap, HttpServletRequest request, JSONObject jsonResponse) throws Exception;
	
	public void terminate();
}
