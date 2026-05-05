package com.example.smarthome.domain.smartdevices.devices.smartfan;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.fanstates.FanOffState;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Smart fan device implementation.
 *
 * <p>
 * Represents a fan within the smart home system. Its behavior is governed
 * by a state machine, with the default state being {@code FanOffState}.
 * </p>
 *
 * <p>
 * In addition to standard device properties, the fan maintains a speed setting,
 * which is persisted and exposed to clients as part of its extra properties.
 * </p>
 */
@Entity
@Table(name = "smart_fan")
public class SmartFan extends SmartDeviceBase {

    /**
     * Current fan speed.
     *
     * Persisted as a string representation in the database.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FanSpeed speed;

    public SmartFan(){}

    /**
     * Constructs a smart fan with default settings.
     *
     * <ul>
     *     <li>Initial state: {@code FanOffState}</li>
     *     <li>Default speed: MEDIUM</li>
     * </ul>
     *
     * @param name the device name
     * @param location the device location
     */
    public SmartFan(String name, String location){
        super(name, location, DeviceType.FAN);
        factoryReset();
    }

    /**
     * Updates the fan speed.
     *
     * @param speed the new speed value
     */
    public void setSpeed(FanSpeed speed){
        this.speed = speed;
    }

    /**
     * @return the current fan speed
     */
    public FanSpeed getSpeed(){
        return this.speed;
    }

    /**
     * Returns device-specific properties.
     *
     * <p>
     * Includes the current fan speed as a human-readable value.
     * </p>
     *
     * @return a map containing the fan's speed
     */
    public Map<String, Object> getExtraProperties(){

        Map<String, Object> extraProperties = new HashMap<>();
        extraProperties.put("speed", this.speed.getDescription());
        return extraProperties;
    }

    /**
     * Resets the fan to its default configuration.
     *
     * <ul>
     *     <li>State: {@code FanOffState}</li>
     *     <li>Power: off</li>
     *     <li>Speed: LOW</li>
     * </ul>
     */
    @Override
    public void factoryReset(){
        this.state = new FanOffState();
        this.isOn = false;
        this.speed = FanSpeed.MEDIUM;
    }
}
