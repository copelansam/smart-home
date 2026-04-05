package com.example.smarthome.controller;


import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import com.example.smarthome.service.SmartDeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/devices")
public class SmartDeviceController {

    private final SmartDeviceService deviceService;

    public SmartDeviceController(SmartDeviceService deviceService){
        this.deviceService = deviceService;
    }

    @GetMapping
    public ResponseEntity<List<SmartDeviceBase>> getDevices(
            @RequestParam(required = false)DeviceType type,
            @RequestParam(required = false)String location,
            @RequestParam(required = false)Boolean isOn
            ){

        List<SmartDeviceBase> device = deviceService.getDevices(type,location,isOn);
        return ResponseEntity.ok(device);
    }
}
