package com.common.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jettison.json.JSONObject;

import com.common.command.ICommand;
import com.rubiks.command.RetrieveCube;

public class RestAPIServlet extends AbstractJsonServlet {
	
	// com.common.servlet.RestAPIServlet
	
	private static final long serialVersionUID = 1L;
	
	private List<ICommand> avaliableCommands = new ArrayList<ICommand>();
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		avaliableCommands.add(new RetrieveCube());
		
		for(ICommand avaliableCommand : avaliableCommands)
			avaliableCommand.initialize();
	}

	protected void handleRequest(HttpServletRequest request, JSONObject jsonResponse) throws Exception {
		String commandParamValue = retrieveParamValue(request, "command", true);
		
		ICommand command = retrieveCommand(commandParamValue);
		
		if(command == null)
			throw new IllegalStateException("command can not be null, params was " + commandParamValue);
		
		command.executeCommand((Map<String, String[]>)request.getParameterMap(), request, jsonResponse);
	}
	
	private ICommand retrieveCommand(String commandParamValue) {
		ICommand command = null;
		for(ICommand avaliableCommand : avaliableCommands)
		{
			if(commandParamValue.equals(avaliableCommand.getCommandId()))
			{
				command = avaliableCommand;
				break;
			}
		}
		return command;
	}
}
