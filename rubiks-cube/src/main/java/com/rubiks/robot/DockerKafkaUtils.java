package com.rubiks.robot;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class DockerKafkaUtils {

	protected static Logger LOGGER = Logger.getLogger(DockerKafkaUtils.class);
	
	public static String retrieveKafkaHostname() {
		String osName = System.getProperty("os.name");
		if(StringUtils.isNotEmpty(System.getenv().get("KAFKA_BROKER_HOST"))) {
			return System.getenv().get("KAFKA_BROKER_HOST");
		}
		else if(StringUtils.isNotEmpty(osName) && osName.equalsIgnoreCase("Windows 7"))
			return "192.168.71.131"; // DEV Config
		return "127.0.0.1";
	}
	
	public static String buildBrokerServersConnectionString () {
		String kafkaBrokerServers = String.format("%s:%s", retrieveKafkaHostname(), "9092");
		LOGGER.debug(String.format("kafkaBrokerServers: %s", kafkaBrokerServers));
		return kafkaBrokerServers;
	}
}
