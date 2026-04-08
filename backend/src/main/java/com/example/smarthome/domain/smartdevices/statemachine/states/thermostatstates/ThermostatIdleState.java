package com.example.smarthome.domain.smartdevices.statemachine.states.thermostatstates;

import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition.ThermostatAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition.ThermostatTransition;

import java.util.List;

public class ThermostatIdleState extends StateBase<ThermostatTransition, SmartThermostat> {

    static {
        StateRegistry.register("Thermostat Idle", ThermostatIdleState::new);
    }

    public ThermostatIdleState(){
        super("Thermostat Idle",
                List.of(
                new ThermostatTransition(ThermostatAction.START_COOLING),
                new ThermostatTransition(ThermostatAction.START_HEATING),
                new ThermostatTransition(ThermostatAction.POWER_OFF)
                )
        );
    }

    public TransitionResult execute(ThermostatTransition transition, SmartThermostat device){

        ThermostatAction action = transition.getAction();

        switch(action){

            case POWER_OFF:
                device.setState(new ThermostatOffState());
                return new TransitionResult("Thermostat has been turned off",true);

            case START_COOLING:
                device.setState(new ThermostatCoolingState());
                device.setIsOn(true);
                return new TransitionResult("The ambient temperature is greater than the desired temperature. Thermostat begins cooling", true);

            case START_HEATING:
                device.setState(new ThermostatCoolingState());
                device.setIsOn(true);
                return new TransitionResult("The ambient temperature is lower than the desired temperature. Thermostat begins heating",true);

            default:
                return new TransitionResult();
        }
    }
}
