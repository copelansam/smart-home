package com.example.smarthome.controller;


import com.example.smarthome.domain.smartdevices.devices.DeviceDTO;
import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.service.SmartDeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/devices")
public class SmartDeviceController {

    private final SmartDeviceService deviceService;

    public SmartDeviceController(SmartDeviceService deviceService){
        this.deviceService = deviceService;
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

        if (device != null) {
            return ResponseEntity.ok(device);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create-device")
    @CrossOrigin(origins = "*")
    public void createNewDevice(@RequestBody DeviceCreationRequest request){

        deviceService.createDevice(request.name, request.location, request.deviceType, request.attributes);
    }

    @DeleteMapping("/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Boolean> deleteDevice(@PathVariable UUID id){

        boolean deleted = deviceService.deleteDeviceById(id);
        return ResponseEntity.ok(deleted);
    }

    @PutMapping("/{id}/state")
    @CrossOrigin(origins = "*")
    public void executeAction(@PathVariable("id") UUID id,
                              @RequestParam(required = true) String transition){
        deviceService.executeAction(id, transition);
    }
}
