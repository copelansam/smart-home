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
import java.util.UUID;

@RestController
@RequestMapping("api/devices")
public class SmartDeviceController {

    private final SmartDeviceService deviceService;
    private final DeviceLogRepository deviceLogRepository;

    public SmartDeviceController(SmartDeviceService deviceService, DeviceLogRepository deviceLogRepository){
        this.deviceService = deviceService;
        this.deviceLogRepository = deviceLogRepository;
    }

    @GetMapping
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<DeviceDTO>> getDevices(
            @RequestParam(required = false)DeviceType type,
            @RequestParam(required = false)String location,
            @RequestParam(required = false)Boolean isOn
            ){

        List<ISmartDevice> device = deviceService.getDevices(type,location,isOn);
        List<DeviceDTO> deviceDtos = deviceService.deviceListToDto(device);
        return ResponseEntity.ok(Collections.unmodifiableList(deviceDtos));
    }

    @GetMapping("/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<ISmartDevice> getDeviceById(
            @PathVariable("id")UUID id) {
        ISmartDevice device = deviceService.getDeviceById(id);
        System.out.println(device.getExtraProperties());
        return ResponseEntity.ok(device);
    }

    @PostMapping("/create-device")
    @CrossOrigin(origins = "*")
    public ResponseEntity<CallResult> createNewDevice(@RequestBody DeviceCreationRequest request){

        CallResult result = deviceService.createDevice(request);

        if (!result.getIsSuccess()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, result.getMessage());
        }
        else{
            return ResponseEntity.ok(result);
        }
    }

    @DeleteMapping("/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Boolean> deleteDevice(@PathVariable UUID id){

        boolean deleted = deviceService.deleteDeviceById(id);
        return ResponseEntity.ok(deleted);
    }

    @PutMapping("/{id}/state")
    @CrossOrigin(origins = "*")
    public ResponseEntity<CallResult> executeAction(@PathVariable("id") UUID id,
                                                    @RequestParam(required = true) String action){
        System.out.println("Transition: " + action);
        CallResult result = deviceService.executeAction(id, action);

        if (!result.getIsSuccess()){

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, result.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/history")
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<DeviceLog>> getDeviceLogs(@PathVariable("id") UUID uuid){

        List<DeviceLog> logs = deviceLogRepository.findByDeviceIdOrderByTimestampDesc(uuid);
        return ResponseEntity.ok(logs);
    }
}
