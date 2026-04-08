package com.example.smarthome.domain.smartdevices.devices;

import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeviceDTO {

    public UUID uuid;
    public String name;
    public String location;
    public String deviceType;
    public boolean isOn;
    public String state;
    public List<ITransition<?>> availableActions;

    public Map<String,Object> properties;

    public static DeviceDTO fromISmartDevice(ISmartDevice device){

        DeviceDTO dto = new DeviceDTO();
        dto.uuid = device.getUuid();
        dto.name = device.getName();
        dto.location = device.getLocation();
        dto.deviceType = device.getDeviceType().getDeviceTypeName();
        dto.isOn = device.getIsOn();
        dto.state = device.getState();
        dto.availableActions = device.getAvailableTransitions();


        dto.properties = device.getExtraProperties();

        return dto;
    }

}
