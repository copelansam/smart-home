package com.example.smarthome.domain.smartdevices.statemachine.states;

import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;

import java.util.List;

public interface IState <T extends ITransition<?>, D extends ISmartDevice>{

    // This method will be responsible for executing a transition in the statechart
    TransitionResult execute(T transition, D device);

    // This method will return the state's available transitions
    public List<ITransition<?>> provideAvailableTransitions();

    public String getName();
}