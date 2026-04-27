package com.example.smarthome.domain.smartdevices.devicefactories;

import com.example.smarthome.controller.DeviceCreationRequest;
import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;

import java.util.Map;


/**
 *  Factory for creating SmartDevice instances based on device type.
 *  This factory only creates the device in memory, it does not persist them.
 */
public interface ISmartDeviceFactory {

    /**
     * Method for creating SmartDevice instances based on device type.
     * Created device is **not persisted**, persistence must happen elsewhere
     * @return A new SmartDeviceBase instance
     */
    SmartDeviceBase createDevice(DeviceCreationRequest request);
}
