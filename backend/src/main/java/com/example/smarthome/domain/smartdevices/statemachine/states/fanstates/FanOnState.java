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

public class FanOnState extends StateBase<SmartFan> {

    static {
        StateRegistry.register("Fan On", FanOnState::new);
    }

    public FanOnState(){
        super("Fan On",
                List.of(
                new FanTransition(FanAction.TURN_FAN_OFF)
                ),
                List.of(new FanTransition(FanAction.UPDATE_SPEED))
        );
    }

    public CallResult execute(String transition, SmartFan device, Map<String, Object> parameters){

        FanAction action = FanAction.getActionFromString(transition);

        switch (action){

            case TURN_FAN_OFF:
                device.setState(new FanOffState());
                device.setIsOn(false);
                return new CallResult("Fan is now off", true,
                        new DeviceLog(device.getUuid(),"State Change", "State changed from Light On to Light Off"));

            case UPDATE_SPEED:
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
