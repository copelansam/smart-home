package com.example.smarthome.domain.smartdevices.devicefactories;

import com.example.smarthome.controller.DeviceCreationRequest;
import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;

import java.util.Map;


/**
 * Factory interface for creating {@link SmartDeviceBase} instances.
 *
 * Implementations determine the specific device type to create based on
 * the provided {@link DeviceCreationRequest}. This factory is responsible
 * only for object creation and does not handle persistence.
 */
public interface ISmartDeviceFactory {

    /**
     * Creates a new smart device instance based on the given request.
     *
     * @param request the request containing device configuration data
     * @return a newly created SmartDeviceBase instance
     */
    SmartDeviceBase createDevice(DeviceCreationRequest request);
}
