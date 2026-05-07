package com.example.smarthome.simulation.strategies;

import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import com.example.smarthome.simulation.ThermostatResult;
import org.springframework.stereotype.Component;

/**
 * Simulation strategy responsible for cooling behavior.
 *
 * This strategy applies active cooling to a thermostat during
 * a simulation cycle.
 *
 * When executed, the strategy:
 * - transitions the thermostat into the cooling state if necessary
 * - decreases the ambient temperature
 * - records any state transitions and temperature updates
 *   in the returned ThermostatResult
 *
 * This strategy assumes that cooling has already been determined
 * to be the correct action by the ThermostatStrategyFactory.
 */
@Component
public class CoolingStrategy implements IThermostatStrategy {

    /**
     * Applies cooling behavior to the provided thermostat.
     *
     * If the thermostat is not already cooling, a cooling
     * state transition is triggered before reducing the
     * ambient temperature.
     *
     * @param thermostat the thermostat being simulated
     * @param ambient the current ambient temperature
     * @param desired the desired target temperature
     * @return a result containing all simulation changes,
     *         logs, and state transitions
     */
    @Override
    public ThermostatResult apply(SmartThermostat thermostat, double ambient, double desired){

        ThermostatResult result = new ThermostatResult();

        // If the thermostat is not already in the cooling state, transition to it
        if (!thermostat.getState().contains("Cooling")){
            CallResult thermostatStateChange = thermostat.execute("START_COOLING", null);
            result.addTransitionLog(thermostatStateChange);
        }

        // Adjust the ambient temperature and log it
        thermostat.updateAmbientTemperature(-1);
        result.markChanged();

        result.addLog(new DeviceLog(thermostat.getUuid(), "Temperature Update",
                "The ambient temperature has been decreased to: " + (ambient - 1)));

        return result;
    }
}
