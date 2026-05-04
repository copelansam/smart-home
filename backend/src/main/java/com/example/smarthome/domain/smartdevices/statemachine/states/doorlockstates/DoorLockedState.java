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

/**
 * Concrete state representing a door in the "locked" state.
 *
 * <p>
 * This state is part of the smart door lock state machine. It defines:
 * <ul>
 *     <li>Which transitions are allowed while the door is locked</li>
 *     <li>How those transitions modify the device</li>
 *     <li>How the system should respond to those transitions</li>
 * </ul>
 * </p>
 *
 * <p>
 * This state is registered in the {@link StateRegistry} using a static initializer,
 * ensuring it is available for reconstruction from persisted state names.
 * </p>
 */
public class DoorLockedState extends StateBase<SmartDoorLock> {

    /**
     * Registers this state in the global {@link StateRegistry} so it can be
     * reconstructed from its string representation ("Door Locked").
     */
    static {
        StateRegistry.register("Door Locked", DoorLockedState::new);
    }

    /**
     * Constructs the Door Locked state with:
     * <ul>
     *     <li>Allowed transition: UNLOCK</li>
     *     <li>No updatable fields in this state</li>
     * </ul>
     */
    public DoorLockedState(){
        super("Door Locked",
                List.of(
                new DoorTransition(DoorLockAction.UNLOCK)
                ),
                List.of()
        );
    }

    /**
     * Executes a transition while the device is in the locked state.
     *
     * <p>
     * Supported transitions:
     * <ul>
     *     <li>UNLOCK → transitions device to {@link DoorUnlockedState}</li>
     * </ul>
     * </p>
     *
     * @param transition string representation of the action
     * @param device the door lock being modified
     * @param parameters optional parameters (not used in this state)
     * @return result of the state transition execution
     */
    @Override
    public CallResult execute(String transition, SmartDoorLock device, Map<String, Object> parameters){

        DoorLockAction action = DoorLockAction.getActionFromString(transition);

        switch (action){
            case UNLOCK:
                // Unlock the door by transitioning to the unlocked state
                device.setState(new DoorUnlockedState());
                return new CallResult("Success", true,
                        new DeviceLog(device.getUuid(), "State Change","State changed from Door Locked to Door Unlocked"));

            default:
                return new CallResult();
        }
    }
}
