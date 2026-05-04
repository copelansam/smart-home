package com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition;

/**
 * Represents all valid actions that can be performed on a thermostat device.
 *
 * <p>
 * These actions are used by the thermostat state machine to control transitions
 * between states such as heating, cooling, idle, and off.
 * </p>
 *
 * <p>
 * Some actions are exposed to the UI layer and include a human-readable label
 * (e.g. "Turn On", "Update Mode"). These are intended to be displayed directly
 * to the user.
 * </p>
 *
 * <p>
 * Other actions are internal-only state transitions (e.g. START_HEATING,
 * STOP_COOLING). These do not have UI labels and are not intended to be
 * triggered directly by the client. They are used exclusively by the
 * state machine to manage device behavior internally.
 * </p>
 */
public enum ThermostatAction {

    /** Turns the thermostat on. Displayed in the UI as "Turn On" */
    POWER_THERMOSTAT_ON("Turn On"),

    /** Turns the thermostat off. Displayed in the UI as "Turn Off" */
    POWER_THERMOSTAT_OFF("Turn Off"),

    /** Turns the thermostat to heating mode. Not displayed in the UI */
    START_HEATING(null),

    /** Turns the thermostat to idle from heating. Not displayed in the UI */
    STOP_HEATING(null),

    /** Turns the thermostat to cooling mode. Not displayed in the UI */
    START_COOLING(null),

    /** Turns the thermostat to idle from cooling. Not displayed in the UI */
    STOP_COOLING(null),

    /** Updates the desired temperature based on parameters passed into the state machine.
     *  Displayed in the UI as "Update Desired Temp" */
    UPDATE_DESIRED_TEMP("Update Desired Temp"),

    /** Updates the thermostat mode based on parameters passed into the state machine.
     *  Displayed in the UI as "Update Mode" */
    UPDATE_MODE("Update Mode");

    /**
     * Human-readable label used for UI display.
     *
     * <p>
     * A null value indicates that this action is internal-only and should not
     * be exposed or triggered directly by the client/UI layer.
     * </p>
     */
    String label;

    /**
     * Creates a thermostat action with a UI display label.
     *
     * @param label human-readable label shown in the UI
     */
    ThermostatAction(String label){
        this.label = label;
    }

    /**
     * Converts a string into a corresponding {@link ThermostatAction}.
     *
     * <p>
     * This is used to map incoming request values (e.g., from API calls or UI events)
     * into strongly typed actions for the state machine.
     * </p>
     *
     * @param wantedAction the enum name of the action (e.g., "TURN_ON", "TURN_OFF")
     * @return the matching {@link ThermostatAction}, or null if no match is found
     */
    public static ThermostatAction getActionFromString(String wantedAction){

        // Iterate through all possible values until you find one that
        // matches the wanted action and return it
        for (ThermostatAction action : ThermostatAction.values()){
            if (action.name().equals(wantedAction)){
                return action;
            }
        }
        // Return null if not found
        return null;
    }
}
