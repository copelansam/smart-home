package com.example.smarthome.domain.smartdevices.devices;

import com.example.smarthome.domain.smartdevices.statemachine.states.IState;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;

import java.util.List;
import java.util.Map;

public interface ISmartDevice {

    void setState(IState newState);

    boolean getIsOn();

    String getLocation();

    DeviceType getDeviceType();

    String getName();

    Map<String, Object> getExtraProperties();

    public String getState();

    public List<ITransition<?>> getAvailableTransitions();
}
