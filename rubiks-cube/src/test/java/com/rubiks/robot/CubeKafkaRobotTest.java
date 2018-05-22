package com.rubiks.robot;

import java.io.IOException;
import java.nio.charset.Charset;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.PullImageResultCallback;

public class CubeKafkaRobotTest extends TestCase {

	private static final String HA_KAFKA_HUB_NAME = "mcarville/sandbox:ha_kafka_3";

	protected Logger logger = Logger.getLogger(getClass());
	
	protected String containerId;
	protected DockerClient dockerClient;
	
	@Override
	protected void setUp() throws Exception {

		dockerClient = DockerClientBuilder.getInstance().build();
		
		AuthConfig authConfig = new AuthConfig();
		
		String dockerUsername = System.getenv().get("DOCKER_USERNAME");
		String dockerPassword = System.getenv().get("DOCKER_PASSWORD");
		
		if(StringUtils.isNotEmpty(dockerUsername) && StringUtils.isNotEmpty(dockerPassword))
			authConfig.withUsername(dockerUsername).withPassword(dockerPassword);
		
        dockerClient.authCmd().withAuthConfig(authConfig).exec();
        
		CreateContainerCmd containerConfig = 
				dockerClient.createContainerCmd(HA_KAFKA_HUB_NAME)
				.withPortBindings(PortBinding.parse("9092:9092"), PortBinding.parse("9000:9000"));
		
		String osName = System.getProperty("os.name");
		if(StringUtils.isNotEmpty(osName) && osName.equalsIgnoreCase("Windows 7"))
			containerConfig.withEnv(String.format("KAFKA_ADVERTISED_HOST_NAME=%s", retrieveDockerMachine()));
		
		CreateContainerResponse container = null;
		try 
		{ 
			container = containerConfig.exec();
		}
		catch(NotFoundException e) {
			
			dockerClient.pullImageCmd(HA_KAFKA_HUB_NAME)
	        	.withAuthConfig(authConfig)
	        	.exec(new PullImageResultCallback()).awaitSuccess();
			
			logger.info(String.format("Docker image: %s has been downloaded", HA_KAFKA_HUB_NAME));
			
			container = containerConfig.exec();
		}
		
		containerId = container.getId();
		
		dockerClient.startContainerCmd(containerId).exec();
		
		logger.info(String.format("Start container with containerId: %s", containerId));
	}
	
	private String retrieveDockerMachine() throws IOException {
		String command = "docker-machine ip default";
	    Process child = Runtime.getRuntime().exec(command);

	    String result = IOUtils.toString(child.getInputStream(), Charset.defaultCharset());
	    
	    logger.info(String.format("%s => %s", command, result));
	    
	    return result;
	}
	
	@Override
	protected void tearDown() throws Exception {
		if(dockerClient != null && StringUtils.isNotEmpty(containerId)) {
			dockerClient.stopContainerCmd(containerId).exec();
			
			logger.info(String.format("Stop container with containerId: %s", containerId));
			
			dockerClient.close();
		}
	}
	
	public void test() {

	}
}
