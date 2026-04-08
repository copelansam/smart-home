package com.example.smarthome.domain.smartdevices.devices.smartdoorlock;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.doorlockstates.DoorLockedState;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.doorlocktransition.DoorLockAction;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.HashMap;
import java.util.Map;

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

    // Door Lock has no extra properties at the moment, could receive some in the future(?)
    public Map<String, Object> getExtraProperties(){

        HashMap<String, Object> extraProperties = new HashMap<>();
        return extraProperties;
    }

    public void execute(ITransition<?> transition){
        state.execute(transition, this);
    }
}
