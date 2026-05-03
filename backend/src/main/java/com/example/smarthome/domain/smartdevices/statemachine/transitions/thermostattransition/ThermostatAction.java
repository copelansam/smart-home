package com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition;

public enum ThermostatAction {
    POWER_THERMOSTAT_ON("Turn On"),
    POWER_THERMOSTAT_OFF("Turn Off"),
    START_HEATING(null),
    STOP_HEATING(null),
    START_COOLING(null),
    STOP_COOLING(null),
    UPDATE_DESIRED_TEMP("Update Desired Temp"),
    UPDATE_MODE("Update Mode");

    String label;

    ThermostatAction(String message){
        this.label = message;
    }

    public static ThermostatAction getActionFromString(String wantedAction){

        for (ThermostatAction action : ThermostatAction.values()){
            if (action.name().equals(wantedAction)){
                return action;
            }
        }
        return null;
    }
}
