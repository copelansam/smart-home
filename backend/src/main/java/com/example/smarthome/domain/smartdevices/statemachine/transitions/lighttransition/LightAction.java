package com.example.smarthome.domain.smartdevices.statemachine.transitions.lighttransition;


public enum LightAction {
    TURN_LIGHT_ON,
    TURN_LIGHT_OFF;

    public static LightAction getActionFromString(String wantedAction){

        for (LightAction action : LightAction.values()){
            if (action.name().equals(wantedAction)){
                return action;
            }
        }
        return null;
    }
}
