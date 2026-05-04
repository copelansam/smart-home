package com.example.smarthome.domain.smartdevices.devices.smartdoorlock;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.doorlockstates.DoorUnlockedState;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.HashMap;
import java.util.Map;

/**
 * Smart door lock device implementation.
 *
 * <p>
 * Represents a door lock within the smart home system. Its behavior is driven
 * by a state machine, with the default state being {@code DoorUnlockedState}.
 * </p>
 *
 * <p>
 * Invariant: Door locks are always considered "on" for UI and filtering purposes.
 * </p>
 *
 * <p>
 * Currently, this device does not expose additional properties beyond the base
 * device attributes, but the structure allows for future extensibility.
 * </p>
 */
@Entity
@Table(name = "smart_door_lock")
public class SmartDoorLock extends SmartDeviceBase {

    public SmartDoorLock(){}

    /**
     * Constructs a new smart door lock with a default unlocked state.
     *
     * @param name the name of the device
     * @param location the location of the device
     */
    public SmartDoorLock(String name, String location){
        super(name, location, DeviceType.DOORLOCK);
        this.state = new DoorUnlockedState();

        // Invariant: For UI filtering purposes, all door lock objects are considered as "on"
        this.isOn = true;
    }

    /**
     * Returns device-specific properties.
     *
     * <p>
     * Door locks currently do not have additional properties,
     * so this returns an empty map.
     * </p>
     *
     * @return an empty map of properties
     */
    public Map<String, Object> getExtraProperties(){

        HashMap<String, Object> extraProperties = new HashMap<>();
        return extraProperties;
    }

    /**
     * Resets the door lock to its default state.
     *
     * <p>
     * Factory reset restores the device to the unlocked state.
     * </p>
     */
    public void factoryReset(){
        this.state = new DoorUnlockedState();
    }
}
