package com.example.smarthome.domain.smartdevices.statemachine.transitions;

import com.example.smarthome.domain.history.DeviceLog;

public class TransitionResult {

    private String message;
    private boolean success;
    private DeviceLog log;

    public TransitionResult(){
        this.message = "Invalid Transition from current state";
        this.success = false;
        this.log = null;
    }

    public TransitionResult(String message, boolean success, DeviceLog log) {
        this.message = message;
        this.success = success;
        this.log = log;
    }

    public DeviceLog getLog(){
        return this.log;
    }

    public boolean getIsSuccess(){
        return this.success;
    }

    public String getMessage(){
        return this.message;
    }
}
