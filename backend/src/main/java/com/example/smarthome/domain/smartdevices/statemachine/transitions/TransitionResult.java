package com.example.smarthome.domain.smartdevices.statemachine.transitions;

public class TransitionResult {

    private String message;
    private boolean success;

    public TransitionResult(){
        this.message = "Invalid Transition from current state";
        this.success = false;
    }

    public TransitionResult(String message, boolean success){
        this.message = message;
        this.success = success;
    }
}
