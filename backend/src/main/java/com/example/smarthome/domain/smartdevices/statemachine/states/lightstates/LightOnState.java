package com.example.smarthome.domain.smartdevices.statemachine.states.lightstates;

import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.lighttransition.LightAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.lighttransition.LightTransition;

import java.util.List;

public class LightOnState extends StateBase {

    public LightOnState(){
        super("On",
                List.of(
                new LightTransition(LightAction.TURN_OFF)
                )
        );
    }

    public TransitionResult execute(){
        return new TransitionResult("Light is now off", true, new LightOffState());
    }
}
