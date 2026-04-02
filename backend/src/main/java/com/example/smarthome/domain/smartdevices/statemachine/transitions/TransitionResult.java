package com.example.smarthome.domain.smartdevices.statemachine.transitions;

import com.example.smarthome.domain.smartdevices.statemachine.states.IState;

public class TransitionResult {

    private String message;
    private boolean success;
    private IState newState;

    public TransitionResult(String message, boolean success, IState newState){
        this.message = message;
        this.success = success;
        this.newState = newState;
    }

    public IState getNewState(){
        return this.newState;
    }
}
