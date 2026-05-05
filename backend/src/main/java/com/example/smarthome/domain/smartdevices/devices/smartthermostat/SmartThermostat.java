package com.example.smarthome.domain.smartdevices.devices.smartthermostat;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.thermostatstates.ThermostatOffState;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Smart thermostat device implementation.
 *
 * <p>
 * Represents a thermostat in the smart home system that controls and tracks
 * both ambient and desired temperature. The device behavior is driven by a
 * state machine and can operate in different modes such as AUTO, HEAT, and COOL.
 * </p>
 *
 * <p>
 * This entity persists both ambient and desired temperatures using embedded
 * {@link Temperature} value objects, and maps them to dedicated database columns.
 * </p>
 *
 * <p>
 * The thermostat supports automatic temperature adjustment logic and exposes
 * additional properties such as current mode and environmental readings for UI
 * and simulation purposes.
 * </p>
 */
@Entity
@Table(name = "smart_thermostat")
public class SmartThermostat extends SmartDeviceBase {

    /** Desired target temperature set by the user (°F). */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "temperature", column = @Column(name = "desired_temperature"))
    })
    private Temperature desiredTemperature;

    /** Current ambient temperature of the environment (°F). */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "temperature", column = @Column(name = "ambient_temperature"))
    })
    private Temperature ambientTemperature;

    /** Current operating mode of the thermostat (AUTO, HEAT, COOL). */
    @Enumerated(EnumType.STRING)
    private ThermostatMode mode;

    public SmartThermostat(){}

    /**
     * Creates a thermostat with default values.
     * Ambient and desired temperatures are initialized to default values,
     * and the device starts in OFF state with AUTO mode enabled.
     */
    public SmartThermostat(String name, String location){
        super(name, location, DeviceType.THERMOSTAT);
        this.desiredTemperature = new Temperature();
        this.ambientTemperature = new Temperature();
        this.state = new ThermostatOffState();
        this.mode = ThermostatMode.AUTO;
    }

    /**
     * Creates a thermostat with specified temperature settings.
     *
     * @param desiredTemperature target temperature (°F, typically 60–80 range)
     * @param ambientTemperature initial ambient temperature (°F)
     */
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

    /**
     * Updates the ambient temperature reading.
     *
     * @param temperature new ambient temperature (°F)
     */
    public void setAmbientTemperature(double temperature){
        this.ambientTemperature = new Temperature(temperature);
    }

    /**
     * Sets the user-defined target temperature.
     *
     * <p>
     * Enforces system constraint: thermostat range is limited to 60–80°F.
     * Values outside this range are rejected.
     * </p>
     *
     * @param desiredTemperature target temperature (°F)
     * @throws IllegalArgumentException if temperature is outside allowed range
     */
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

    /**
     * Updates the operating mode of the thermostat.
     *
     * @param mode new thermostat mode (AUTO, HEAT, COOL)
     */
    public void setMode(ThermostatMode mode){
        this.mode = mode;
    }

    /**
     * Returns additional runtime properties used for UI display and simulation.
     *
     * <p>
     * Includes:
     * - desired temperature
     * - ambient temperature
     * - current operating mode
     * </p>
     *
     * @return map of property names to values
     */
    public Map<String,Object> getExtraProperties(){

        Map<String,Object> extraProperties = new HashMap<>();
        extraProperties.put("desiredTemperature", this.desiredTemperature.temperature());
        extraProperties.put("ambientTemperature", this.ambientTemperature.temperature());
        extraProperties.put("mode", this.mode);
        return extraProperties;
    }

    /**
     * Adjusts the current temperature by a delta value.
     *
     * <p>
     * Positive values increase temperature, negative values decrease it.
     * This method is primarily used for simulation and thermostat automation.
     * </p>
     *
     * @param amount change in temperature (can be positive or negative)
     */
    public void updateTemperature(int amount){

        // Calculate the new temperature by retrieving the current and adding the delta
        double newTemp = this.ambientTemperature.temperature() + amount;

        // Create a new temperature object with the new temperature value
        this.ambientTemperature = new Temperature(newTemp);
    }

    /**
     * Resets the thermostat to factory default settings.
     *
     * <p>
     * Restores:
     * - OFF state
     * - AUTO mode
     * - default ambient temperature (70°F)
     * - default desired temperature (75°F)
     * </p>
     *
     * <p>
     * This method is typically used by system-wide reset operations.
     * </p>
     */
    @Override
    public void factoryReset(){
        this.state = new ThermostatOffState();
        this.isOn = false;
        this.ambientTemperature = new Temperature(70);
        this.desiredTemperature = new Temperature(75);
        this.mode = ThermostatMode.AUTO;
    }
}
