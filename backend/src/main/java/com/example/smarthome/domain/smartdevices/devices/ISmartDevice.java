package com.example.smarthome.domain.smartdevices.devices;

import com.example.smarthome.domain.smartdevices.statemachine.states.IState;

public interface ISmartDevice {

    void setState(IState newState);

    boolean getIsOn();

    String getLocation();

    DeviceType getDeviceType();
}
