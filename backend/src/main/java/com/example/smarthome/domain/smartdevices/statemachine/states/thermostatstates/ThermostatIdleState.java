package com.example.smarthome.domain.smartdevices.statemachine.states.thermostatstates;

import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition.ThermostatAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition.ThermostatTransition;

import java.util.List;

public class ThermostatIdleState extends StateBase<SmartThermostat> {

    static {
        StateRegistry.register("Thermostat Idle", ThermostatIdleState::new);
    }

    public ThermostatIdleState(){
        super("Thermostat Idle",
                List.of(
                new ThermostatTransition(ThermostatAction.START_COOLING),
                new ThermostatTransition(ThermostatAction.START_HEATING),
                new ThermostatTransition(ThermostatAction.POWER_THERMOSTAT_OFF)
                )
        );
    }

    public CallResult execute(String transition, SmartThermostat device){

        ThermostatAction action = ThermostatAction.getActionFromString(transition);

        if (action == null){
            return new CallResult();
        }

        switch(action){

            case POWER_THERMOSTAT_OFF:
                device.setState(new ThermostatOffState());
                return new CallResult("Thermostat has been turned off",true,
                        new DeviceLog(device.getUuid(),"State Change", "State changed from Thermostat Idle to Thermostat Off"));

            case START_COOLING:
                device.setState(new ThermostatCoolingState());
                device.setIsOn(true);
                return new CallResult("The ambient temperature is greater than the desired temperature. Thermostat begins cooling", true,
                        new DeviceLog(device.getUuid(),"State Change", "State changed from Thermostat Idle to Thermostat Cooling"));

            case START_HEATING:
                device.setState(new ThermostatHeatingState());
                device.setIsOn(true);
                return new CallResult("The ambient temperature is lower than the desired temperature. Thermostat begins heating",true,
                        new DeviceLog(device.getUuid(),"State Change", "State changed from Thermostat Idle to Thermostat Heating"));

            default:
                return new CallResult();
        }
    }
}
