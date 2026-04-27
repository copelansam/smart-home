package com.example.smarthome.domain.smartdevices.statemachine.states.thermostatstates;

import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition.ThermostatAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition.ThermostatTransition;

import java.util.List;

public class ThermostatHeatingState extends StateBase<SmartThermostat> {

    static {
        StateRegistry.register("Thermostat Heating", ThermostatHeatingState::new);
    }

    public ThermostatHeatingState(){
        super("Thermostat Heating",
                List.of(
                new ThermostatTransition(ThermostatAction.STOP_HEATING),
                new ThermostatTransition(ThermostatAction.POWER_THERMOSTAT_OFF)
                )
        );
    }

    public CallResult execute(String transition, SmartThermostat device){

        ThermostatAction action = ThermostatAction.getActionFromString(transition);

        if (action == null){
            return new CallResult();
        }

        switch (action){

            case STOP_HEATING:
                device.setState(new ThermostatIdleState());
                device.setIsOn(false);
                return new CallResult("The ambient temperature has reached the desired temperature. The thermostat has gone idle.",true,
                        new DeviceLog(device.getUuid(),"State Change", "State changed from Thermostat Heating to Thermostat Idle"));

            case POWER_THERMOSTAT_OFF:
                device.setState(new ThermostatOffState());
                device.setIsOn(false);
                return new CallResult("The thermostat has been turned off.", true,
                        new DeviceLog(device.getUuid(),"State Change", "State changed from Thermostat Heating to Thermostat Off"));

            default:
                return new CallResult();
        }

    }
}
