package com.example.smarthome.repository;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.smartdoorlock.SmartDoorLock;
import com.example.smarthome.domain.smartdevices.devices.smartfan.SmartFan;
import com.example.smarthome.domain.smartdevices.devices.smartlight.SmartLight;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// This class provides the seed data that will be added to the database upon initial start up
@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final SmartDeviceRepository repo;

    public DatabaseSeeder(SmartDeviceRepository repo){
        this.repo = repo;
    }

    @Override
    public void run(String... args){

        repo.flush();

        // If there are no records in the database, add seed data. Otherwise, skip this
        if (repo.count() == 0){

            repo.save(new SmartThermostat("Living Room Thermostat", "Living Room", DeviceType.THERMOSTAT, 76, 78));
            repo.save(new SmartDoorLock("Garage Door Lock", "Garage", DeviceType.DOORLOCK));
            repo.save(new SmartDoorLock("Front Door Lock", "Living Room", DeviceType.DOORLOCK));
            repo.save(new SmartFan("Bedroom Box Fan", "Bedroom", DeviceType.FAN));
            repo.save(new SmartFan("Kitchen Fan","Kitchen",DeviceType.FAN));
            repo.save(new SmartLight("Living Room Ceiling Light", "Living Room", DeviceType.LIGHT, 32, 67,89,123));
            repo.save(new SmartLight("Bathroom Lights", "Bathroom", DeviceType.LIGHT, 55, 100, 0, 0));

        }
    }
}
