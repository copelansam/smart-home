package com.example.smarthome.controller;


import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.devices.DeviceDTO;
import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import com.example.smarthome.repository.DeviceLogRepository;
import com.example.smarthome.service.SmartDeviceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<DeviceDTO> getDeviceById(
            @PathVariable("id")UUID id) {

        // Retrieve the smart device by id
        ISmartDevice device = deviceService.getDeviceById(id);

        // convert the device to a DTO before sending it out
        DeviceDTO deviceDto = (deviceService.deviceListToDto(List.of(device))).get(0);

        // return the device DTO
        return ResponseEntity.ok(deviceDto);
    }

    /***
     * Creates a smart device based on the request sent by the client
     *
     * @param request An object that contains all of the information needed to create a device
     * @return A response entity that signals whether the device was successfully created or not
     */
    @PostMapping("/create-device")
    @CrossOrigin(origins = "*")
    public ResponseEntity<CallResult> createNewDevice(@RequestBody @Valid DeviceCreationRequest request){

        // Pass the creation request to the service so it can try to make the device and store the result
        CallResult result = deviceService.createDevice(request);

        // Send call result to the client
        return ResponseEntity.ok(result);
    }

    /***
     * Deletes the smart device with the id specified by the client
     *
     * @param id id of the device to be deleted.
     * @return A response entity that says the device was successfully deleted.
     */
    @DeleteMapping("/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> deleteDevice(@PathVariable UUID id){

        // Pass ID to the service layer so it can delete the device
        deviceService.deleteDeviceById(id);

        return ResponseEntity.noContent().build();
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
                                                    @RequestParam(required = true) @NotBlank String action,
                                                    @RequestBody(required = false) Map<String, Object> parameters){
        System.out.println("Transition: " + action);
        System.out.println("Parameters: " + parameters);

        // pass the parameters to the service so it can try to execute the action
        CallResult result = deviceService.executeAction(id, action, parameters);

        // Send the Call Result to the client
        return ResponseEntity.ok(result);

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
