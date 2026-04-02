package com.example.smarthome.domain.smartdevices.statemachine.transitions.fantransition;

import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;

public class FanTransition implements ITransition<FanAction> {

    private FanAction action;

    public FanTransition(FanAction action){
        this.action = action;
    }

    @Override
    public FanAction getAction() {
        return action;
    }
}
