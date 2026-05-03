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

public class LightOffState extends StateBase<SmartLight> {

    static {
        StateRegistry.register("Light Off", LightOffState::new);
    }

    public LightOffState(){
        super("Light Off",
                List.of(
                    new LightTransition(LightAction.TURN_LIGHT_ON)
                ),
                List.of()
        );
    }

    public CallResult execute(String transition, SmartLight device, Map<String, Object> parameters){

        LightAction action = LightAction.getActionFromString(transition);

        switch (action){

            case TURN_LIGHT_ON:
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
