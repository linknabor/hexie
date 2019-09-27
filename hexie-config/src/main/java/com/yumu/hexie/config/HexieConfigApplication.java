package com.yumu.hexie.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 
 * @author david
 *
 */
@SpringBootApplication
@EnableConfigServer
@EnableEurekaClient
public class HexieConfigApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(HexieConfigApplication.class, args);
	}
}
