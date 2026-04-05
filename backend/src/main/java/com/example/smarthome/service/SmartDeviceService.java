package com.example.smarthome.service;

import com.example.smarthome.domain.smartdevices.devicefactories.ISmartDeviceFactory;
import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import com.example.smarthome.repository.SmartDeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Service
public class SmartDeviceService {

    private final SmartDeviceRepository repo;
    private final ISmartDeviceFactory deviceFactory;

    public SmartDeviceService(SmartDeviceRepository repo, ISmartDeviceFactory deviceFactory){
        this.repo = repo;
        this.deviceFactory = deviceFactory;
    }

    /**
     * Creates a new device and saves it to the repository
     *
     * @param name        Name of the device
     * @param location    Location of the device
     * @param deviceType  The type of the device
     * @param attributes  A map of device specific attributes (brightness percentage, color, etc.)
     *
     */
    @PostMapping
    public void createDevice(String name, String location, DeviceType deviceType, Map<String, Object> attributes){

        // Create the new device
        SmartDeviceBase newDevice = deviceFactory.createDevice(name, location, deviceType, attributes);

        // Save the device to the database
        repo.save(newDevice);

    }



}
