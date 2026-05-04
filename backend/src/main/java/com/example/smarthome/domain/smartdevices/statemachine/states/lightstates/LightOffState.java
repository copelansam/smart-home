package com.example.smarthome.domain.smartdevices.statemachine.states.lightstates;

import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.devices.smartlight.SmartLight;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.lighttransition.LightAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.lighttransition.LightTransition;

import java.util.List;
import java.util.Map;

/**
 * Concrete state representing a light in the "off" state.
 *
 * <p>
 * This state is part of the smart light state machine. It defines:
 * <ul>
 *     <li>Which transitions are allowed while the light is off</li>
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
public class LightOffState extends StateBase<SmartLight> {

    /**
     * Registers this state in the global {@link StateRegistry} so it can be
     * reconstructed from its string representation ("Light Off").
     */
    static {
        StateRegistry.register("Light Off", LightOffState::new);
    }

    /**
     * Constructs the Fan Off state with:
     * <ul>
     *     <li>Allowed transition: TURN_LIGHT_ON</li>
     *     <li>No updatable fields in this state</li>
     * </ul>
     */
    public LightOffState(){
        super("Light Off",
                List.of(
                    new LightTransition(LightAction.TURN_LIGHT_ON)
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
     *     <li>TURN_LIGHT_ON → transitions device to {@link LightOnState}</li>
     * </ul>
     * </p>
     *
     * @param transition string representation of the action
     * @param device the fan being modified
     * @param parameters optional parameters (not used in this state)
     * @return result of the state transition execution
     */
    public CallResult execute(String transition, SmartLight device, Map<String, Object> parameters){

        LightAction action = LightAction.getActionFromString(transition);

        switch (action){

            case TURN_LIGHT_ON:
                // Turn light on by transitioning to the on state
                device.setState(new LightOnState());
                device.setIsOn(true);
                return new CallResult("Light has been turned on", true,
                        new DeviceLog(device.getUuid(),"State Change", "State changed from Light Off to Light On"));

            case UPDATE_BRIGHTNESS:
                return new CallResult("Cannot change brightness when the device is off", false, null);

            case UPDATE_COLOR:
                return new CallResult("Cannot update color when device is off", false, null);

            default:
                return new CallResult();
        }
    }
}
