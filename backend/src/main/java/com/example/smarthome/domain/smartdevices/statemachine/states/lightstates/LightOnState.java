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

public class LightOnState extends StateBase<SmartLight> {

    static {
        StateRegistry.register("Light On", LightOnState::new);
    }

    public LightOnState(){
        super("Light On",
                List.of(
                new LightTransition(LightAction.TURN_LIGHT_OFF)
                ),
                List.of(new LightTransition(LightAction.UPDATE_COLOR),
                        new LightTransition(LightAction.UPDATE_BRIGHTNESS))
        );
    }

    public CallResult execute(String transition, SmartLight device, Map<String, Object> parameters){

        LightAction action = LightAction.getActionFromString(transition);

        switch (action){

            case TURN_LIGHT_OFF:
                device.setState(new LightOffState());
                device.setIsOn(false);
                return new CallResult("Light is now off", true,
                        new DeviceLog(device.getUuid(),"State Change", "State changed from Light On to Light Off"));

            case UPDATE_BRIGHTNESS:
                if (device.getIsOn()) {
                    int newBrightness = ((Number) parameters.get("brightnessPercentage")).intValue();
                    device.setBrightnessPercentage(newBrightness);
                    return new CallResult("Brightness percentage changed to: " + newBrightness, true,
                            new DeviceLog(device.getUuid(), "Properties Change", "Brightness percentage has been updated to: " + newBrightness));
                }
                else{
                    return new CallResult("Cannot change brightness when the device is off", false, null);
                }

            case UPDATE_COLOR:
                if (device.getIsOn()){
                    int redValue = ((Number) parameters.get("redValue")).intValue();
                    int greenValue = ((Number) parameters.get("greenValue")).intValue();
                    int blueValue = ((Number) parameters.get("blueValue")).intValue();
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
