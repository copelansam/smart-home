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

public class ThermostatOffState extends StateBase<SmartThermostat> {

    static {
        StateRegistry.register("Thermostat Off", ThermostatOffState::new);
    }

    public ThermostatOffState(){
        super("Thermostat Off",
                List.of(
                new ThermostatTransition(ThermostatAction.POWER_THERMOSTAT_ON)
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

            case POWER_THERMOSTAT_ON:
                device.setState(new ThermostatIdleState());
                return new CallResult("Thermostat has been set to idle. Awaiting further instruction",true,
                        new DeviceLog(device.getUuid(),"State Change", "State changed from Thermostat Off to Thermostat Idle"));

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
