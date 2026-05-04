package com.example.smarthome.domain.smartdevices.statemachine.transitions.fantransition;

/**
 * Represents all valid actions that can be performed on a fan device.
 *
 * <p>
 * These actions are used by the fan state machine to control transitions
 * between states such as on and off.
 * </p>
 *
 * <p>
 * Each action also contains a human-readable label used for UI display purposes.
 * This allows the backend to expose strongly typed actions while still providing
 * user-friendly text for front-end rendering.
 * </p>
 */
public enum FanAction {

    /** Turns the fan on. Displayed in the UI as "Turn On" */
    TURN_FAN_ON("Turn On"),

    /** Turns the fan off. Displayed in the UI as "Turn Off"*/
    TURN_FAN_OFF("Turn Off"),

    /** Updates the fan's speed based on a passed parameter in the state machine.
     * Displayed in the UI as "Update Speed" */
    UPDATE_SPEED("Update Speed");

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
    FanAction(String label){
        this.label = label;
    }

    /**
     * Converts a string into a corresponding {@link FanAction}.
     *
     * <p>
     * This is used to map incoming request values (e.g., from API calls or UI events)
     * into strongly typed actions for the state machine.
     * </p>
     *
     * @param wantedAction the enum name of the action (e.g., "TURN_ON", "TURN_OFF")
     * @return the matching {@link FanAction}, or null if no match is found
     */
    public static FanAction getActionFromString(String wantedAction){

        // Iterate through all possible values until you find one that
        // matches the wanted action and return it
        for (FanAction action : FanAction.values()){
            if (action.name().equals(wantedAction)){
                return action;
            }
        }
        // Return null if not found
        return null;
    }
}
