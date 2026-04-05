package com.example.smarthome.domain.smartdevices.devices.smartdoorlock;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.doorlockstates.DoorLockedState;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "smart_door_lock")
public class SmartDoorLock extends SmartDeviceBase {

    public SmartDoorLock(){}

    public SmartDoorLock(String name, String location, DeviceType deviceType){
        super(name, location, deviceType);
        this.state = new DoorLockedState();

        // Invariant: For UI filtering purposes, all door lock objects are considered on
        this.isOn = true;
    }
}
