package com.example.smarthome.domain.smartdevices.statemachine.states.lightstates;

import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.devices.smartlight.SmartLight;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.lighttransition.LightAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.lighttransition.LightTransition;

import java.util.List;

public class LightOnState extends StateBase<SmartLight> {

    static {
        StateRegistry.register("Light On", LightOnState::new);
    }

    public LightOnState(){
        super("Light On",
                List.of(
                new LightTransition(LightAction.TURN_LIGHT_OFF)
                )
        );
    }

    public CallResult execute(String transition, SmartLight device){

        LightAction action = LightAction.getActionFromString(transition);

        switch (action){

            case TURN_LIGHT_OFF:
                device.setState(new LightOffState());
                device.setIsOn(false);
                return new CallResult("Light is now off", true,
                        new DeviceLog(device.getUuid(),"State Change", "State changed from Light On to Light Off"));

            default:
                return new CallResult();
        }
    }
}
