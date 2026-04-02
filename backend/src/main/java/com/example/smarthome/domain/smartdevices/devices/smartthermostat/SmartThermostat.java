package com.example.smarthome.domain.smartdevices.devices.smartthermostat;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.IState;

public class SmartThermostat extends SmartDeviceBase {

    private Temperature desiredTemperature;
    private Temperature ambientTemperature;
    private IState state;

    public SmartThermostat(String name, String location, DeviceType deviceType, double desiredTemperature, double ambientTemperature){
        super(name, location, deviceType);
        this.desiredTemperature = new Temperature(desiredTemperature);
        this.ambientTemperature = new Temperature(ambientTemperature);
    }

    public void setState(IState newState){
        this.state = state.execute().getNewState();
    }
}
