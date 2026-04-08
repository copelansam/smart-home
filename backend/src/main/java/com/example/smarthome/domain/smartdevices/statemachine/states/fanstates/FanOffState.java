package com.example.smarthome.domain.smartdevices.statemachine.states.fanstates;

import com.example.smarthome.domain.smartdevices.devices.smartfan.SmartFan;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.fantransition.FanAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.fantransition.FanTransition;

import java.util.List;


public class FanOffState extends StateBase<SmartFan> {

    static {
        StateRegistry.register("Fan Off", FanOffState::new);
    }

    public FanOffState(){
        super("Fan Off",
                List.of(
                new FanTransition(FanAction.TURN_FAN_ON)
                )
        );
    }

    public TransitionResult execute(String transition, SmartFan device){

        FanAction action = FanAction.getActionFromString(transition);

        switch (action){

            case TURN_FAN_ON:
                device.setState(new FanOnState());
                device.setIsOn(true);
                return new TransitionResult("Fan is now on", true);

            default:
                return new TransitionResult();
        }
    }
}
