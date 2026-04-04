package com.example.smarthome.domain.smartdevices.statemachine.states.lightstates;

import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.states.thermostatstates.ThermostatOffState;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.lighttransition.LightAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.lighttransition.LightTransition;

import java.util.List;

public class LightOnState extends StateBase {

    static {
        StateRegistry.register("Light On", LightOnState::new);
    }

    public LightOnState(){
        super("Light On",
                List.of(
                new LightTransition(LightAction.TURN_OFF)
                )
        );
    }

    public TransitionResult execute(){
        return new TransitionResult("Light is now off", true, new LightOffState());
    }
}
