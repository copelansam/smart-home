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


public class FanOffState extends StateBase<SmartFan> {

    static {
        StateRegistry.register("Fan Off", FanOffState::new);
    }

    public FanOffState(){
        super("Fan Off",
                List.of(
                new FanTransition(FanAction.TURN_FAN_ON)
                ),
                List.of()
        );
    }

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
