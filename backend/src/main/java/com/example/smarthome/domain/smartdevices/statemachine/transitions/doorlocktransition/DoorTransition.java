package com.example.smarthome.domain.smartdevices.statemachine.transitions.doorlocktransition;

import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;

public class DoorTransition implements ITransition<DoorLockAction> {

    private DoorLockAction doorLockAction;

    public DoorTransition(DoorLockAction action){
        this.doorLockAction = action;
    }

    @Override
    public DoorLockAction getAction() {
        return doorLockAction;
    }

    @Override
    public String getLabel(){
        return this.doorLockAction.label;
    }
}
