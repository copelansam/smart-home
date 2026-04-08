package com.example.smarthome.domain.smartdevices.devicefactories;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import com.example.smarthome.domain.smartdevices.devices.smartdoorlock.SmartDoorLock;
import com.example.smarthome.domain.smartdevices.devices.smartfan.SmartFan;
import com.example.smarthome.domain.smartdevices.devices.smartlight.SmartLight;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import org.springframework.stereotype.Component;

import java.util.Map;


// This class will be responsible for the creation of new devices. Any attribute that is specific to a certain device
// type is stored in a map and will be retrieved in the constructor of each object
@Component
public class SmartDeviceFactory implements ISmartDeviceFactory{

    public SmartDeviceBase createDevice(String name, String location, DeviceType deviceType){

        switch (deviceType){

            case FAN:
                return new SmartFan(name, location, DeviceType.FAN);

            case LIGHT:
                return new SmartLight(name, location, DeviceType.LIGHT);

            case DOORLOCK:
                return new SmartDoorLock(name, location, DeviceType.DOORLOCK);

            case THERMOSTAT:
               return new SmartThermostat(name, location, DeviceType.THERMOSTAT);

            default:
                throw new IllegalArgumentException("You must input all requested information");
        }
    }
}
