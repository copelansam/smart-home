package com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition;

import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;

public class ThermostatTransition implements ITransition<ThermostatAction> {

    private final ThermostatAction thermostatAction;

    public ThermostatTransition(ThermostatAction thermostatAction){
        this.thermostatAction = thermostatAction;
    }

    @Override
    public ThermostatAction getAction(){
        return this.thermostatAction;
    }
}
