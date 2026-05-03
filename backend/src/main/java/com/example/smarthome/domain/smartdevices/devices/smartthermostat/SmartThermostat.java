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

    @Enumerated(EnumType.STRING)
    private ThermostatMode mode;

    public SmartThermostat(){}

    // Constructor with no temperatures specified, temperatures will be set to a random value between 60 and 80
    public SmartThermostat(String name, String location){
        super(name, location, DeviceType.THERMOSTAT);
        this.desiredTemperature = new Temperature();
        this.ambientTemperature = new Temperature();
        this.state = new ThermostatOffState();
        this.mode = ThermostatMode.AUTO;
    }

    public SmartThermostat(String name, String location, double desiredTemperature, double ambientTemperature){
        super(name, location, DeviceType.THERMOSTAT);
        setDesiredTemperature(desiredTemperature);
        this.ambientTemperature = new Temperature();
        this.state = new ThermostatOffState();
        this.desiredTemperature = new Temperature(desiredTemperature);
        this.ambientTemperature = new Temperature(ambientTemperature);
        this.mode = ThermostatMode.AUTO;
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

    public void setDesiredTemperature(double desiredTemperature){
        if (desiredTemperature < 60 || desiredTemperature > 80){
            throw new IllegalArgumentException("Desired temperature must be between 60 and 80 degrees fahrenheit (inclusive)");
        }else{
            this.desiredTemperature = new Temperature(desiredTemperature);
        }
    }

    public ThermostatMode getMode(){
        return this.mode;
    }

    public void setMode(ThermostatMode mode){
        this.mode = mode;
    }

    public Map<String,Object> getExtraProperties(){

        Map<String,Object> extraProperties = new HashMap<>();
        extraProperties.put("desiredTemperature", this.desiredTemperature.getTemperature());
        extraProperties.put("ambientTemperature", this.ambientTemperature.getTemperature());
        extraProperties.put("mode", this.mode);
        return extraProperties;
    }

    @Override
    public void factoryReset(){
        this.state = new ThermostatOffState();
        this.isOn = false;
        this.ambientTemperature = new Temperature(70);
        this.desiredTemperature = new Temperature(75);
        this.mode = ThermostatMode.AUTO;
    }
}
