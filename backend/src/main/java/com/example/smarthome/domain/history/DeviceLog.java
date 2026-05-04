package com.example.smarthome.domain.history;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a log entry for a smart device.
 *
 * Stores historical records of device-related events such as state changes,
 * updates, and lifecycle actions. Each log is associated with a specific device
 * and includes a timestamp indicating when the event occurred.
 */
@Entity
public class DeviceLog {

    public DeviceLog(){}

    /** Unique identifier for the log entry (auto-generated). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ID of the device associated with this log entry. */
    private UUID deviceId;

    /** High-level category of the event (e.g., "Device Creation"). */
    private String event;

    /** Detailed description of what happened */
    private String message;

    /** Timestamp indicating when the event occurred. */
    private LocalDateTime timestamp;

    /**
     * Creates a new device log entry.
     *
     * @param deviceId the unique identifier of the device associated with this log
     * @param event a short description of the type of event (e.g., "Device Creation", "Temperature Update")
     * @param message a detailed message describing the event
     */
    public DeviceLog(UUID deviceId, String event, String message){

        this.deviceId = deviceId;
        this.event = event;
        this.message = message;
        this.timestamp = LocalDateTime.now();
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
