package com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition;

import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;

public class ThermostatTransition implements ITransition<ThermostatAction> {

    /**
     * The underlying thermostat action represented by this transition.
     */
    private final ThermostatAction thermostatAction;

    /**
     * Creates a transition wrapper for a specific fan action.
     *
     * @param action the thermostat action this transition represents
     */
    public ThermostatTransition(ThermostatAction action){
        this.thermostatAction = action;
    }

    /**
     * Returns the underlying action used by the state machine.
     */
    @Override
    public ThermostatAction getAction(){
        return this.thermostatAction;
    }

    /**
     * Returns a human-readable label for UI display.
     */
    @Override
    public String getLabel(){
        return this.thermostatAction.label;
    }
}
