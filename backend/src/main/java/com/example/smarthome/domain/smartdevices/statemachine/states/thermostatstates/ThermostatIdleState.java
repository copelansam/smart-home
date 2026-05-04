package com.example.smarthome.domain.smartdevices.statemachine.states.thermostatstates;

import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.ThermostatMode;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition.ThermostatAction;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.thermostattransition.ThermostatTransition;

import java.util.List;
import java.util.Map;

/**
 * Concrete state representing a thermostat in the "idle" state.
 *
 * <p>
 * This state is part of the smart thermostat state machine. It defines:
 * <ul>
 *     <li>Which transitions are allowed while the thermostat is idle</li>
 *     <li>How those transitions modify the device</li>
 *     <li>How the system should respond to those transitions</li>
 * </ul>
 * </p>
 *
 * <p>
 * This state is registered in the {@link StateRegistry} using a static initializer,
 * ensuring it is available for reconstruction from persisted state names.
 * </p>
 */
public class ThermostatIdleState extends StateBase<SmartThermostat> {

    /**
     * Registers this state in the global {@link StateRegistry} so it can be
     * reconstructed from its string representation ("Thermostat Idle").
     */
    static {
        StateRegistry.register("Thermostat Idle", ThermostatIdleState::new);
    }

    /**
     * Constructs the Thermostat Idle state with:
     * <ul>
     *     <li>Allowed transition: START_HEATING, START_COOLING, POWER_THERMOSTAT_OFF</li>
     *     <li>Updatable fields: mode, desired temperature</li>
     * </ul>
     */
    public ThermostatIdleState(){
        super("Thermostat Idle",
                List.of(
                new ThermostatTransition(ThermostatAction.START_COOLING),
                new ThermostatTransition(ThermostatAction.START_HEATING),
                new ThermostatTransition(ThermostatAction.POWER_THERMOSTAT_OFF)
                ),
                List.of(new ThermostatTransition(ThermostatAction.UPDATE_DESIRED_TEMP),
                        new ThermostatTransition(ThermostatAction.UPDATE_MODE))
        );
    }

    /**
     * Executes a transition while the device is in the idle state.
     *
     * <p>
     * Supported transitions:
     * <ul>
     *     <li>START_HEATING → transitions device to {@link ThermostatHeatingState}</li>
     *     <li>START_COOLING → transitions device to {@link ThermostatCoolingState}</li>
     *     <li>POWER_THERMOSTAT_OFF → transitions device to {@link ThermostatOffState}</li>
     * </ul>
     * </p>
     *
     * @param transition string representation of the action
     * @param device the fan being modified
     * @param parameters optional parameters (desireTemp, mode)
     * @return result of the state transition execution
     */
    public CallResult execute(String transition, SmartThermostat device, Map<String, Object> parameters){

        ThermostatAction action = ThermostatAction.getActionFromString(transition);

        if (action == null){
            return new CallResult();
        }

        switch(action){

            case POWER_THERMOSTAT_OFF:
                // Turn thermostat off by transitioning to the off state
                device.setState(new ThermostatOffState());
                device.setIsOn(false);
                return new CallResult("Thermostat has been turned off",true,
                        new DeviceLog(device.getUuid(),"State Change", "State changed from Thermostat Idle to Thermostat Off"));

            case START_COOLING:
                // Check that the thermostat is in either the cooling mode or auto mode
                if (device.getMode() == ThermostatMode.AUTO || device.getMode() == ThermostatMode.COOL) {
                    // If so, transition to the cooling state
                    device.setState(new ThermostatCoolingState());
                    device.setIsOn(true);
                    return new CallResult("The ambient temperature is greater than the desired temperature. Thermostat begins cooling", true,
                            new DeviceLog(device.getUuid(), "State Change", "State changed from Thermostat Idle to Thermostat Cooling"));
                }
                else{ // Otherwise, do not change state. Thermostat cannot start cooling if it is in heat mode
                    return new CallResult("Cannot start cooling while in HEAT mode",false,null);
                }
            case START_HEATING:
                // Check that the thermostat is in either the heating mode or auto mode
                if (device.getMode() == ThermostatMode.AUTO || device.getMode() == ThermostatMode.HEAT) {
                    // If so, transition to the heating state
                    device.setState(new ThermostatHeatingState());
                    device.setIsOn(true);
                    return new CallResult("The ambient temperature is lower than the desired temperature. Thermostat begins heating", true,
                            new DeviceLog(device.getUuid(), "State Change", "State changed from Thermostat Idle to Thermostat Heating"));
                } else{ // Otherwise, do not change state. Thermostat cannot start cooling if it is in cool mode
                    return new CallResult("Cannot start heating while in COOL mode",false,null);
                }

            case UPDATE_DESIRED_TEMP:
                // Update desired temp by retrieving value from parameters and pass it to the device.
                // Device state does not change
                double newTemp = ((Number) parameters.get("desiredTemp")).doubleValue();
                device.setDesiredTemperature(newTemp);
                return new CallResult("The thermostat's desired temperature has been updated to: " + newTemp + " F",
                        true, new DeviceLog(device.getUuid(),"Properties Change","Desired Temperature has been updated to:" + newTemp + " F"));

            case UPDATE_MODE:
                // Update mode by retrieving value from parameters and pass it to the device.
                // Device state does not change
                String modeString = parameters.get("mode").toString();
                ThermostatMode newMode = ThermostatMode.valueOf(modeString);
                device.setMode(newMode);
                return new CallResult("The devices mode has been changed to: " + newMode, true,
                        new DeviceLog(device.getUuid(), "Properties Change", "Mode has been changed to: " + newMode));

            default:
                return new CallResult();
        }
    }
}
