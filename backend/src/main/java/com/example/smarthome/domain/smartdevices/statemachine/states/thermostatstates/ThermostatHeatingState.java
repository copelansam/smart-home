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
 * Concrete state representing a thermostat in the "heating" state.
 *
 * <p>
 * This state is part of the smart thermostat state machine. It defines:
 * <ul>
 *     <li>Which transitions are allowed while the thermostat is heating</li>
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
public class ThermostatHeatingState extends StateBase<SmartThermostat> {

    /**
     * Registers this state in the global {@link StateRegistry} so it can be
     * reconstructed from its string representation ("Thermostat Heating").
     */
    static {
        StateRegistry.register("Thermostat Heating", ThermostatHeatingState::new);
    }

    /**
     * Constructs the Thermostat Heating state with:
     * <ul>
     *     <li>Allowed transition: STOP_HEATING, POWER_THERMOSTAT_OFF</li>
     *     <li>Updatable fields: mode, desired temperature</li>
     * </ul>
     */
    public ThermostatHeatingState(){
        super("Thermostat Heating",
                List.of(
                new ThermostatTransition(ThermostatAction.STOP_HEATING),
                new ThermostatTransition(ThermostatAction.POWER_THERMOSTAT_OFF)
                ),
                List.of(new ThermostatTransition(ThermostatAction.UPDATE_DESIRED_TEMP),
                        new ThermostatTransition(ThermostatAction.UPDATE_MODE))
        );
    }

    /**
     * Executes a transition while the device is in the heating state.
     *
     * <p>
     * Supported transitions:
     * <ul>
     *     <li>STOP_HEATING → transitions device to {@link ThermostatIdleState}</li>
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

        switch (action){

            case STOP_HEATING:
                // Stop heating by transitioning device to idle state
                device.setState(new ThermostatIdleState());
                device.setIsOn(false);
                return new CallResult("The ambient temperature has reached the desired temperature. The thermostat has gone idle.",true,
                        new DeviceLog(device.getUuid(),"State Change", "State changed from Thermostat Heating to Thermostat Idle"));

            case POWER_THERMOSTAT_OFF:
                // Power thermostat off by transitioning device to off state
                device.setState(new ThermostatOffState());
                device.setIsOn(false);
                return new CallResult("The thermostat has been turned off.", true,
                        new DeviceLog(device.getUuid(),"State Change", "State changed from Thermostat Heating to Thermostat Off"));

            case UPDATE_DESIRED_TEMP:
                // Update desired temp by retrieving value from parameters and pass it to the device.
                // Device state does not change
                double newTemp = ((Number) parameters.get("desiredTemp")).doubleValue();
                device.setDesiredTemperature(newTemp);
                return new CallResult("The thermostat's desired temperature has been updated to: " + newTemp + " F",
                        true, new DeviceLog(device.getUuid(),"Properties Change",
                        "Desired Temperature has been updated to:" + newTemp + " F"));

            case UPDATE_MODE:
                // Update mode by retrieving value from parameters and pass it to the device.
                // Device state does not change
                String modeString = parameters.get("mode").toString();
                ThermostatMode newMode = ThermostatMode.valueOf(modeString);;
                device.setMode(newMode);
                return new CallResult("The devices mode has been changed to: " + newMode, true,
                        new DeviceLog(device.getUuid(), "Properties Change", "Mode has been changed to: " + newMode));

            default:
                return new CallResult();
        }

    }
}
