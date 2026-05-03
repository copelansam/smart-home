package com.example.smarthome.service;

import com.example.smarthome.controller.DeviceCreationRequest;
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
import com.example.smarthome.exception.NoDevicesException;
import com.example.smarthome.exception.ThermostatAlreadyExistsException;
import com.example.smarthome.repository.DeviceLogRepository;
import com.example.smarthome.repository.ISmartDeviceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;

/**
 * Service responsible for managing smart devices in the system.
 *
 * Handles device lifecycle operations such as creation, retrieval, updates,
 * deletion, and state transitions. Also coordinates persistence, logging,
 * and query construction for device filtering.
 *
 * Acts as the central business logic layer between controllers and repositories.
 */
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
     * Retrieves a list of smart devices filtered by type, location, and power status
     *
     * @param type      The type of devices that the user wants to retrieve. (default is all types)
     * @param location  The location that the user want to retrieve devices from (default is all locations)
     * @param isOn      The power status of devices that the users wants to retrieve (default is all devices)
     * @return  List of devices based on filters
     */
    public List<ISmartDevice> getDevices(DeviceType type, String location, Boolean isOn){

        // Build the device query based on the supplied filters
        IDeviceQuery query = queryBuilder.buildQuery(type, location, isOn);

        // return an immutable list of the results of the query
        return Collections.unmodifiableList(query.getItems());
    }

    /**
     * Creates a new device and saves it to the repository
     *
     * @param request An object that contains all of the information needed to create a device
     * @return A call result that signals whether the device was successfully created or not
     *
     */
    @PostMapping
    public CallResult createDevice(DeviceCreationRequest request){

        // Format the location so each word starts with a capital letter
        request.setLocation(makeLocationPretty(request.location));

        // If the user tries to create a thermostat in a location that already has one, throw an exception
        if (request.deviceType == DeviceType.THERMOSTAT && doesLocationHaveThermostat(request.location)) {
            throw new ThermostatAlreadyExistsException(request.location + " already has a thermostat");
        }
        else {

            // Create the new device by passing the request into the factory
            SmartDeviceBase newDevice = deviceFactory.createDevice(request);

            // Save the device to the database
            saveDeviceUpdate(newDevice);

            // Create call result to save and send back to user
            CallResult result = new CallResult(newDevice.getName() + "Successfully created", true,
                    new DeviceLog(newDevice.getUuid(),"Device Creation", newDevice.getName() + " was created successfully"));

            // Save creation log
            deviceLogRepository.save(result.getLog());

            // return call result
            return result;
        }
    }

    /***
     * Retrieves a device with a client specified uuid
     *
     * @param uuid The uuid of the device to retrieve
     * @return The device with the specified uuid
     * @throws DeviceNotFoundException if the database does not contain a device with the specified uuid
     */
    public ISmartDevice getDeviceById(UUID uuid){

        // pass the uuid into the database and try to find the corresponding smart device. If there is not a device
        // with the specified uuid, set the device to null
        ISmartDevice device = repo.findById(uuid).orElse(null);

        // If the device was not found, throw an exception
        if (device == null){
            throw new DeviceNotFoundException("Device with this ID was not found.");
        }
        else { // Otherwise, return the device
            return device;
        }
    }


    /***
     * Deletes a device with a given uuid
     *
     * @param uuid The uuid of the device the client wants to delete
     * @return a boolean value that represents if the deletion was successful
     */
    public boolean deleteDeviceById(UUID uuid){

        // Check if a device with the specified uuid exists in the database
        if (repo.existsById(uuid)){

            // If it does, delete it from the database
            repo.deleteById(uuid);

            // Create a device log and save it so we have the full device lifecycle
            deviceLogRepository.save(new DeviceLog(uuid, "Device Deletion", "This device has been deleted."));

            // return that it was successful
            return true;
        }
        else{
            // If the device could not be found in the database, throw an exception
            throw new DeviceNotFoundException("Device with this ID cannot be found.");
        }
    }

    /***
     * Converts a list of ISmartDevices to a list of DeviceDTOs in preparation for sending them outside the system
     *
     * @param devices a list of devices to be converted
     * @return a list of DeviceDTOs that represent each object
     */
    public List<DeviceDTO> deviceListToDto(List<ISmartDevice> devices){

        // Initialize the list to return
        List<DeviceDTO> deviceDtos = new ArrayList<>();

        // Iterate through all of the ISmartDevices
        for (ISmartDevice device : devices){

            // call the DeviceDTO converter method to convert each device and add them to the dto list
            deviceDtos.add(DeviceDTO.fromISmartDevice(device));
        }

        // Return an immutable list of the DeviceDTOs
        return Collections.unmodifiableList(deviceDtos);
    }

    /***
     * Performs the client specified action with parameters on the specified device
     *
     * @param uuid The uuid of the smart device that will have the action performed on it
     * @param transition The action that will be passed into the device's statechart
     * @param parameters Optional parameters that may be applied depending on the action
     * @return A call result that represents whether the action succeeded
     */
    public CallResult executeAction(UUID uuid, String transition, Map<String, Object> parameters){

        // Retrieve the device
        ISmartDevice device = repo.getReferenceById(uuid);

        // Call the device's execute method, passing in the transition and parameters, and store the result
        CallResult result = device.execute(transition, parameters);

        // If the action was performed successfully, save the changes to the device and it's corresponding log
        if (result.getIsSuccess()){
            saveDeviceUpdate(device);
            deviceLogRepository.save(result.getLog());
        }
        else{ // Otherwise throw an exception passing in the message from the statechart machine
            throw new IllegalStateException(result.getMessage());
        }

        // return the result of the call
        return result;
    }

    /***
     * Retrieves a list of all of the thermostats, used for the thermostat simulation
     *
     * @return List of all thermostats
     */
    public List<ISmartDevice> getAllThermostats(){

        // Retrieve a list of all of the thermostats by building and executing a query for it.
        List<ISmartDevice> thermostats = (queryBuilder.buildQuery(DeviceType.THERMOSTAT, null, null)).getItems();

        // Return the list of thermostats
        return Collections.unmodifiableList(thermostats);
    }

    /***
     * Saves a device to the repository, used when a device is created or updated
     *
     * @param device The device to save
     */
    @Transactional
    public void saveDeviceUpdate(ISmartDevice device){

        // Save the device to the repository
        // Cast to SmartDeviceBase so that hibernate will save it. It cannot save interfaces
        repo.save((SmartDeviceBase) device);
    }

    /***
     * Updates the ambient location field of the client specified location
     *
     * @param location The location that needs its temperature updated
     * @param temperature The new ambient temperature value that will be assigned to the location
     * @return
     */
    @Transactional
    public String updateLocationAmbientTemperature(String location, double temperature){

        // Create the query that will retrieve the thermostat in the specified location
        IDeviceQuery query = queryBuilder.buildQuery(DeviceType.THERMOSTAT, location, null);

        // Execute the query and store the result, the query returns a list
        List<ISmartDevice> thermostats = query.getItems();

        if (thermostats.isEmpty()){ // If there is no thermostat in the specified location, throw an exception
            throw new DeviceNotFoundException("Thermostat in " + location + " not found. Either it does not exist or it was deleted.");
        }
        else{
            // Otherwise, retrieve the thermostat from the list, cast it to a thermostat and update its ambient temperature
            ISmartDevice thermostat = thermostats.get(0);
            ((SmartThermostat) thermostat).setAmbientTemperature(temperature);

            // Save the updates to the device repository and log it in the device log repository
            deviceLogRepository.save(new DeviceLog(thermostat.getUuid(),"Location Temperature Update" ,
                    "Ambient Temperature in: " + location + " was updated to: " + temperature));
            saveDeviceUpdate(thermostat);

            return "Ambient Temperature in: " + location + " was updated to: " + temperature;
        }
    }


    /***
     * Resets all devices to their factory settings
     *
     * @return the amount of devices that were reset
     */
    @Transactional
    public int resetAllDevices(){

        // Retrieve a list of all of the devices
        List<ISmartDevice> devices = getDevices(null,null,null);

        // If there are no devices, throw an exception
        if (devices.isEmpty()){
            throw new NoDevicesException("There are no devices in the smart home, therefore I cannot reset them");
        }

        // Iterate through all of the devices and execute their corresponding factory reset methods
        for(ISmartDevice device : devices){

            // factory reset
            device.factoryReset();

            // save changes to device repo
            saveDeviceUpdate(device);

            // Create a log of the reset
            deviceLogRepository.save(
                    new DeviceLog(
                            device.getUuid(),
                    "Reset device",
                    "Device reset to factory settings"));
        }

        // Return the number of devices reset
        return devices.size();
    }

    /***
     * Takes in a string that represents a location and normalize it
     *
     * @param location The string that will represent a location
     * @return A string that will have each word start with a capital letter and the rest be lowercase
     * @throws IllegalArgumentException if a location is not provided or if it is just a series of spaces
     */
    public String makeLocationPretty(String location) throws  IllegalArgumentException{

        // If there isn't a location provided or if it is just spaces, throw an exception
        if (location == null || location.isBlank()){
            throw new IllegalArgumentException("Location cannot be blank");
        }

        // Split the location into words that we can manipulate individually before reattaching
        String[] words = location.trim().toLowerCase().split("\\s+");
        StringBuilder prettyString = new StringBuilder();

        for (String word : words){
            // normalize the location formatting into Title Case
            prettyString.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        }
        // Trim off the trailing space and return
        return prettyString.toString().trim();
    }

    /***
     * Checks to see if the given location already has a thermostat.
     *
     * Invariant: Each location may only have 1 thermostat
     *
     * @param location the location to check for a thermostat
     * @return a boolean value to represent if there is a thermostat in the specified location
     */
    public boolean doesLocationHaveThermostat(String location){
        return repo.existsByDeviceTypeAndLocation(DeviceType.THERMOSTAT, location);
    }
}
