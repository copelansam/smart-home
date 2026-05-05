package com.example.smarthome.repository;

import com.example.smarthome.controller.DeviceCreationRequest;
import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.service.SmartDeviceService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

/***
 * Seeds the database with initial smart device data on application startup.
 *
 * This component runs once at startup via {@link CommandLineRunner}.
 * It checks whether the database is empty and, if so, creates a predefined
 * set of smart devices for development and testing purposes.
 *
 * If data already exists, seeding is skipped to avoid duplication.
 */
@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final ISmartDeviceRepository repo;
    private final SmartDeviceService service;

    public DatabaseSeeder(ISmartDeviceRepository repo, SmartDeviceService service){
        this.repo = repo;
        this.service = service;
    }

    @Override
    public void run(String... args){

        // If there are no records in the database, add seed data. Otherwise, skip this
        if (repo.count() == 0){

            service.createDevice(
                    new DeviceCreationRequest("Living Room Thermostat",
                                            "Living Room",
                                                    DeviceType.THERMOSTAT,
                                                    Map.of("ambientTemperature", 67,
                                                            "desiredTemperature", 72)
                                            )
            );

            service.createDevice(
                    new DeviceCreationRequest("Garage Door Lock",
                            "Garage",
                            DeviceType.DOORLOCK,
                            null
                    )
            );

            service.createDevice(
                    new DeviceCreationRequest("Front Door Lock",
                            "Living Room",
                            DeviceType.DOORLOCK,
                            null
                    )
            );

            service.createDevice(
                    new DeviceCreationRequest("Bedroom Box Fan",
                            "Bedroom",
                            DeviceType.FAN,
                            null
                    )
            );

            service.createDevice(
                    new DeviceCreationRequest("Kitchen Fan",
                            "Kitchen",
                            DeviceType.FAN,
                            null
                    )
            );

            service.createDevice(
                    new DeviceCreationRequest("Living Room Ceiling Light",
                            "Living Room",
                            DeviceType.LIGHT,
                            null
                    )
            );

            service.createDevice(
                    new DeviceCreationRequest("Bathroom Lights",
                            "Bathroom",
                            DeviceType.LIGHT,
                            null
                    )
            );

        }
    }
}
