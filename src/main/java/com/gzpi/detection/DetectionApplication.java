package com.gzpi.detection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication()
public class DetectionApplication {

	public static void main(String[] args) {
		SpringApplication.run(DetectionApplication.class, args);
	}

}
