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

public class ThermostatIdleState extends StateBase<SmartThermostat> {

    static {
        StateRegistry.register("Thermostat Idle", ThermostatIdleState::new);
    }

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

    public CallResult execute(String transition, SmartThermostat device, Map<String, Object> parameters){

        ThermostatAction action = ThermostatAction.getActionFromString(transition);

        if (action == null){
            return new CallResult();
        }

        switch(action){

            case POWER_THERMOSTAT_OFF:
                device.setState(new ThermostatOffState());
                device.setIsOn(false);
                return new CallResult("Thermostat has been turned off",true,
                        new DeviceLog(device.getUuid(),"State Change", "State changed from Thermostat Idle to Thermostat Off"));

            case START_COOLING:

                if (device.getMode() == ThermostatMode.AUTO || device.getMode() == ThermostatMode.COOL) {
                    device.setState(new ThermostatCoolingState());
                    device.setIsOn(true);
                    return new CallResult("The ambient temperature is greater than the desired temperature. Thermostat begins cooling", true,
                            new DeviceLog(device.getUuid(), "State Change", "State changed from Thermostat Idle to Thermostat Cooling"));
                }
                else{
                    return new CallResult("Cannot start cooling while in HEAT mode",false,null);
                }
            case START_HEATING:
                if (device.getMode() == ThermostatMode.AUTO || device.getMode() == ThermostatMode.HEAT) {
                    device.setState(new ThermostatHeatingState());
                    device.setIsOn(true);
                    return new CallResult("The ambient temperature is lower than the desired temperature. Thermostat begins heating", true,
                            new DeviceLog(device.getUuid(), "State Change", "State changed from Thermostat Idle to Thermostat Heating"));
                } else{
                    return new CallResult("Cannot start heating while in COOL mode",false,null);
                }
            case UPDATE_DESIRED_TEMP:
                double newTemp = ((Number) parameters.get("desiredTemp")).doubleValue();
                device.setDesiredTemperature(newTemp);
                return new CallResult("The thermostat's desired temperature has been updated to: " + newTemp + " F",
                        true, new DeviceLog(device.getUuid(),"Properties Change","Desired Temperature has been updated to:" + newTemp + " F"));

            case UPDATE_MODE:
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
