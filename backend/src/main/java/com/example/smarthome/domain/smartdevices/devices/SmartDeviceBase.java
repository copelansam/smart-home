package com.example.smarthome.domain.smartdevices.devices;


import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class SmartDeviceBase implements ISmartDevice{

    @Id
    @GeneratedValue
    private UUID uuid;

    private String name;
    private String location;

    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    public SmartDeviceBase(){}

    public SmartDeviceBase(String name, String location, DeviceType deviceType){
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.location = location;
        this.deviceType = deviceType;
    }

    public UUID getUuid(){return this.uuid;}
    public String getName(){
        return this.name;
    }
    public String getLocation(){
        return this.location;
    }
    public DeviceType getDeviceType(){
        return this.deviceType;
    }
}
