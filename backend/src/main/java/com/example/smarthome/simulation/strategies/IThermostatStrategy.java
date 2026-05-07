package com.example.smarthome.simulation.strategies;

import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.simulation.ThermostatResult;

/**
 * Defines a simulation strategy for a smart thermostat.
 *
 * Implementations encapsulate a specific thermostat behavior
 * that may occur during a simulation cycle, such as:
 * - heating
 * - cooling
 * - idle correction
 *
 * A strategy is selected by the ThermostatStrategyFactory
 * based on the thermostat's current environmental conditions
 * and operating mode.
 *
 * Each strategy is responsible for:
 * - applying thermostat state transitions when necessary
 * - updating ambient temperature
 * - recording any simulation side effects in the returned result
 *
 * Strategies do not determine when they should be used.
 * Strategy selection is handled externally by the factory.
 */
public interface IThermostatStrategy {

    /**
     * Applies this simulation strategy to the provided thermostat.
     *
     * The strategy may:
     * - modify the thermostat's state
     * - adjust ambient temperature
     * - produce transition logs and device logs
     *
     * Any changes performed during execution should be recorded
     * in the returned ThermostatResult.
     *
     * @param thermostat the thermostat being simulated
     * @param ambient the current ambient temperature
     * @param desired the desired target temperature
     * @return a result describing all simulation changes and side effects
     */
    ThermostatResult apply(SmartThermostat thermostat, double ambient, double desired);
}
