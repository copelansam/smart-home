package com.example.smarthome.domain.smartdevices.devices.smartlight;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.lightstates.LightOffState;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.HashMap;
import java.util.Map;

/**
 * Smart light device implementation.
 *
 * <p>
 * Represents a controllable light within the smart home system. The light supports
 * adjustable brightness and color (RGB), and its behavior is controlled via a state machine.
 * </p>
 *
 * <p>
 * Default configuration:
 * <ul>
 *     <li>State: {@code LightOffState}</li>
 *     <li>Brightness: 100%</li>
 *     <li>Color: White (255, 255, 255)</li>
 *     <li>Power: off</li>
 * </ul>
 * </p>
 *
 * <p>
 * Brightness is constrained to the range [10, 100] to prevent invalid or unusable values.
 * Color is represented using an embedded {@link RGB} value object.
 * </p>
 */
@Entity
@Table(name = "smart_light")
public class SmartLight extends SmartDeviceBase {

    /** Brightness level of the light (10–100%). */
    private int brightnessPercentage;

    /** RGB color configuration of the light. */
    @Embedded
    private RGB color;

    public SmartLight(){}

    /**
     * Creates a light with default settings (white, 100% brightness, off).
     *
     * @param name device name
     * @param location device location
     */
    public SmartLight(String name, String location){
        super(name, location, DeviceType.LIGHT);
        this.brightnessPercentage = 100;
        setColor(new RGB(255,255,255));
        this.state = new LightOffState();
        this.isOn = false;
    }

    /**
     * Creates a light with custom brightness and RGB color.
     *
     * @param name device name
     * @param location device location
     * @param brightnessPercentage brightness (must be 10–100)
     * @param red red component (0–255)
     * @param green green component (0–255)
     * @param blue blue component (0–255)
     * @throws IllegalArgumentException if brightness is out of range or RGB values are invalid
     */
    public SmartLight(String name, String location, int brightnessPercentage, int red, int green, int blue){
        super(name, location, DeviceType.LIGHT);
        setBrightnessPercentage(brightnessPercentage);
        this. color = new RGB(red, green, blue);
        this.state = new LightOffState();
        this.isOn = false;
    }

    /**
     * Sets brightness percentage of the light.
     *
     * @param brightnessPercentage value between 10 and 100 inclusive
     * @throws IllegalArgumentException if value is outside valid range
     */
    public void setBrightnessPercentage(int brightnessPercentage){

        if (brightnessPercentage < 10 || brightnessPercentage > 100){
            throw new IllegalArgumentException("Brightness must be between 10% and 100%");
        }
        else{
            this.brightnessPercentage = brightnessPercentage;
        }
    }

    public int getBrightnessPercentage(){
        return brightnessPercentage;
    }

    public void setColor(RGB color){

        this.color = color;
    }

    public RGB getColor(){
        return color;
    }

    /**
     * Returns device-specific properties for API consumption.
     *
     * <p>
     * Includes:
     * <ul>
     *     <li>RGB color as an integer array [R, G, B]</li>
     *     <li>Brightness percentage</li>
     * </ul>
     * </p>
     *
     * @return map of light properties
     */
    public Map<String, Object> getExtraProperties(){

        Map<String, Object> extraProperties = new HashMap<>();
        extraProperties.put("color", this.color.getColor());
        extraProperties.put("brightnessPercentage", this.brightnessPercentage);
        return extraProperties;
    }

    /**
     * Resets the light to factory defaults.
     *
     * <p>
     * Restores:
     * <ul>
     *     <li>State: off</li>
     *     <li>Brightness: 100%</li>
     *     <li>Color: white</li>
     * </ul>
     * </p>
     */
    @Override
    public void factoryReset(){
        this.isOn = false;
        this.state = new LightOffState();
        setColor(new RGB(255,255,255));
        this.brightnessPercentage = 100;
    }
}
