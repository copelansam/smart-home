package com.example.smarthome.domain.smartdevices.statemachine.states.thermostatstates;

import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition.ThermostatAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition.ThermostatTransition;

import java.util.List;

public class ThermostatOffState extends StateBase<SmartThermostat> {

    static {
        StateRegistry.register("Thermostat Off", ThermostatOffState::new);
    }

    public ThermostatOffState(){
        super("Thermostat Off",
                List.of(
                new ThermostatTransition(ThermostatAction.POWER_THERMOSTAT_ON)
                )
        );
    }

    public CallResult execute(String transition, SmartThermostat device){

        ThermostatAction action = ThermostatAction.getActionFromString(transition);

        if (action == null){
            return new CallResult();
        }

        switch (action){

            case POWER_THERMOSTAT_ON:
                device.setState(new ThermostatIdleState());
                return new CallResult("Thermostat has been set to idle. Awaiting further instruction",true,
                        new DeviceLog(device.getUuid(), "State changed from Thermostat Off to Thermostat Idle"));

            default:
                return new CallResult();
        }
    }
}
