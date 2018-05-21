package com.common.kafka.tests;


public class MainConsumer {

	public static void main(String[] args) throws InterruptedException {

		for(int i = 0 ; i < 4 ; i++) {
			Thread consumerThread = new Thread(new MainConsumerThread());
			consumerThread.start();
		}
		
		while(true){
			Thread.sleep(10000);
		}
	}

}
