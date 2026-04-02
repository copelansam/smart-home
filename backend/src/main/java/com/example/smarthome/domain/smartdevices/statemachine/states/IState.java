package com.example.smarthome.domain.smartdevices.statemachine.states;

import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;

import java.util.List;

public interface IState {
    public TransitionResult execute();
}