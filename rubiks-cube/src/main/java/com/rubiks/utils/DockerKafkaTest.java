package com.rubiks.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

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
import com.rubiks.robot.CubeKafkaRobot;

import junit.framework.TestCase;

public abstract class DockerKafkaTest extends TestCase {

	public static final String HA_KAFKA_HUB_NAME = "mcarville/sandbox:ha_kafka_5";

	protected Logger logger = Logger.getLogger(getClass());
	
	protected String containerId;
	protected DockerClient dockerClient;
	
//	@Override
//	protected void setUp() throws Exception {
//
//		String osName = System.getProperty("os.name");
//		if(StringUtils.isNotEmpty(osName) && osName.equalsIgnoreCase("Windows 7"))
//		{
//			logger.info("You are running in DEV environnement Windows");
////			containerConfig.withEnv(String.format("KAFKA_ADVERTISED_HOST_NAME=%s", "192.168.71.131")); // DEV Config
//		}
//		else
//		{
//			dockerClient = DockerClientBuilder.getInstance().build();
//			
//			AuthConfig authConfig = new AuthConfig();
//			
//			String dockerUsername = System.getenv().get("DOCKER_USERNAME");
//			String dockerPassword = System.getenv().get("DOCKER_PASSWORD");
//			
//			if(StringUtils.isNotEmpty(dockerUsername) && StringUtils.isNotEmpty(dockerPassword))
//				authConfig.withUsername(dockerUsername).withPassword(dockerPassword);
//			
//	        dockerClient.authCmd().withAuthConfig(authConfig).exec();
//	        
//			CreateContainerCmd containerConfig = 
//					dockerClient.createContainerCmd(HA_KAFKA_HUB_NAME)
//					.withPortBindings(PortBinding.parse("9092:9092"), PortBinding.parse("9000:9000"));
//			
//			containerConfig.withEnv(String.format("KAFKA_ADVERTISED_HOST_NAME=%s", "127.0.0.1"));
//		
//			CreateContainerResponse container = null;
//			try 
//			{ 
//				container = containerConfig.exec();
//			}
//			catch(NotFoundException e) {
//				
//				logger.info(String.format("Docker image: %s does not exist locally => trying to get it from the registry", HA_KAFKA_HUB_NAME));
//				
//				dockerClient.pullImageCmd(HA_KAFKA_HUB_NAME)
//		        	.withAuthConfig(authConfig)
//		        	.exec(new PullImageResultCallback()).awaitSuccess();
//				
//				logger.info(String.format("Docker image: %s has been downloaded", HA_KAFKA_HUB_NAME));
//				
//				container = containerConfig.exec();
//			}
//			
//			containerId = container.getId();
//			
//			dockerClient.startContainerCmd(containerId).exec();
//			
//			logger.info(String.format("Start container with containerId: %s", containerId));
//		}
//	}
//	
//	@Override
//	protected void tearDown() throws Exception {
//		if(dockerClient != null && StringUtils.isNotEmpty(containerId)) {
//			dockerClient.stopContainerCmd(containerId).exec();
//			
//			logger.info(String.format("Stop container with containerId: %s", containerId));
//			
//			dockerClient.close();
//		}
//	}
	
	protected List<CubeKafkaRobot> startCubeKafkaRobots (ThreadPoolExecutor threadPoolExecutor, int cubeKafkaRobotNumber) {
		List<CubeKafkaRobot> cubeKafkaRobots = new ArrayList<CubeKafkaRobot>();
		for(int i = 0 ; i < cubeKafkaRobotNumber ; i++) {
			CubeKafkaRobot cubeKafkaRobot = new CubeKafkaRobot();
			cubeKafkaRobots.add(cubeKafkaRobot);
			threadPoolExecutor.submit(cubeKafkaRobot);
		}
		return cubeKafkaRobots;
	}

	protected void stopCubeKafkaRobots(List<CubeKafkaRobot> cubeKafkaRobots) {
		for(CubeKafkaRobot cubeKafkaRobot : cubeKafkaRobots)
			cubeKafkaRobot.stop();
	}
}
