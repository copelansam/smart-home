package com.example.smarthome.domain.smartdevices.statemachine.states.fanstates;

import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.fantransition.FanAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.fantransition.FanTransition;

import java.util.List;


public class FanOffState extends StateBase {

    static {
        StateRegistry.register("Fan Off", FanOffState::new);
    }

    public FanOffState(){
        super("Fan Off",
                List.of(
                new FanTransition(FanAction.TURN_ON)
                )
        );
    }

    public TransitionResult execute(){
        return new TransitionResult("Fan is now on", true, new FanOnState());
    }
}
