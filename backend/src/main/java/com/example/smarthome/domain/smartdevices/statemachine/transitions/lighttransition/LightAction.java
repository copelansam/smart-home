package com.example.smarthome.domain.smartdevices.statemachine.transitions.lighttransition;


public enum LightAction {
    TURN_LIGHT_ON("Turn On"),
    TURN_LIGHT_OFF("Turn Off"),
    UPDATE_BRIGHTNESS("Update Brightness"),
    UPDATE_COLOR("Update Color");

    String label;

    LightAction(String message){
        this.label = message;
    }

    public static LightAction getActionFromString(String wantedAction){

        for (LightAction action : LightAction.values()){
            if (action.name().equals(wantedAction)){
                return action;
            }
        }
        return null;
    }
}
