package com.example.smarthome.domain.smartdevices.statemachine.states.thermostatstates;

import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition.ThermostatAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition.ThermostatTransition;

import java.util.List;

public class ThermostatCoolingState extends StateBase<SmartThermostat> {

    static {
        StateRegistry.register("Thermostat Cooling", ThermostatCoolingState::new);
    }

    public ThermostatCoolingState(){
        super("Thermostat Cooling",
                List.of(
                new ThermostatTransition(ThermostatAction.STOP_COOLING),
                new ThermostatTransition(ThermostatAction.POWER_THERMOSTAT_OFF)
                )
        );
    }

    public TransitionResult execute(String transition, SmartThermostat device){

        ThermostatAction action = ThermostatAction.getActionFromString(transition);

        if (action == null){
            return new TransitionResult();
        }

        switch (action){

            case STOP_COOLING:
                device.setState(new ThermostatIdleState());
                device.setIsOn(false);
                return new TransitionResult("The ambient temperature has reached the desired temperature. The thermostat has gone idle.", true,
                        new DeviceLog(device.getUuid(), "State changed from Thermostat Cooling -> Thermostat Idle"));

            case POWER_THERMOSTAT_OFF:
                device.setState(new ThermostatOffState());
                device.setIsOn(false);
                return new TransitionResult("The thermostat has been turned off.", true,
                        new DeviceLog(device.getUuid(), "State changed from Thermostat Cooling -> Thermostat Off"));

            default:
                return new TransitionResult();
        }
    }
}
