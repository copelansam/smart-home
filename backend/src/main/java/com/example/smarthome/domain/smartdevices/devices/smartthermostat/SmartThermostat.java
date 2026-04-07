package com.example.smarthome.domain.smartdevices.devices.smartthermostat;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.IState;
import com.example.smarthome.domain.smartdevices.statemachine.states.thermostatstates.ThermostatOffState;
import jakarta.persistence.*;

@Entity
@Table(name = "smart_thermostat")
public class SmartThermostat extends SmartDeviceBase {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "temperature", column = @Column(name = "desired_temperature"))
    })
    private Temperature desiredTemperature;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "temperature", column = @Column(name = "ambient_temperature"))
    })
    private Temperature ambientTemperature;

    public SmartThermostat(){}

    public SmartThermostat(String name, String location, DeviceType deviceType, double desiredTemperature, double ambientTemperature){
        super(name, location, deviceType);
        this.desiredTemperature = new Temperature(desiredTemperature);
        this.ambientTemperature = new Temperature(ambientTemperature);
        this.state = new ThermostatOffState();
    }

    public Temperature getDesiredTemperature(){
        return this.desiredTemperature;
    }

    public Temperature getAmbientTemperature(){
        return this.ambientTemperature;
    }

}
