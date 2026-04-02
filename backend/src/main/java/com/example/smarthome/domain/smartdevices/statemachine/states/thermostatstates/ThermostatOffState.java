package com.example.smarthome.domain.smartdevices.statemachine.states.thermostatstates;

import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition.ThermostatAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition.ThermostatTransition;

import java.util.List;

public class ThermostatOffState extends StateBase {

    public ThermostatOffState(){
        super("Off",
                List.of(
                new ThermostatTransition(ThermostatAction.POWER_ON)
                )
        );
    }

    public TransitionResult execute(){
        return new TransitionResult("Thermostat has turned off or gone ot idle", true, new IdleState());
    }
}
