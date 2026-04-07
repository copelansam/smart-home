package com.example.smarthome.controller;


import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.service.SmartDeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/devices")
public class SmartDeviceController {

    private final SmartDeviceService deviceService;

    public SmartDeviceController(SmartDeviceService deviceService){
        this.deviceService = deviceService;
    }

    @GetMapping
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<List<ISmartDevice>> getDevices(
            @RequestParam(required = false)DeviceType type,
            @RequestParam(required = false)String location,
            @RequestParam(required = false)Boolean isOn
            ){

        List<ISmartDevice> device = deviceService.getDevices(type,location,isOn);
        return ResponseEntity.ok(device);
    }
}
