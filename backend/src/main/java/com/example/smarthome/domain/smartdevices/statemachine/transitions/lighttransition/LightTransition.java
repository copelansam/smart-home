package com.example.smarthome.domain.smartdevices.statemachine.transitions.lighttransition;

import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;

public class LightTransition implements ITransition<LightAction> {

    private final LightAction action;

    public LightTransition(LightAction action){
        this.action = action;
    }

    @Override
    public LightAction getAction() {
        return action;
    }
}
