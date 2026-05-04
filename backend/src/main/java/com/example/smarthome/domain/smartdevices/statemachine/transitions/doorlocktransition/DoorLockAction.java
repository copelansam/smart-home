package com.example.smarthome.domain.smartdevices.statemachine.transitions.doorlocktransition;

/**
 * Represents all valid actions that can be performed on a door lock device.
 *
 * <p>
 * These actions are used by the door lock state machine to control transitions
 * between states such as locked and unlocked.
 * </p>
 *
 * <p>
 * Each action also contains a human-readable label used for UI display purposes.
 * This allows the backend to expose strongly typed actions while still providing
 * user-friendly text for front-end rendering.
 * </p>
 */
public enum DoorLockAction {

    /** Locks the door. Displayed in UI as "Lock". */
    LOCK("Lock"),

    /** Unlocks the door. Displayed in UI as "Unlock". */
    UNLOCK("Unlock");

    /**
     * Human-readable label used for UI display.
     *
     * <p>
     * This value is returned to the frontend so that actions can be shown in a
     * user-friendly format rather than enum names.
     * </p>
     */
    String label;

    /**
     * Creates a door lock action with a UI display label.
     *
     * @param label human-readable label shown in the UI
     */
    DoorLockAction(String label){
        this.label = label;
    }

    /**
     * Converts a string into a corresponding {@link DoorLockAction}.
     *
     * <p>
     * This is used to map incoming request values (e.g., from API calls or UI events)
     * into strongly typed actions for the state machine.
     * </p>
     *
     * @param wantedAction the enum name of the action (e.g., "LOCK", "UNLOCK")
     * @return the matching {@link DoorLockAction}, or null if no match is found
     */
    public static DoorLockAction getActionFromString(String wantedAction){

        // Iterate through all possible values until you find one that
        // matches the wanted action and return it
        for (DoorLockAction action : DoorLockAction.values()){
            if (action.name().equals(wantedAction)){
                return action;
            }
        }
        // Return null if not found
        return null;
    }
}
