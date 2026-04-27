package com.example.smarthome.domain.smartdevices.statemachine.states.fanstates;

import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.devices.smartfan.SmartFan;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.fantransition.FanAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.fantransition.FanTransition;

import java.util.List;

public class FanOnState extends StateBase<SmartFan> {

    static {
        StateRegistry.register("Fan On", FanOnState::new);
    }

    public FanOnState(){
        super("Fan On",
                List.of(
                new FanTransition(FanAction.TURN_FAN_OFF)
                )
        );
    }

    public CallResult execute(String transition, SmartFan device){

        FanAction action = FanAction.getActionFromString(transition);

        switch (action){

            case TURN_FAN_OFF:
                device.setState(new FanOffState());
                device.setIsOn(false);
                return new CallResult("Fan is now off", true,
                        new DeviceLog(device.getUuid(),"State Change", "State changed from Light On to Light Off"));

            default:
                return new CallResult();
        }
    }
}
