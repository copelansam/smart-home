package com.example.smarthome.domain.smartdevices.statemachine.states;

import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;

import java.util.List;
import java.util.Map;

public interface IState <D extends ISmartDevice>{

    // This method will be responsible for executing a transition in the statechart
    CallResult execute(String transition, D device, Map<String, Object> parameters);

    // This method will return the state's available transitions
    List<ITransition<?>> provideAvailableTransitions();

    List<ITransition<?>> provideUpdatableFields();

    String getName();
}