package com.example.smarthome.service;

import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.simulation.ThermostatResult;
import com.example.smarthome.simulation.strategies.IThermostatStrategy;
import com.example.smarthome.simulation.strategies.ThermostatStrategyFactory;
import org.springframework.stereotype.Service;

/**
 * Orchestrates a single thermostat simulation cycle.
 *
 * This service acts as the entry point for thermostat simulation
 * and coordinates the execution of simulation behavior.
 *
 * It is responsible for:
 * - retrieving the thermostat's current state
 * - calculating environmental temperature differences
 * - delegating strategy selection to the ThermostatStrategyFactory
 * - executing the selected thermostat strategy
 *
 * All simulation rules and behavioral decisions are delegated to
 * the strategy layer and are not implemented within this service.
 *
 * This service does not define simulation rules. Instead, it
 * orchestrates rule execution by selecting and invoking the
 * appropriate strategy for each simulation cycle.
 */
@Service
public class ThermostatSimulationService {

    /**
     * Temperature difference threshold used to decide when heating or cooling
     * should be triggered.
     *
     * If the difference between ambient and desired temperature exceeds this
     * value, the thermostat will attempt to actively heat or cool.
     */
    public static final int TEMP_THRESHOLD = 1;

    /**
     * Factory used to determine which thermostat strategy should be used (cool, heat, idle)
     */
    private final ThermostatStrategyFactory strategyFactory;

    public ThermostatSimulationService(ThermostatStrategyFactory strategyFactory){
        this.strategyFactory = strategyFactory;
    }

    /**
     * Evaluates a thermostat for a single simulation cycle.
     *
     * This method acts as the primary orchestration point for
     * thermostat simulation behavior.
     *
     * During evaluation, the service:
     * - retrieves the thermostat's current environmental state
     * - calculates the temperature difference between ambient
     *   and desired temperature
     * - delegates strategy selection to the
     *   ThermostatStrategyFactory
     * - executes the selected thermostat simulation strategy
     *
     * If the thermostat is currently off, no simulation behavior
     * is executed and an empty ThermostatResult is returned.
     *
     * Any state transitions, temperature adjustments, and
     * simulation logs produced during execution are recorded
     * in the returned ThermostatResult.
     *
     * @param thermostat the thermostat being evaluated
     * @return a result containing all simulation changes,
     *         logs, and state transitions generated during
     *         the simulation cycle
     */
    public ThermostatResult evaluate(SmartThermostat thermostat){

        ThermostatResult result = new ThermostatResult();

        // If the thermostat is off, don't do anything
        if (thermostat.getState().contains("Off")){
            return result;
        }

        // Store variables used for evaluation
        double ambientTemperature = thermostat.getAmbientTemperature().temperature();
        double desiredTemperature = thermostat.getDesiredTemperature().temperature();
        double tempDifference = ambientTemperature - desiredTemperature;

        // Use the factory to determine which thermostat strategy to use
        IThermostatStrategy thermostatStrategy = strategyFactory.decideStrategy(thermostat, tempDifference);

        // Execute that strategy and return its result
        return thermostatStrategy.apply(thermostat, ambientTemperature, desiredTemperature);

    }
}
