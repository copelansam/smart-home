package com.example.smarthome.controller;


import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.devices.DeviceDTO;
import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import com.example.smarthome.repository.DeviceLogRepository;
import com.example.smarthome.service.SmartDeviceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/***
 * REST controller for managing smart devices
 *
 * provides endpoints to load, create, delete, update, and view logs for smart devices
 */
@RestController
@RequestMapping("api/devices")
public class SmartDeviceController {

    private final SmartDeviceService deviceService;
    private final DeviceLogRepository deviceLogRepository;

    public SmartDeviceController(SmartDeviceService deviceService, DeviceLogRepository deviceLogRepository){
        this.deviceService = deviceService;
        this.deviceLogRepository = deviceLogRepository;
    }

    /***
     * Retrieves a list of smart devices filtered by type, location, and power status
     *
     * @param type type of device selected by the user (null retrieves all device types)
     * @param location location of devices selected by the user (null retrieves all locations)
     * @param isOn power status of devices selected by the user (null retrieves on and off devices)
     * @return list of devices converted to DeviceDTO that meet the user specified filters
     */
    @GetMapping
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<DeviceDTO>> getDevices(
            @RequestParam(required = false)DeviceType type,
            @RequestParam(required = false)String location,
            @RequestParam(required = false)Boolean isOn
            ){

        System.out.println("Retrieving devices with these filters: Type: " + type + " location: " + location + " isOn: " + isOn);

        // Retrieve a list of smart devices based on the specified filters
        List<ISmartDevice> device = deviceService.getDevices(type,location,isOn);

        // Convert the devices to DTOs before sending them off to the client
        List<DeviceDTO> deviceDtos = deviceService.deviceListToDto(device);

        return ResponseEntity.ok(Collections.unmodifiableList(deviceDtos));
    }

    /***
     * Retrieves a smart device based on the id specified by the client
     *
     * @param id id of the device that the client is looking for
     * @return The device that the client is looking for
     */
    @GetMapping("/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<ISmartDevice> getDeviceById(
            @PathVariable("id")UUID id) {

        // Retrieve the smart device by id
        ISmartDevice device = deviceService.getDeviceById(id);

        // return the device
        return ResponseEntity.ok(device);
    }

    /***
     * Creates a smart device based on the request sent by the client
     *
     * @param request An object that contains all of the information needed to create a device
     * @return A response entity that signals whether the device was successfully created or not
     */
    @PostMapping("/create-device")
    @CrossOrigin(origins = "*")
    public ResponseEntity<CallResult> createNewDevice(@RequestBody DeviceCreationRequest request){

        // Pass the creation request to the service so it can try to make the device and store the result
        CallResult result = deviceService.createDevice(request);

        // if the device couldn't be created, return an error result
        if (!result.getIsSuccess()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, result.getMessage());
        }
        else{ // otherwise return a result saying it was successful
            return ResponseEntity.ok(result);
        }
    }

    /***
     * Deletes the smart device with the id specified by the client
     *
     * @param id id of the device to be deleted
     * @return A response entity that says whether or not the device was successfully deleted
     */
    @DeleteMapping("/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Boolean> deleteDevice(@PathVariable UUID id){

        // Pass id to the service layer so it can delete the device
        boolean deleted = deviceService.deleteDeviceById(id);

        if (deleted == true){

            // return a response saying it was successful
            return ResponseEntity.ok(deleted);
        }

    }

    /***
     * Performs an action specified by the client with optional parameters for updating device attributes
     *
     * @param id id of the device to perform an action on
     * @param action The action to be performed on the device
     * @param parameters Any sort of values that are to be updated during certain actions (updating device fields)
     * @return A response entity that says whether the update was successful
     */
    @PutMapping("/{id}/state")
    @CrossOrigin(origins = "*")
    public ResponseEntity<CallResult> executeAction(@PathVariable("id") UUID id,
                                                    @RequestParam(required = true) String action,
                                                    @RequestBody(required = false) Map<String, Object> parameters){
        System.out.println("Transition: " + action);
        System.out.println("Parameters: " + parameters);

        // pass the parameters to the service so it can try to execute the action
        CallResult result = deviceService.executeAction(id, action, parameters);

        // if the action failed, pass an error
        if (!result.getIsSuccess()){

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, result.getMessage());
        }
        else { // otherwise, return a result that says that it was successful
            return ResponseEntity.ok(result);
        }
    }

    /***
     * Retrieves a list of device logs for the client specified device
     *
     * @param uuid uuid of the device the user wants logs of
     * @return a list of logs for the specified device
     */
    @GetMapping("/{id}/history")
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<DeviceLog>> getDeviceLogs(@PathVariable("id") UUID uuid){

        List<DeviceLog> logs = deviceLogRepository.findByDeviceIdOrderByTimestampDesc(uuid);
        return ResponseEntity.ok(logs);
    }
}
