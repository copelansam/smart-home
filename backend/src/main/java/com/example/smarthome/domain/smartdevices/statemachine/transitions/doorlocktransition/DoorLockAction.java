package com.example.smarthome.domain.smartdevices.statemachine.transitions.doorlocktransition;

public enum DoorLockAction {
    LOCK("Lock"),
    UNLOCK("Unlock");

    String label;

    DoorLockAction(String message){
        this.label = message;
    }

    public static DoorLockAction getActionFromString(String wantedAction){

        for (DoorLockAction action : DoorLockAction.values()){
            if (action.name().equals(wantedAction)){
                return action;
            }
        }
        return null;
    }
}
