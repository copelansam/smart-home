package com.example.smarthome.domain.smartdevices.devicefactories;

import com.example.smarthome.controller.DeviceCreationRequest;
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

    public SmartDeviceBase createDevice(DeviceCreationRequest request){

        switch (request.deviceType){

            case FAN:
                return new SmartFan(request.name, request.location);

            case LIGHT:
                // If there isn't a brightness and color set, use the constructor that assigns default values
                if (request.attributes == null){
                    return new SmartLight(request.name, request.location);
                }
                else { // Otherwise pass properties into the constructor appropriately
                    return new SmartLight(request.name, request.location,
                            ((Number) request.attributes.get("brightnessPercentage")).intValue(), // Bast brightness and colors to ints
                            ((Number) request.attributes.get("redValue")).intValue(),
                            ((Number) request.attributes.get("greenValue")).intValue(),
                            ((Number) request.attributes.get("blueValue")).intValue()
                    );
                }

            case DOORLOCK:
                return new SmartDoorLock(request.name, request.location);

            case THERMOSTAT:
               return new SmartThermostat(request.name, request.location,
                       ((Number) request.attributes.get("desiredTemperature")).doubleValue(), // Cast the temperature values to doubles
                       ((Number) request.attributes.get("ambientTemperature")).doubleValue());

            default:
                throw new IllegalArgumentException("Selected device type either not supported or empty");
        }
    }
}
