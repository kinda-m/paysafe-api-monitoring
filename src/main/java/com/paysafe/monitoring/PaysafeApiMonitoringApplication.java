package com.paysafe.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/***
 * This class is the entry point of this application.
 */
@SpringBootApplication
public class PaysafeApiMonitoringApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaysafeApiMonitoringApplication.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
}
