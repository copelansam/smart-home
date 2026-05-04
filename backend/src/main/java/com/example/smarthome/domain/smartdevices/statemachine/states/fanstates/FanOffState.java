package com.example.smarthome.domain.smartdevices.statemachine.states.fanstates;

import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.devices.smartfan.SmartFan;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.fantransition.FanAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.fantransition.FanTransition;

import java.util.List;
import java.util.Map;

/**
 * Concrete state representing a fan in the "off" state.
 *
 * <p>
 * This state is part of the smart fan state machine. It defines:
 * <ul>
 *     <li>Which transitions are allowed while the fan is off</li>
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
public class FanOffState extends StateBase<SmartFan> {
    /**
     * Registers this state in the global {@link StateRegistry} so it can be
     * reconstructed from its string representation ("Fan Off").
     */
    static {
        StateRegistry.register("Fan Off", FanOffState::new);
    }

    /**
     * Constructs the Fan Off state with:
     * <ul>
     *     <li>Allowed transition: TURN_FAN_ON</li>
     *     <li>No updatable fields in this state</li>
     * </ul>
     */
    public FanOffState(){
        super("Fan Off",
                List.of(
                new FanTransition(FanAction.TURN_FAN_ON)
                ),
                List.of()
        );
    }

    /**
     * Executes a transition while the device is in the off state.
     *
     * <p>
     * Supported transitions:
     * <ul>
     *     <li>TURN_FAN_ON → transitions device to {@link FanOnState}</li>
     * </ul>
     * </p>
     *
     * @param transition string representation of the action
     * @param device the fan being modified
     * @param parameters optional parameters (not used in this state)
     * @return result of the state transition execution
     */
    public CallResult execute(String transition, SmartFan device, Map<String, Object> parameters){

        FanAction action = FanAction.getActionFromString(transition);

        switch (action){

            case TURN_FAN_ON:
                device.setState(new FanOnState());
                device.setIsOn(true);
                return new CallResult("Fan is now on", true,
                        new DeviceLog(device.getUuid(),"State Change", "State changed from Light Off to Light On"));

            case UPDATE_SPEED:
                return new CallResult("Cannot change device's speed when it is off", false,null);

            default:
                return new CallResult();
        }
    }
}
