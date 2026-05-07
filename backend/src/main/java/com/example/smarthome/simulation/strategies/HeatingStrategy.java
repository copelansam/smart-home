package com.example.smarthome.simulation.strategies;

import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import com.example.smarthome.simulation.ThermostatResult;
import org.springframework.stereotype.Component;

/**
 * Simulation strategy responsible for heating behavior.
 *
 * This strategy applies active heating to a thermostat during
 * a simulation cycle.
 *
 * When executed, the strategy:
 * - transitions the thermostat into the heating state if necessary
 * - increases the ambient temperature
 * - records any state transitions and temperature updates
 *   in the returned ThermostatResult
 *
 * This strategy assumes that heating has already been determined
 * to be the correct action by the ThermostatStrategyFactory.
 */
@Component
public class HeatingStrategy implements IThermostatStrategy {

    /**
     * Applies heating behavior to the provided thermostat.
     *
     * If the thermostat is not already heating, a heating
     * state transition is triggered before increasing the
     * ambient temperature.
     *
     * @param thermostat the thermostat being simulated
     * @param ambient the current ambient temperature
     * @param desired the desired target temperature
     *                (included for strategy interface consistency)
     * @return a result containing all simulation changes,
     *         logs, and state transitions
     */
    @Override
    public ThermostatResult apply(SmartThermostat thermostat, double ambient, double desired) {

        ThermostatResult result = new ThermostatResult();

        // If the thermostat is not already in the heating state, transition to it
        if (!thermostat.getState().contains("Heating")) {
            CallResult thermostatStateChange = thermostat.execute("START_HEATING", null);
            result.addTransitionLog(thermostatStateChange);
        }

        // Adjust the ambient temperature and log it
        thermostat.updateAmbientTemperature(1);
        result.markChanged();

        result.addLog(new DeviceLog(thermostat.getUuid(), "Temperature Update",
                "The ambient temperature has been increased to: " + (ambient + 1)));

        return result;

    }
}
