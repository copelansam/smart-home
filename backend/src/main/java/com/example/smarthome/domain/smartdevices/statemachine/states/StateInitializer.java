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

public class StateInitializer {
    public static void init() {

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

        for (Class<?> c : classes) {
            try {
                Class.forName(c.getName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
