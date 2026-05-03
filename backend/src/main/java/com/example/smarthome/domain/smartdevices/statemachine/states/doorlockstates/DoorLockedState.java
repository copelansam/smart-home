package com.example.smarthome.domain.smartdevices.statemachine.states.doorlockstates;

import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.devices.smartdoorlock.SmartDoorLock;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.doorlocktransition.DoorLockAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.doorlocktransition.DoorTransition;

import java.util.List;
import java.util.Map;

public class DoorLockedState extends StateBase<SmartDoorLock> {

    static {
        StateRegistry.register("Door Locked", DoorLockedState::new);
    }

    public DoorLockedState(){
        super("Door Locked",
                List.of(
                new DoorTransition(DoorLockAction.UNLOCK)
                ),
                List.of()
        );
    }

    @Override
    public CallResult execute(String transition, SmartDoorLock device, Map<String, Object> parameters){

        DoorLockAction action = DoorLockAction.getActionFromString(transition);

        switch (action){
            case UNLOCK:
                device.setState(new DoorUnlockedState());
                return new CallResult("Success", true,
                        new DeviceLog(device.getUuid(), "State Change","State changed from Door Locked to Door Unlocked"));

            default:
                return new CallResult();
        }
    }
}
