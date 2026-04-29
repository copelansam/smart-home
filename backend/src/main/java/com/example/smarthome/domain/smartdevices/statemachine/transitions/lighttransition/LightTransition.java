package com.example.smarthome.domain.smartdevices.statemachine.transitions.lighttransition;

import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;

public class LightTransition implements ITransition<LightAction> {

    private final LightAction lightAction;

    public LightTransition(LightAction action){
        this.lightAction = action;
    }

    @Override
    public LightAction getAction() {
        return lightAction;
    }

    @Override
    public String getLabel(){
        return this.lightAction.label;
    }
}
