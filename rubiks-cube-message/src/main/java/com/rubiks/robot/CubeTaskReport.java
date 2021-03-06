package com.rubiks.robot;

public class CubeTaskReport {

	private final boolean onError;
	private final String runtimeMXBean;
	private final String hostname;
	private final String dockerAlias;
	
	public CubeTaskReport(boolean onError, String runtimeMXBean, String hostname, String dockerAlias) {
		super();
		this.onError = onError;
		this.runtimeMXBean = runtimeMXBean;
		this.hostname = hostname;
		this.dockerAlias = dockerAlias;
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
	
	public String getDockerAlias() {
		return dockerAlias;
	}
}
