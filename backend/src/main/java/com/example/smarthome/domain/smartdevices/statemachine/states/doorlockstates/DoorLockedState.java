package com.example.smarthome.domain.smartdevices.statemachine.states.doorlockstates;

import com.example.smarthome.domain.smartdevices.devices.smartdoorlock.SmartDoorLock;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.doorlocktransition.DoorLockAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.doorlocktransition.DoorTransition;

import java.util.List;

public class DoorLockedState extends StateBase<DoorTransition, SmartDoorLock> {

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

    @Override
    public TransitionResult execute(DoorTransition transition, SmartDoorLock lock){

        DoorLockAction action = transition.getAction();

        switch (action){
            case UNLOCK:
                lock.setState(new DoorUnlockedState());
                return new TransitionResult("Success", true);

            default:
                return new TransitionResult("Invalid Transition from current state",false);
        }
    }
}
