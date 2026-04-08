package com.example.smarthome.domain.smartdevices.statemachine.transitions.doorlocktransition;

public enum DoorLockAction {
    LOCK,
    UNLOCK;

    public static DoorLockAction getActionFromString(String wantedAction){

        for (DoorLockAction action : DoorLockAction.values()){
            if (action.name().equals(wantedAction)){
                return action;
            }
        }
        return null;
    }
}
