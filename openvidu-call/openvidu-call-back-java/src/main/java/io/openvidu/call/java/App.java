package io.openvidu.call.java;

import io.openvidu.call.java.core.SessionManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("io.openvidu.call.java")
public class App implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
  @Override
  public void run(String...args) throws Exception{
    SessionManager.main(args);
  }

}
