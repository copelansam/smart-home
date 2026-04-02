package com.example.smarthome.domain.smartdevices.statemachine.states.lightstates;

import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.lighttransition.LightAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.lighttransition.LightTransition;

import java.util.List;

public class LightOffState extends StateBase {

    public LightOffState(){
        super("Off",
                List.of(
                    new LightTransition(LightAction.TURN_ON)
                )
        );
    }

    public TransitionResult execute(){
        return new TransitionResult("Light has been turned on", true, new LightOffState());
    }
}
