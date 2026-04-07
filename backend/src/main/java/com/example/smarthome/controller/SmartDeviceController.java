package com.example.smarthome.controller;


import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.service.SmartDeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/devices")
public class SmartDeviceController {

    private final SmartDeviceService deviceService;

    public SmartDeviceController(SmartDeviceService deviceService){
        this.deviceService = deviceService;
    }

    @GetMapping("get-all-devices")
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<ISmartDevice>> getDevices(
            @RequestParam(required = false)DeviceType type,
            @RequestParam(required = false)String location,
            @RequestParam(required = false)Boolean isOn
            ){

        List<ISmartDevice> device = deviceService.getDevices(type,location,isOn);
        return ResponseEntity.ok(device);
    }

    @PostMapping("/create-device")
    @CrossOrigin(origins = "*")
    public void createNewDevice(@RequestBody DeviceCreationRequest request){

        deviceService.createDevice(request.name, request.location, request.deviceType, request.attributes);
    }
}
