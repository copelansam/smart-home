package com.example.smarthome.repository;

import com.example.smarthome.controller.DeviceCreationRequest;
import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.smartdoorlock.SmartDoorLock;
import com.example.smarthome.domain.smartdevices.devices.smartfan.SmartFan;
import com.example.smarthome.domain.smartdevices.devices.smartlight.SmartLight;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.service.SmartDeviceService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

// This class provides the seed data that will be added to the database upon initial start up
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

        repo.flush();

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
                            DeviceType.DOORLOCK
                    )
            );

            service.createDevice(
                    new DeviceCreationRequest("Front Door Lock",
                            "Living Room",
                            DeviceType.DOORLOCK
                    )
            );

            service.createDevice(
                    new DeviceCreationRequest("Bedroom Box Fan",
                            "Bedroom",
                            DeviceType.FAN
                    )
            );

            service.createDevice(
                    new DeviceCreationRequest("Kitchen Fan",
                            "Kitchen",
                            DeviceType.FAN
                    )
            );

            service.createDevice(
                    new DeviceCreationRequest("Living Room Ceiling Light",
                            "Living Room",
                            DeviceType.LIGHT
                    )
            );

            service.createDevice(
                    new DeviceCreationRequest("Bathroom Lights",
                            "Bathroom",
                            DeviceType.LIGHT
                    )
            );

        }
    }
}
