package com.example.smarthome.domain.smartdevices.devicefactories;

import com.example.smarthome.controller.DeviceCreationRequest;
import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import com.example.smarthome.domain.smartdevices.devices.smartdoorlock.SmartDoorLock;
import com.example.smarthome.domain.smartdevices.devices.smartfan.SmartFan;
import com.example.smarthome.domain.smartdevices.devices.smartlight.SmartLight;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.ThermostatMode;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * Concrete factory for creating smart device instances based on {@link DeviceType}.
 *
 * Uses the {@link DeviceCreationRequest} to determine which type of device to create.
 *
 * This factory is responsible only for object creation and does not persist devices.
 */
@Component
public class SmartDeviceFactory implements ISmartDeviceFactory{

    /**
     * Creates a smart device instance based on the provided request.
     *
     * The device type determines which concrete implementation is created.
     *
     * @param request the request containing device configuration data
     * @return a newly created SmartDeviceBase instance
     * @throws IllegalArgumentException if the device type is unsupported
     */
    public SmartDeviceBase createDevice(DeviceCreationRequest request){

        switch (request.deviceType()){

            case FAN:
                return new SmartFan(request.name(), request.location());

            case LIGHT:
               return new SmartLight(request.name(), request.location());

            case DOORLOCK:
                return new SmartDoorLock(request.name(), request.location());

            case THERMOSTAT:
               return new SmartThermostat(request.name(), request.location());
            default:
                throw new IllegalArgumentException("Selected device type either not supported or empty");
        }
    }
}
