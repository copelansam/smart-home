package com.example.smarthome.domain.smartdevices.statemachine.transitions.doorlocktransition;

import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;

public class DoorTransition implements ITransition<DoorLockAction> {

    private DoorLockAction action;

    public DoorTransition(DoorLockAction action){
        this.action = action;
    }

    @Override
    public DoorLockAction getAction() {
        return action;
    }
}
