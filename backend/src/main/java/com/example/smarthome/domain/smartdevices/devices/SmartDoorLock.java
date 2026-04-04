package com.example.smarthome.domain.smartdevices.devices;

import com.example.smarthome.domain.smartdevices.statemachine.states.IState;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "smart_door_lock")
public class SmartDoorLock extends SmartDeviceBase {

    public SmartDoorLock(){}

    public SmartDoorLock(String name, String location, DeviceType deviceType){
        super(name, location, deviceType);
    }


}
