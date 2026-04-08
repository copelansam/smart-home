package com.example.smarthome.domain.smartdevices.devices;

import com.example.smarthome.domain.smartdevices.statemachine.states.IState;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ISmartDevice {

    void setState(IState newState);

    boolean getIsOn();

    String getLocation();

    DeviceType getDeviceType();

    String getName();

    Map<String, Object> getExtraProperties();

    String getState();

    List<ITransition<?>> getAvailableTransitions();

    UUID getUuid();
}
