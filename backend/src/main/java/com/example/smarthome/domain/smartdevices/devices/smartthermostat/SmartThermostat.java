package com.example.smarthome.domain.smartdevices.devices.smartthermostat;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.thermostatstates.ThermostatOffState;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

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

    public SmartThermostat(String name, String location){
        super(name, location, DeviceType.THERMOSTAT);
        this.desiredTemperature = new Temperature();
        this.ambientTemperature = new Temperature();
        this.state = new ThermostatOffState();
    }

    public SmartThermostat(String name, String location, double desiredTemperature, double ambientTemperature){
        super(name, location, DeviceType.THERMOSTAT);
        this.desiredTemperature = new Temperature();
        this.ambientTemperature = new Temperature();
        this.state = new ThermostatOffState();
        this.desiredTemperature = new Temperature(desiredTemperature);
        this.ambientTemperature = new Temperature(ambientTemperature);
    }

    public Temperature getDesiredTemperature(){
        return this.desiredTemperature;
    }

    public Temperature getAmbientTemperature(){
        return this.ambientTemperature;
    }

    public void setAmbientTemperature(double temperature){
        this.ambientTemperature = new Temperature(temperature);
    }

    public Map<String,Object> getExtraProperties(){

        Map<String,Object> extraProperties = new HashMap<>();
        extraProperties.put("desiredTemperature", this.desiredTemperature.getTemperature());
        extraProperties.put("ambientTemperature", this.ambientTemperature.getTemperature());
        return extraProperties;
    }

    @Override
    public CallResult execute(String transition) {
        return this.state.execute(transition, this);
    }

    @Override
    public void factoryReset(){
        this.state = new ThermostatOffState();
        this.isOn = false;
        this.ambientTemperature = new Temperature();
        this.desiredTemperature = new Temperature();
    }

}
