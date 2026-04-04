package com.example.smarthome.domain.smartdevices.devices.smartfan;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import com.example.smarthome.domain.smartdevices.statemachine.states.IState;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;


@Entity
@Table(name = "smart_fan")
public class SmartFan extends SmartDeviceBase {

    private IState state;

    public SmartFan(String name, String location, DeviceType deviceType){
        super(name, location, deviceType);
    }

    public void setState(IState newState){
        this.state = state.execute().getNewState();
    }
}
