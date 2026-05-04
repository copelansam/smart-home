package com.example.smarthome.domain.smartdevices.statemachine.states.fanstates;

import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.devices.smartfan.FanSpeed;
import com.example.smarthome.domain.smartdevices.devices.smartfan.SmartFan;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.fantransition.FanAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.fantransition.FanTransition;

import java.util.List;
import java.util.Map;

/**
 * Concrete state representing a fan in the "on" state.
 *
 * <p>
 * This state is part of the smart fan state machine. It defines:
 * <ul>
 *     <li>Which transitions are allowed while the fan is on</li>
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
public class FanOnState extends StateBase<SmartFan> {

    /**
     * Registers this state in the global {@link StateRegistry} so it can be
     * reconstructed from its string representation ("Fan On").
     */
    static {
        StateRegistry.register("Fan On", FanOnState::new);
    }

    /**
     * Constructs the Fan On state with:
     * <ul>
     *     <li>Allowed transition: TURN_FAN_OFF</li>
     *     <li>Updatable fields: fan speed</li>
     * </ul>
     */
    public FanOnState(){
        super("Fan On",
                List.of(
                new FanTransition(FanAction.TURN_FAN_OFF)
                ),
                List.of(new FanTransition(FanAction.UPDATE_SPEED))
        );
    }

    /**
     * Executes a transition while the device is in the on state.
     *
     * <p>
     * Supported transitions:
     * <ul>
     *     <li>TURN_FAN_OFF → transitions device to {@link FanOffState}</li>
     * </ul>
     * </p>
     *
     * @param transition string representation of the action
     * @param device the fan being modified
     * @param parameters optional parameters (fan speed)
     * @return result of the state transition execution
     */
    public CallResult execute(String transition, SmartFan device, Map<String, Object> parameters){

        FanAction action = FanAction.getActionFromString(transition);

        switch (action){

            case TURN_FAN_OFF:
                // Turn the fan off by moving to the off state
                device.setState(new FanOffState());
                device.setIsOn(false);
                return new CallResult("Fan is now off", true,
                        new DeviceLog(device.getUuid(),"State Change", "State changed from Light On to Light Off"));

            case UPDATE_SPEED:
                // Ensure that the fan is on and check for a speed parameter to update the device's speed fields
                if (device.getIsOn()) {
                    String speedString = parameters.get("speed").toString();
                    FanSpeed newSpeed = FanSpeed.valueOf(speedString.toUpperCase());
                    device.setSpeed(newSpeed);
                    return new CallResult("The device's speed has been set to: " + newSpeed, true,
                            new DeviceLog(device.getUuid(), "Properties Change", "Speed has been set to: " + newSpeed));
                }
                else{
                    return new CallResult("Cannot change device's speed when it is off", false,null);
                }

            default:
                return new CallResult();
        }
    }
}
