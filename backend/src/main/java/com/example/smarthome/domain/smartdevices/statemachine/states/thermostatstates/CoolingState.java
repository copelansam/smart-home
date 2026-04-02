package com.example.smarthome.domain.smartdevices.statemachine.states.thermostatstates;

import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition.ThermostatAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition.ThermostatTransition;

import java.util.List;

public class CoolingState extends StateBase {

    private final List<ITransition<ThermostatAction>> availableTransitions;

    public CoolingState(){
        super("Cooling");
        this.availableTransitions = List.of(
                new ThermostatTransition(ThermostatAction.STOP_COOLING),
                new ThermostatTransition(ThermostatAction.POWER_OFF)
        );
    }

    public TransitionResult execute(){
        return new TransitionResult("Thermostat has turned off or gone ot idle", true, new ThermostatOffState());
    }
}
