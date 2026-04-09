package com.example.smarthome.domain.smartdevices.statemachine.states.lightstates;

import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.devices.smartlight.SmartLight;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.lighttransition.LightAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.lighttransition.LightTransition;

import java.util.List;

public class LightOffState extends StateBase<SmartLight> {

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

    public TransitionResult execute(String transition, SmartLight device){

        LightAction action = LightAction.getActionFromString(transition);

        switch (action){

            case TURN_LIGHT_ON:
                device.setState(new LightOnState());
                device.setIsOn(true);
                return new TransitionResult("Light has been turned on", true,
                        new DeviceLog(device.getUuid(), "State changed from Light Off -> Light On"));

            default:
                return new TransitionResult();
        }
    }
}
