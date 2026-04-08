package com.example.smarthome.domain.smartdevices.statemachine.states.lightstates;

import com.example.smarthome.domain.smartdevices.devices.smartlight.SmartLight;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.states.thermostatstates.ThermostatOffState;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.lighttransition.LightAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.lighttransition.LightTransition;

import java.util.List;

public class LightOffState extends StateBase<LightTransition, SmartLight> {

    static {
        StateRegistry.register("Light Off", LightOffState::new);
    }

    public LightOffState(){
        super("Light Off",
                List.of(
                    new LightTransition(LightAction.TURN_ON)
                )
        );
    }

    public TransitionResult execute(LightTransition transition, SmartLight device){
        return new TransitionResult("Light has been turned on", true);
    }
}
