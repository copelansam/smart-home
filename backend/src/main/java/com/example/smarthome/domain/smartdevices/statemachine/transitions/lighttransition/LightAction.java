package com.example.smarthome.domain.smartdevices.statemachine.transitions.lighttransition;

/**
 * Represents all valid actions that can be performed on a light device.
 *
 * <p>
 * These actions are used by the light state machine to control transitions
 * between states such as on and off.
 * </p>
 *
 * <p>
 * Each action also contains a human-readable label used for UI display purposes.
 * This allows the backend to expose strongly typed actions while still providing
 * user-friendly text for front-end rendering.
 * </p>
 */
public enum LightAction {

    /** Turns the light on. Displayed in the UI as "Turn On" */
    TURN_LIGHT_ON("Turn On"),

    /** Turns the light off. Displayed in the UI as "Turn Off"*/
    TURN_LIGHT_OFF("Turn Off"),

    /** Updates the light's brightness percentage based on a passed parameter in the state machine.
     * Displayed in the UI as "Update Brightness" */
    UPDATE_BRIGHTNESS("Update Brightness"),

    /** Updates the light's color based on passed parameters in the state machine.
     * Displayed in the UI as "Update Color" */
    UPDATE_COLOR("Update Color");

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
     * Creates a light action with a UI display label.
     *
     * @param label human-readable label shown in the UI
     */
    LightAction(String label){
        this.label = label;
    }

    /**
     * Converts a string into a corresponding {@link LightAction}.
     *
     * <p>
     * This is used to map incoming request values (e.g., from API calls or UI events)
     * into strongly typed actions for the state machine.
     * </p>
     *
     * @param wantedAction the enum name of the action (e.g., "TURN_ON", "TURN_OFF")
     * @return the matching {@link LightAction}, or null if no match is found
     */
    public static LightAction getActionFromString(String wantedAction){

        // Iterate through all possible values until you find one that
        // matches the wanted action and return it
        for (LightAction action : LightAction.values()){
            if (action.name().equals(wantedAction)){
                return action;
            }
        }
        // Return null if not found
        return null;
    }
}
