package com.example.smarthome.domain.smartdevices.devices;

import com.example.smarthome.domain.smartdevices.statemachine.states.IState;

public interface ISmartDevice {

    public void setState(IState newState);
}
