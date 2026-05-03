package com.example.smarthome;

import com.example.smarthome.domain.smartdevices.statemachine.states.StateInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Smart Home Spring Boot application.
 *
 * Bootstraps the application context, enables scheduling,
 * and initializes the state machine before the application starts.
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.example.smarthome")
public class SmartHomeApplication {

	public static void main(String[] args) {

        // Initializes the state machine
         StateInitializer.init();

         // Starts the application
        SpringApplication.run(SmartHomeApplication.class, args);

	}

}
