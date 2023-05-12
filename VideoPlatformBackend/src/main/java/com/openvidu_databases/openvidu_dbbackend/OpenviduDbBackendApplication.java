package com.openvidu_databases.openvidu_dbbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableJpaRepositories
@EnableWebMvc
public class OpenviduDbBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenviduDbBackendApplication.class, args);
	}

}
