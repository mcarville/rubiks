package com.rubiks.robot;

public class CubeTaskReport {

	private final boolean onError;
	private final String runtimeMXBean;
	private final String hostname;
	
	public CubeTaskReport(boolean onError, String runtimeMXBean,
			String hostname) {
		super();
		this.onError = onError;
		this.runtimeMXBean = runtimeMXBean;
		this.hostname = hostname;
	}

	public boolean isOnError() {
		return onError;
	}

	public String getRuntimeMXBean() {
		return runtimeMXBean;
	}

	public String getHostname() {
		return hostname;
	}
}
