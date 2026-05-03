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

public class ThermostatCoolingState extends StateBase<SmartThermostat> {

    static {
        StateRegistry.register("Thermostat Cooling", ThermostatCoolingState::new);
    }

    public ThermostatCoolingState(){
        super("Thermostat Cooling",
                List.of(
                new ThermostatTransition(ThermostatAction.STOP_COOLING),
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

        switch (action){

            case STOP_COOLING:
                device.setState(new ThermostatIdleState());
                device.setIsOn(false);
                return new CallResult("The ambient temperature has reached the desired temperature. The thermostat has gone idle.", true,
                        new DeviceLog(device.getUuid(),"State Change", "State changed from Thermostat Cooling to Thermostat Idle"));

            case POWER_THERMOSTAT_OFF:
                device.setState(new ThermostatOffState());
                device.setIsOn(false);
                return new CallResult("The thermostat has been turned off.", true,
                        new DeviceLog(device.getUuid(),"State Change", "State changed from Thermostat Cooling to Thermostat Off"));

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
