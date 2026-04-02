package com.example.smarthome.domain.smartdevices.statemachine.states.thermostatstates;

import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition.ThermostatAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition.ThermostatTransition;

import java.util.List;

public class IdleState extends StateBase {

    private final List<ITransition<ThermostatAction>> availableTransitions;

    public IdleState(){
        super("Idle");
        this.availableTransitions = List.of(
                new ThermostatTransition(ThermostatAction.START_COOLING),
                new ThermostatTransition(ThermostatAction.START_HEATING),
                new ThermostatTransition(ThermostatAction.POWER_OFF)
        );
    }

    public TransitionResult execute(){
        return new TransitionResult("Thermostat has turned off or gone ot idle", true, new ThermostatOffState());
    }
}
