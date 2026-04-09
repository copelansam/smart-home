package com.example.smarthome;

import com.example.smarthome.domain.smartdevices.statemachine.states.StateInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.example.smarthome")
public class SmartHomeApplication {

	public static void main(String[] args) {

        StateInitializer.init();

        SpringApplication.run(SmartHomeApplication.class, args);

	}

}
