package com.example.smarthome.domain.smartdevices.devicefactories;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;

import java.util.Map;

public interface ISmartDeviceFactory {

    public SmartDeviceBase createDevice(String name, String location, DeviceType deviceType, Map<String, Object> attributes);
}
