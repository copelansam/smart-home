package com.example.smarthome.service;

import com.example.smarthome.domain.devicequeries.IDeviceQuery;
import com.example.smarthome.domain.devicequeries.QueryBuilder;
import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.devicefactories.ISmartDeviceFactory;
import com.example.smarthome.domain.smartdevices.devices.DeviceDTO;
import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import com.example.smarthome.exception.DeviceNotFoundException;
import com.example.smarthome.exception.NoDevicesExcpetion;
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
    public CallResult createDevice(String name, String location, DeviceType deviceType){

        // Create the new device
        SmartDeviceBase newDevice = deviceFactory.createDevice(name, location, deviceType);

        if (newDevice == null){
            return new CallResult("Item creation failed", false, null);
        }
        else {
            CallResult result = new CallResult(newDevice.getName() + "Successfully created", true,
                    new DeviceLog(newDevice.getUuid(),newDevice.getName() + " was created successfully"));
            // Save the device to the database
            repo.save(newDevice);
            deviceLogRepository.save(result.getLog());
            return result;
        }
    }

    public ISmartDevice getDeviceById(UUID uuid){

        ISmartDevice device = repo.findById(uuid).orElse(null);

        if (device == null){
            throw new DeviceNotFoundException("Device with this ID was not found.");
        }
        else {
            return device;
        }
    }


    public boolean deleteDeviceById(UUID uuid){

        if (repo.existsById(uuid)){
            repo.deleteById(uuid);
            deviceLogRepository.save(new DeviceLog(uuid, "Device Deletion", "This device has been deleted."));
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

    public CallResult executeAction(UUID uuid, String transition){
        ISmartDevice device = repo.getReferenceById(uuid);

        CallResult result = device.execute(transition);

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

    @Transactional
    public String updateLocationAmbientTemperature(String location, double temperature){

        IDeviceQuery query = queryBuilder.buildQuery(DeviceType.THERMOSTAT, location, null);

        List<ISmartDevice> thermostats = query.getItems();

        if (thermostats.isEmpty()){
            throw new DeviceNotFoundException("Thermostat in " + location + " not found. Either it does not exist or it was deleted.");
        }
        else{
            ISmartDevice thermostat = thermostats.get(0);
            ((SmartThermostat) thermostat).setAmbientTemperature(temperature);
            deviceLogRepository.save(new DeviceLog(thermostat.getUuid(), "Ambient Temperature in: " + location + " was updated to: " + temperature));
            repo.save((SmartDeviceBase) thermostat);
            return "Ambient Temperature in: " + location + " was updated to: " + temperature;
        }
    }

    @Transactional
    public int resetAllDevices(){

        List<ISmartDevice> devices = getDevices(null,null,null);

        if (devices.isEmpty()){
            throw new NoDevicesExcpetion("There are no devices in the smart home, therefore I cannot reset them");
        }

        for(ISmartDevice device : devices){
            device.factoryReset();
            repo.save((SmartDeviceBase) device);

            deviceLogRepository.save(
                    new DeviceLog(
                            device.getUuid(),
                    "Device reset to factory settings"));
        }
        return devices.size();
    }
}
