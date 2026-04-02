package com.example.smarthome.domain.smartdevices.statemachine.states;

import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;

import java.util.List;

public interface IState {

    // This method will be responsible for executing a transition in the statechart
    public TransitionResult execute();

    // This method will return the state's available transitions
    public List<ITransition<IState>> provideAvailableTransitions();
}