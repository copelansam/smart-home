package com.example.smarthome.domain.history;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class DeviceLog {

    public DeviceLog(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID deviceId;
    private String event;
    private String message;
    private LocalDateTime timestamp;

    public DeviceLog(UUID deviceId, String event, String message){

        this.deviceId = deviceId;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public DeviceLog(UUID deviceId, String message){
        this.deviceId = deviceId;
        this.message = message;
    }

    public Long getId(){
        return  this.id;
    }

    public UUID getDeviceId(){
        return this.deviceId;
    }

    public String getMessage(){
        return this.message;
    }
    public LocalDateTime getTimestamp(){
        return this.timestamp;
    }
    public String getEvent(){
        return  this.event;
    }
}
