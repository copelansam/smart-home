package com.example.smarthome.domain.smartdevices.statemachine.states.thermostatstates;

import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition.ThermostatAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition.ThermostatTransition;

import java.util.List;

public class ThermostatOffState extends StateBase<ThermostatTransition, SmartThermostat> {

    static {
        StateRegistry.register("Thermostat Off", ThermostatOffState::new);
    }

    public ThermostatOffState(){
        super("Thermostat Off",
                List.of(
                new ThermostatTransition(ThermostatAction.POWER_ON)
                )
        );
    }

    public TransitionResult execute(ThermostatTransition transition, SmartThermostat device){

        ThermostatAction action = transition.getAction();

        switch (action){

            case POWER_ON:
                device.setState(new ThermostatIdleState());
                return new TransitionResult("Thermostat has been set to idle. Awaiting further instruction",true);

            default:
                return new TransitionResult();
        }
    }
}
