package com.example.smarthome.domain.smartdevices.statemachine.states.doorlockstates;

import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.devices.smartdoorlock.SmartDoorLock;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.doorlocktransition.DoorLockAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.doorlocktransition.DoorTransition;

import java.util.List;

public class DoorUnlockedState extends StateBase<SmartDoorLock> {

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

    public TransitionResult execute(String transition, SmartDoorLock device){

        DoorLockAction action = DoorLockAction.getActionFromString(transition);

        switch(action){

            case LOCK:
                device.setState(new DoorLockedState());
                return new TransitionResult("Door is now locked", true,
                        new DeviceLog(device.getUuid(), "State changed from Door Unlocked -> Door Locked"));

            default:
                return new TransitionResult();

        }
    }
}
