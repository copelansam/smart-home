package com.example.smarthome.domain.smartdevices.devicefactories;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
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

    public SmartDeviceBase createDevice(String name, String location, DeviceType deviceType, Map<String, Object> attributes){

        switch (deviceType){

            case FAN:
                return new SmartFan(name, location, DeviceType.FAN);

            case LIGHT:
                if (attributes.containsKey("brightness") // Check that the relevant attributes were added to the map 
                        && attributes.containsKey("red") // to ensure that the constructor can be properly called
                        && attributes.containsKey("green") 
                        && attributes.containsKey("blue")) {
                    return new SmartLight(name, location, DeviceType.LIGHT,
                            ((Number) attributes.get("brightness")).intValue(), // Cast to int
                            ((Number) attributes.get("red")).intValue(), // Cast to int
                            ((Number) attributes.get("green")).intValue(), // Cast to int
                            ((Number) attributes.get("blue")).intValue()); // Cast to int
                }
                else{
                    throw new IllegalArgumentException("You must input all requested information");
                }

            case DOORLOCK:
                return new SmartDoorLock(name, location, DeviceType.DOORLOCK);

            case THERMOSTAT:
                if (attributes.containsKey("desired temp")  // Check that the relevant attributes were added to the map 
                        && attributes.containsKey("ambient temp")) { // to ensure that the constructor can be properly called
                    return new SmartThermostat(name, location, DeviceType.THERMOSTAT,
                            ((Number) attributes.get("desired temp")).intValue(), // Cast to int
                            ((Number) attributes.get("ambient temp")).intValue()); // Cast to int
                }
                else{
                    throw new IllegalArgumentException("You must input all requested information");
                }

            default:
                throw new IllegalArgumentException("You must input all requested information");
        }
    }
}
