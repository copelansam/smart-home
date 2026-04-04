package com.example.smarthome.domain.smartdevices.statemachine.states.doorlockstates;

import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.states.fanstates.FanOnState;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.doorlocktransition.DoorLockAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.doorlocktransition.DoorTransition;

import java.util.List;

public class DoorUnlockedState extends StateBase {

    static {
        StateRegistry.register("Door Unlocked", DoorUnlockedState::new);
    }

    public DoorUnlockedState(){
        super("Door Unlocked",
                List.of(
                        new DoorTransition(DoorLockAction.LOCK)
                )
        );
    }

    public TransitionResult execute(){
        return new TransitionResult("Door is now locked", true, new DoorLockedState());
    }
}
