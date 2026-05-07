package com.example.smarthome.simulation.strategies;

import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.ThermostatMode;
import com.example.smarthome.simulation.ThermostatSimulationService;
import org.springframework.stereotype.Component;

/**
 * Factory responsible for selecting the appropriate thermostat
 * simulation strategy for a simulation cycle.
 *
 * This factory evaluates the thermostat's current operating mode
 * and temperature difference in order to determine which behavior
 * should be executed:
 * - cooling
 * - heating
 * - idle correction
 *
 * Strategy selection is based on:
 * - the difference between ambient and desired temperature
 * - the thermostat's configured operating mode
 *
 * The selected strategy is then executed by the
 * ThermostatSimulationService.
 *
 * This factory centralizes simulation decision-making so that
 * individual strategy implementations remain focused solely on
 * executing behavior rather than determining applicability.
 */
@Component
public class ThermostatStrategyFactory {

    /**
     * Strategy used when active heating is required.
     */
    private final HeatingStrategy heatingStrategy;

    /**
     * Strategy used when active cooling is required.
     */
    private final CoolingStrategy coolingStrategy;

    /**
     * Strategy used when the thermostat should return to or
     * maintain an idle state.
     */
    private final IdleStrategy idleStrategy;

    /**
     * Creates a new strategy factory with all available
     * thermostat simulation strategies.
     *
     * @param heatingStrategy strategy responsible for heating behavior
     * @param coolingStrategy strategy responsible for cooling behavior
     * @param idleStrategy strategy responsible for idle correction behavior
     */
    public ThermostatStrategyFactory(
            HeatingStrategy heatingStrategy,
            CoolingStrategy coolingStrategy,
            IdleStrategy idleStrategy
    ) {
        this.heatingStrategy = heatingStrategy;
        this.coolingStrategy = coolingStrategy;
        this.idleStrategy = idleStrategy;
    }

    /**
     * Determines which thermostat simulation strategy should be
     * executed for the current simulation cycle.
     *
     * Cooling is selected when:
     * - the ambient temperature exceeds the desired temperature
     *   by at least the configured threshold
     * - and the thermostat mode permits cooling
     *
     * Heating is selected when:
     * - the ambient temperature is below the desired temperature
     *   by at least the configured threshold
     * - and the thermostat mode permits heating
     *
     * Idle correction is selected when:
     * - the ambient temperature is within the configured threshold
     *   of the desired temperature
     *
     * @param thermostat the thermostat being evaluated
     * @param tempDifference the difference between ambient and
     *                       desired temperature
     * @return the strategy that should handle the current
     *         simulation cycle
     * @throws IllegalStateException if no valid strategy matches
     *         the thermostat's current conditions
     */
    public IThermostatStrategy decideStrategy(SmartThermostat thermostat, double tempDifference){

        if ((tempDifference >= ThermostatSimulationService.TEMP_THRESHOLD) &&
        (thermostat.getMode() == ThermostatMode.AUTO || thermostat.getMode() == ThermostatMode.COOL)){
            return coolingStrategy;
        }
        else if((tempDifference <= -ThermostatSimulationService.TEMP_THRESHOLD) &&
                (thermostat.getMode() == ThermostatMode.AUTO || thermostat.getMode() == ThermostatMode.HEAT)){
            return heatingStrategy;
        }
        else if (Math.abs(tempDifference) < ThermostatSimulationService.TEMP_THRESHOLD){
            return idleStrategy;
        }
        else{
            throw new IllegalStateException("No strategy matched");
        }
    }
}
