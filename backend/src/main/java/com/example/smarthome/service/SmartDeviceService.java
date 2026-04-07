package com.example.smarthome.service;

import com.example.smarthome.domain.devicequeries.IDeviceQuery;
import com.example.smarthome.domain.devicequeries.QueryBuilder;
import com.example.smarthome.domain.smartdevices.devicefactories.ISmartDeviceFactory;
import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import com.example.smarthome.repository.ISmartDeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SmartDeviceService {

    private final ISmartDeviceRepository repo;
    private final ISmartDeviceFactory deviceFactory;
    private final QueryBuilder queryBuilder;

    public SmartDeviceService(ISmartDeviceRepository repo, ISmartDeviceFactory deviceFactory, QueryBuilder queryBuilder){
        this.repo = repo;
        this.deviceFactory = deviceFactory;
        this.queryBuilder = queryBuilder;
    }

    /**
     *
     * @param type      The type of devices that the user wants to retrieve. (default is all types)
     * @param location  The location that the user want to retrieve devices from (default is all locations)
     * @param isOn      The power status of devices that the users wants to retrieve (default is all devices)
     * @return  List of devices
     */
    public List<ISmartDevice> getDevices(DeviceType type, String location, Boolean isOn){

        IDeviceQuery query = queryBuilder.buildQuery(type, location, isOn);

        return query.getItems();
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

    public ISmartDevice getDeviceById(UUID uuid){

        return repo.findById(uuid).orElse(null);
    }
}
