package com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition;

public enum ThermostatAction {
    POWER_THERMOSTAT_ON,
    POWER_THERMOSTAT_OFF,
    START_HEATING,
    STOP_HEATING,
    START_COOLING,
    STOP_COOLING;

    public static ThermostatAction getActionFromString(String wantedAction){

        for (ThermostatAction action : ThermostatAction.values()){
            if (action.name().equals(wantedAction)){
                return action;
            }
        }
        return null;
    }
}
