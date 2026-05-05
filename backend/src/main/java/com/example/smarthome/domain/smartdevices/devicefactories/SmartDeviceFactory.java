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
 * Uses the {@link DeviceCreationRequest} to determine which type of device to create
 * and extracts any device-specific attributes required for initialization.
 *
 * Some device types require additional configuration provided via the
 * {@code attributes} map in the request.
 *
 * This factory is responsible only for object creation and does not persist devices.
 */
@Component
public class SmartDeviceFactory implements ISmartDeviceFactory{

    /**
     * Creates a smart device instance based on the provided request.
     *
     * The device type determines which concrete implementation is created.
     * Additional attributes may be required for certain device types (e.g., thermostat, light)
     * and are expected to be present in the request's attribute map.
     *
     * @param request the request containing device configuration data
     * @return a newly created SmartDeviceBase instance
     * @throws IllegalArgumentException if the device type is unsupported or required attributes are missing
     */
    public SmartDeviceBase createDevice(DeviceCreationRequest request){

        switch (request.deviceType()){

            case FAN:
                return new SmartFan(request.name(), request.location());

            case LIGHT:
                // If there isn't a brightness and color set, use the constructor that assigns default values
                if (request.attributes() == null){
                    return new SmartLight(request.name(), request.location());
                }
                else { // Otherwise pass properties into the constructor appropriately
                    return new SmartLight(request.name(), request.location(),
                            ((Number) request.attributes().get("brightnessPercentage")).intValue(), // Bast brightness and colors to ints
                            ((Number) request.attributes().get("redValue")).intValue(),
                            ((Number) request.attributes().get("greenValue")).intValue(),
                            ((Number) request.attributes().get("blueValue")).intValue()
                    );
                }

            case DOORLOCK:
                return new SmartDoorLock(request.name(), request.location());

            case THERMOSTAT:
                // If the attributes are empty, create a default thermostat
                if (request.attributes() == null){
                    return new SmartThermostat(request.name(), request.location());
                }

               return new SmartThermostat(request.name(), request.location(),
                       ((Number) request.attributes().get("desiredTemperature")).doubleValue(), // Cast the temperature values to doubles
                       ((Number) request.attributes().get("ambientTemperature")).doubleValue());

            default:
                throw new IllegalArgumentException("Selected device type either not supported or empty");
        }
    }
}
