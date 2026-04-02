package com.example.smarthome.domain.smartdevices.statemachine.states.doorlockstates;

import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.doorlocktransition.DoorLockAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.doorlocktransition.DoorTransition;

import java.util.List;

public class DoorLockedState extends StateBase {

    private final List<ITransition<DoorLockAction>> availableTransitions;


    public DoorLockedState(){
        super("Locked");
        this.availableTransitions = List.of(
                new DoorTransition(DoorLockAction.UNLOCK)
        );
    }

    public TransitionResult execute(){
        return new TransitionResult("Success", true, new DoorUnlockedState());
    }
}
