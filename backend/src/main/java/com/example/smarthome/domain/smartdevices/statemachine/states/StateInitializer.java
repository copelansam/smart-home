package com.example.smarthome.domain.smartdevices.statemachine.states;

import com.example.smarthome.domain.smartdevices.statemachine.states.doorlockstates.DoorLockedState;
import com.example.smarthome.domain.smartdevices.statemachine.states.doorlockstates.DoorUnlockedState;
import com.example.smarthome.domain.smartdevices.statemachine.states.fanstates.FanOffState;
import com.example.smarthome.domain.smartdevices.statemachine.states.fanstates.FanOnState;
import com.example.smarthome.domain.smartdevices.statemachine.states.lightstates.LightOffState;
import com.example.smarthome.domain.smartdevices.statemachine.states.lightstates.LightOnState;
import com.example.smarthome.domain.smartdevices.statemachine.states.thermostatstates.ThermostatCoolingState;
import com.example.smarthome.domain.smartdevices.statemachine.states.thermostatstates.ThermostatHeatingState;
import com.example.smarthome.domain.smartdevices.statemachine.states.thermostatstates.ThermostatIdleState;
import com.example.smarthome.domain.smartdevices.statemachine.states.thermostatstates.ThermostatOffState;

/**
 * Utility class responsible for initializing all state machine states.
 *
 * <p>
 * This class forces the loading of all concrete {@link IState} implementations
 * so that any static initialization logic (such as state registration in a
 * registry or factory) is executed at application startup.
 * </p>
 *
 * <p>
 * This is typically used to ensure that all states are registered and available
 * before any smart devices begin executing transitions.
 * </p>
 *
 * <p>
 * This class is not meant to be instantiated.
 * </p>
 */
public class StateInitializer {

    /**
     * Forces loading of all known state classes.
     *
     * <p>
     * Each class is explicitly loaded using {@link Class#forName(String)} to trigger
     * static initialization blocks or registration logic inside the state classes.
     * </p>
     *
     * <p>
     * If any state class fails to load, the application will fail fast with a runtime exception.
     * </p>
     *
     * @throws RuntimeException if any state class cannot be loaded
     */
    public static void init() throws RuntimeException{

        // An array of all of the state class references
        Class<?>[] classes = {
                DoorUnlockedState.class,
                DoorLockedState.class,
                FanOnState.class,
                FanOffState.class,
                LightOnState.class,
                LightOffState.class,
                ThermostatOffState.class,
                ThermostatIdleState.class,
                ThermostatHeatingState.class,
                ThermostatCoolingState.class
        };

        // Iterate through the state classes array and load and initialize each one
        for (Class<?> c : classes) {
            try {
                Class.forName(c.getName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
