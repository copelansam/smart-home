package com.example.smarthome.domain.smartdevices.statemachine.transitions.fantransition;

public enum FanAction {
    TURN_FAN_ON,
    TURN_FAN_OFF;

    public static FanAction getActionFromString(String wantedAction){

        for (FanAction action : FanAction.values()){
            if (action.name().equals(wantedAction)){
                return action;
            }
        }
        return null;
    }
}
