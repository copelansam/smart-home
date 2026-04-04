package com.example.smarthome.domain.smartdevices.statemachine.states.thermostatstates;

import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition.ThermostatAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition.ThermostatTransition;

import java.util.List;

public class ThermostatHeatingState extends StateBase {

    static {
        StateRegistry.register("Thermostat Heating", ThermostatHeatingState::new);
    }

    public ThermostatHeatingState(){
        super("Thermostat Heating",
                List.of(
                new ThermostatTransition(ThermostatAction.STOP_HEATING),
                new ThermostatTransition(ThermostatAction.POWER_OFF)
                )
        );
    }

    public TransitionResult execute(){
        return new TransitionResult("Thermostat has turned off or gone ot idle", true, new ThermostatOffState());
    }
}
