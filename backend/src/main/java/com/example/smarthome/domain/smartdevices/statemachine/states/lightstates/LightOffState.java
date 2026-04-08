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
                    new LightTransition(LightAction.TURN_LIGHT_ON)
                )
        );
    }

    public TransitionResult execute(LightTransition transition, SmartLight device){

        LightAction action = transition.getAction();

        switch (action){

            case TURN_LIGHT_ON:
                device.setState(new LightOnState());
                device.setIsOn(true);
                return new TransitionResult("Light has been turned on", true);

            default:
                return new TransitionResult();
        }
    }
}
