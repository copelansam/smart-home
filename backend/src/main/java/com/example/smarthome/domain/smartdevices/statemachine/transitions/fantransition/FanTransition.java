package com.example.smarthome.domain.smartdevices.statemachine.transitions.fantransition;

import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;

public class FanTransition implements ITransition<FanAction> {

    private FanAction fanAction;

    public FanTransition(FanAction action){
        this.fanAction = action;
    }

    @Override
    public FanAction getAction() {
        return fanAction;
    }

    @Override
    public String getLabel(){
        return this.fanAction.label;
    }
}
