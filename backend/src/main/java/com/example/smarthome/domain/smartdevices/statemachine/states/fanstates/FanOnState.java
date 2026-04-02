package com.example.smarthome.domain.smartdevices.statemachine.states.fanstates;

import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.fantransition.FanAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.fantransition.FanTransition;

import java.util.List;

public class FanOnState extends StateBase {

    public FanOnState(){
        super("On",
                List.of(
                new FanTransition(FanAction.TURN_OFF)
                )
        );
    }

    public TransitionResult execute(){
        return new TransitionResult("Fan is now off", true, new FanOffState());
    }
}
