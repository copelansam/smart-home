package com.example.smarthome.simulation.strategies;

import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import com.example.smarthome.simulation.ThermostatResult;
import org.springframework.stereotype.Component;

/**
 * Simulation strategy responsible for idle-state correction behavior.
 *
 * This strategy is applied when the ambient temperature is within
 * the acceptable threshold of the thermostat's desired temperature.
 *
 * When executed, the strategy:
 * - stops active cooling if the desired temperature has been reached
 * - stops active heating if the desired temperature has been reached
 * - records any resulting state transitions in the returned
 *   ThermostatResult
 *
 * Unlike heating and cooling strategies, this strategy does not
 * modify ambient temperature directly. Its responsibility is to
 * ensure the thermostat returns to an idle state when active
 * temperature correction is no longer required.
 *
 * This strategy assumes that idle behavior has already been
 * determined to be the correct action by the
 * ThermostatStrategyFactory.
 */
@Component
public class IdleStrategy implements IThermostatStrategy {

    /**
     * Applies idle correction behavior to the provided thermostat.
     *
     * If the thermostat is currently cooling and the ambient
     * temperature has dropped to or below the desired temperature,
     * cooling is stopped.
     *
     * If the thermostat is currently heating and the ambient
     * temperature has risen to or above the desired temperature,
     * heating is stopped.
     *
     * @param thermostat the thermostat being simulated
     * @param ambient the current ambient temperature
     * @param desired the desired target temperature
     * @return a result containing all simulation changes
     *         and state transition logs
     */
    @Override
    public ThermostatResult apply(SmartThermostat thermostat, double ambient, double desired){

        ThermostatResult result = new ThermostatResult();

        // If the thermostat is in the cooling state, stop cooling
        if (ambient <= desired && thermostat.getState().contains("Cooling")){
            CallResult thermostatStateChange = thermostat.execute("STOP_COOLING", null);
            result.addTransitionLog(thermostatStateChange);
            result.markChanged();
        }
        // If the thermostat is in the heating state, stop heating
        else if (ambient >= desired && thermostat.getState().contains("Heating")){
            CallResult thermostatStateChange = thermostat.execute("STOP_HEATING", null);
            result.addTransitionLog(thermostatStateChange);
            result.markChanged();
        }

        return result;
    }
}
