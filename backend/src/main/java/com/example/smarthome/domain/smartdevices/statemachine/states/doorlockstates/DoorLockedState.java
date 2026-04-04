package com.example.smarthome.domain.smartdevices.statemachine.states.doorlockstates;

import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.states.fanstates.FanOnState;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.doorlocktransition.DoorLockAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.doorlocktransition.DoorTransition;

import java.util.List;

public class DoorLockedState extends StateBase {

    static {
        StateRegistry.register("Door Locked", DoorLockedState::new);
    }

    public DoorLockedState(){
        super("Door Locked",
                List.of(
                new DoorTransition(DoorLockAction.UNLOCK)
                )
        );
    }

    public TransitionResult execute(){
        return new TransitionResult("Success", true, new DoorUnlockedState());
    }
}
