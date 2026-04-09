package com.example.smarthome.service;

import com.example.smarthome.domain.devicequeries.IDeviceQuery;
import com.example.smarthome.domain.devicequeries.QueryBuilder;
import com.example.smarthome.domain.smartdevices.devicefactories.ISmartDeviceFactory;
import com.example.smarthome.domain.smartdevices.devices.DeviceDTO;
import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;
import com.example.smarthome.repository.DeviceLogRepository;
import com.example.smarthome.repository.ISmartDeviceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;

@Service
public class SmartDeviceService {

    private final ISmartDeviceRepository repo;
    private final ISmartDeviceFactory deviceFactory;
    private final QueryBuilder queryBuilder;
    private final DeviceLogRepository deviceLogRepository;

    public SmartDeviceService(ISmartDeviceRepository repo,
                              ISmartDeviceFactory deviceFactory,
                              QueryBuilder queryBuilder,
                              DeviceLogRepository deviceLogRepository){
        this.repo = repo;
        this.deviceFactory = deviceFactory;
        this.queryBuilder = queryBuilder;
        this.deviceLogRepository = deviceLogRepository;
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

        return Collections.unmodifiableList(query.getItems());
    }

    /**
     * Creates a new device and saves it to the repository
     *
     * @param name        Name of the device
     * @param location    Location of the device
     * @param deviceType  The type of the device
     *                    *
     */
    @PostMapping
    public void createDevice(String name, String location, DeviceType deviceType){

        // Create the new device
        SmartDeviceBase newDevice = deviceFactory.createDevice(name, location, deviceType);

        // Save the device to the database
        repo.save(newDevice);

    }

    public ISmartDevice getDeviceById(UUID uuid){

        return repo.findById(uuid).orElse(null);
    }


    public boolean deleteDeviceById(UUID uuid){

        if (repo.existsById(uuid)){
            repo.deleteById(uuid);
            return true;
        }
        else{
            return false;
        }
    }

    public List<DeviceDTO> deviceListToDto(List<ISmartDevice> devices){

        List<DeviceDTO> deviceDtos = new ArrayList<>();

        for (ISmartDevice device : devices){
            deviceDtos.add(DeviceDTO.fromISmartDevice(device));
        }

        return Collections.unmodifiableList(deviceDtos);
    }

    public TransitionResult executeAction(UUID uuid, String transition){
        ISmartDevice device = repo.getReferenceById(uuid);

        TransitionResult result = device.execute(transition);

        if (result.getIsSuccess()){
            repo.save((SmartDeviceBase) device);
            deviceLogRepository.save(result.getLog());
        }
        return result;
    }

    public List<ISmartDevice> getAllThermostats(){

        List<ISmartDevice> thermostats = (queryBuilder.buildQuery(DeviceType.THERMOSTAT, null, null)).getItems();

        return thermostats;
    }

    @Transactional
    public void saveDeviceUpdate(ISmartDevice device){
        repo.save((SmartDeviceBase) device);
    }
}
