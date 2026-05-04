package com.example.smarthome.domain.smartdevices.statemachine.states.lightstates;

import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.devices.smartlight.RGB;
import com.example.smarthome.domain.smartdevices.devices.smartlight.SmartLight;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.lighttransition.LightAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.lighttransition.LightTransition;

import java.util.List;
import java.util.Map;

/**
 * Concrete state representing a light in the "on" state.
 *
 * <p>
 * This state is part of the smart light state machine. It defines:
 * <ul>
 *     <li>Which transitions are allowed while the light is on</li>
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
public class LightOnState extends StateBase<SmartLight> {

    /**
     * Registers this state in the global {@link StateRegistry} so it can be
     * reconstructed from its string representation ("Light On").
     */
    static {
        StateRegistry.register("Light On", LightOnState::new);
    }

    /**
     * Constructs the Light On state with:
     * <ul>
     *     <li>Allowed transition: TURN_LIGHT_OFF</li>
     *     <li>Updatable fields: brightness percentage, color</li>
     * </ul>
     */
    public LightOnState(){
        super("Light On",
                List.of(
                new LightTransition(LightAction.TURN_LIGHT_OFF)
                ),
                List.of(new LightTransition(LightAction.UPDATE_COLOR),
                        new LightTransition(LightAction.UPDATE_BRIGHTNESS))
        );
    }

    /**
     * Executes a transition while the device is in the on state.
     *
     * <p>
     * Supported transitions:
     * <ul>
     *     <li>TURN_LIGHT_OFF → transitions device to {@link LightOffState}</li>
     * </ul>
     * </p>
     *
     * @param transition string representation of the action
     * @param device the fan being modified
     * @param parameters optional parameters (brightnessPercentage, redValue, greenValue, blueValue)
     * @return result of the state transition execution
     */
    public CallResult execute(String transition, SmartLight device, Map<String, Object> parameters){

        LightAction action = LightAction.getActionFromString(transition);

        switch (action){

            case TURN_LIGHT_OFF:
                // Turn light off by transitioning to the off state
                device.setState(new LightOffState());
                device.setIsOn(false);
                return new CallResult("Light is now off", true,
                        new DeviceLog(device.getUuid(),"State Change", "State changed from Light On to Light Off"));

            case UPDATE_BRIGHTNESS:
                if (device.getIsOn()) { // Make sure that the device is on
                    // Retrieve the brightness percentage from the parameters and cast it to an int before updating the device
                    int newBrightness = ((Number) parameters.get("brightnessPercentage")).intValue();
                    device.setBrightnessPercentage(newBrightness);
                    return new CallResult("Brightness percentage changed to: " + newBrightness, true,
                            new DeviceLog(device.getUuid(), "Properties Change", "Brightness percentage has been updated to: " + newBrightness));
                }
                else{
                    return new CallResult("Cannot change brightness when the device is off", false, null);
                }

            case UPDATE_COLOR:
                if (device.getIsOn()){ // Make sure that the device is on
                    // Retrieve the red, green, and blue values and cast them to int
                    int redValue = ((Number) parameters.get("redValue")).intValue();
                    int greenValue = ((Number) parameters.get("greenValue")).intValue();
                    int blueValue = ((Number) parameters.get("blueValue")).intValue();

                    // Create a new RGB object then update the device with it
                    RGB newColor = new RGB(redValue, greenValue, blueValue);
                    device.setColor(newColor);
                    return new CallResult("Color has been changed to: " + newColor, true,
                    new DeviceLog(device.getUuid(), "Properties Change", "Color has been updated to: " + newColor));
                }
                else{
                    return new CallResult("Cannot update color when device is off", false, null);
                }

            default:
                return new CallResult();
        }
    }
}
