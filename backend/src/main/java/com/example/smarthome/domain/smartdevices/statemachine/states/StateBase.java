package com.example.smarthome.domain.smartdevices.statemachine.states;

import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;

import java.util.List;

public abstract class StateBase implements IState {
    public final String name;

    public StateBase(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    @Override
    public abstract TransitionResult execute();
}
