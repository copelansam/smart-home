package com.example.smarthome.simulation;

import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.ThermostatMode;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import org.springframework.stereotype.Service;

/**
 * Encapsulates the thermostat simulation rules and decision logic.
 *
 * This service evaluates a thermostat's current state against its desired
 * temperature and determines what actions should be taken during a simulation step.
 *
 * It is responsible for:
 * - Determining whether heating, cooling, or idle correction is required
 * - Triggering thermostat state transitions when necessary
 * - Adjusting ambient temperature in response to simulation rules
 * - Producing a ThermostatResult describing all changes and side effects
 *
 * This service does not perform persistence, logging, or messaging.
 * It only evaluates and applies simulation rules to a thermostat instance.
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
     * Evaluates a thermostat for a single simulation step.
     *
     * Compares the current ambient temperature with the desired temperature
     * and determines whether the thermostat should:
     * - start heating
     * - start cooling
     * - or perform idle correction (stop heating/cooling if needed)
     *
     * This method applies the appropriate simulation rules and updates the
     * thermostat state accordingly.
     *
     * Any state changes, temperature adjustments, and transition logs are
     * recorded in the returned ThermostatResult.
     *
     * @param thermostat the thermostat being evaluated
     * @return a result object containing all changes and side effects
     */
    public ThermostatResult evaluate(SmartThermostat thermostat){

        ThermostatResult result = new ThermostatResult();

        // If the thermostat is off, don't do anything
        if (thermostat.getState().contains("Off")){
            return result;
        }

        double ambientTemperature = thermostat.getAmbientTemperature().temperature();
        double desiredTemperature = thermostat.getDesiredTemperature().temperature();
        double tempDifference = ambientTemperature - desiredTemperature;

        ThermostatMode mode = thermostat.getMode();


        // If it is colder than the desired temperature, try heating
        if (tempDifference >= TEMP_THRESHOLD){
            handleCooling(thermostat, mode, ambientTemperature, result);
        }
        // If it is hotter that the desired temperature, try cooling
        else if (tempDifference <= (-TEMP_THRESHOLD)){
            handleHeating(thermostat, mode, ambientTemperature, result);
        }
        else{
            // If the ambient and desired temperatures are within the threshold of each other, go idle
            handleIdleCorrection(thermostat, ambientTemperature,desiredTemperature, result);
        }

        // return the result object that contains what happened this cycle
        return result;
    }

    /**
     * Applies cooling logic when the ambient temperature is above the desired range.
     *
     * If the thermostat mode allows cooling (COOL or AUTO):
     * - transitions the thermostat into cooling state if not already active
     * - decreases ambient temperature
     * - records the change in the result object
     *
     * @param thermostat the thermostat being evaluated
     * @param mode the current thermostat mode
     * @param ambient current ambient temperature before adjustment
     * @param result accumulator for simulation changes and logs
     */
    private void handleCooling(SmartThermostat thermostat, ThermostatMode mode, double ambient, ThermostatResult result){

        // If the thermostat is in cool or auto mode, reduce the ambient temperature
        if (mode == ThermostatMode.COOL || mode == ThermostatMode.AUTO){

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
        }
    }

    /**
     * Applies heating logic when the ambient temperature is below the desired range.
     *
     * If the thermostat mode allows heating (HEAT or AUTO):
     * - transitions the thermostat into heating state if not already active
     * - increases ambient temperature
     * - records the change in the result object
     *
     * @param thermostat the thermostat being evaluated
     * @param mode the current thermostat mode
     * @param ambient current ambient temperature before adjustment
     * @param result accumulator for simulation changes and logs
     */
    public void handleHeating(SmartThermostat thermostat, ThermostatMode mode, double ambient, ThermostatResult result){

        // If the thermostat is in heat or auto mode, increase the ambient temperature
        if (mode == ThermostatMode.HEAT || mode == ThermostatMode.AUTO){

            // If the thermostat is not already in the heating state, transition to it
            if (!thermostat.getState().contains("Heating")){
                CallResult thermostatStateChange = thermostat.execute("START_HEATING", null);
                result.addTransitionLog(thermostatStateChange);
            }

            // Adjust the ambient temperature and log it
            thermostat.updateAmbientTemperature(1);
            result.markChanged();

            result.addLog(new DeviceLog(thermostat.getUuid(), "Temperature Update",
                    "The ambient temperature has been increased to: " + (ambient + 1)));
        }

    }

    /**
     * Handles idle-state corrections when temperature is within acceptable range.
     *
     * If the thermostat is currently heating or cooling but the ambient
     * temperature has reached the desired range, this method stops the
     * active state:
     * - stops cooling if temperature has dropped to or below desired
     * - stops heating if temperature has risen to or above desired
     *
     * Any state transitions are recorded in the result object.
     *
     * @param thermostat the thermostat being evaluated
     * @param ambient current ambient temperature
     * @param desired desired target temperature
     * @param result accumulator for simulation changes and logs
     */
    public void handleIdleCorrection(SmartThermostat thermostat, double ambient, double desired, ThermostatResult result){

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
    }
}
