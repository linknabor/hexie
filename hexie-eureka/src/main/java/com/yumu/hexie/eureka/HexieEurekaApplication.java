package com.yumu.hexie.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 
 * @author david
 *
 */
@SpringBootApplication
@EnableEurekaServer
public class HexieEurekaApplication {
	
	public static void main(String[] args) {
		
		SpringApplication.run(HexieEurekaApplication.class, args);
	}

}
