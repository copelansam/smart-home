package com.example.smarthome.domain.smartdevices.devices;

import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a smart device for API responses.
 *
 * <p>
 * This class is used to expose device data to clients in a structured and
 * controlled format, decoupling internal domain models from external representations.
 * </p>
 *
 * <p>
 * Includes both core device properties and state machine metadata such as
 * available transitions and updatable fields.
 * </p>
 */
@JsonPropertyOrder({"uuid", "name", "location","deviceType", "state", "isOn", "properties", "availableActions", "updatableFields"})
public class DeviceDTO {

    /** Unique identifier of the device. */
    public UUID uuid;

    /** Human-readable name of the device. */
    public String name;

    /** Location of the device within the smart home. */
    public String location;

    /** Type of the device (e.g., LIGHT, FAN, THERMOSTAT). */
    public DeviceType deviceType;

    /** Indicates whether the device is currently powered on. */
    public boolean isOn;

    /** Current state of the device (state machine name). */
    public String state;

    /** List of transitions/actions that can be executed in the current state. */
    public List<ITransition<?>> availableTransitions;

    /** List of fields that can be updated in the current state. */
    public List<ITransition<?>> updatableFields;

    /**
     * Additional device-specific properties.
     *
     * <p>
     * The structure varies depending on the device type
     * (e.g., brightness for lights, temperatures for thermostats).
     * </p>
     */
    public Map<String,Object> properties;

    /**
     * Converts a domain {@link ISmartDevice} into a {@link DeviceDTO}.
     *
     * <p>
     * Extracts all relevant fields required for client consumption,
     * including state machine metadata and device-specific properties.
     * </p>
     *
     * @param device the device to convert
     * @return a populated DTO representing the device
     */
    public static DeviceDTO fromISmartDevice(ISmartDevice device){

        DeviceDTO dto = new DeviceDTO();
        dto.uuid = device.getUuid();
        dto.name = device.getName();
        dto.location = device.getLocation();
        dto.deviceType = device.getDeviceType();
        dto.isOn = device.getIsOn();
        dto.state = device.getState();
        dto.availableTransitions = device.getAvailableTransitions();
        dto.updatableFields = device.getUpdatableFields();

        dto.properties = device.getExtraProperties();

        return dto;
    }

}
