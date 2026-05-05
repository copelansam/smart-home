package com.example.smarthome.domain.smartdevices.devices;

import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@JsonPropertyOrder({
        "uuid",
        "name",
        "location",
        "deviceType",
        "state",
        "isOn",
        "properties",
        "availableActions",
        "updatableFields"
})
public record DeviceDTO(

        /** Unique identifier of the device. */
        UUID uuid,

        /** Human-readable name of the device. */
        String name,

        /** Location of the device within the smart home. */
        String location,

        /** Type of the device (e.g., LIGHT, FAN, THERMOSTAT). */
        DeviceType deviceType,

        /** Indicates whether the device is currently powered on. */
        boolean isOn,

        /** Current state of the device (state machine name). */
        String state,

        /** List of transitions/actions that can be executed in the current state. */
        List<ITransition<?>> availableTransitions,

        /** List of fields that can be updated in the current state. */
        List<ITransition<?>> updatableFields,

        /**
         * Additional device-specific properties.
         *
         * <p>
         * The structure varies depending on the device type
         * (e.g., brightness for lights, temperatures for thermostats).
         * </p>
         */
        Map<String, Object> properties

) {

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
    public static DeviceDTO fromISmartDevice(ISmartDevice device) {

        return new DeviceDTO(
                device.getUuid(),
                device.getName(),
                device.getLocation(),
                device.getDeviceType(),
                device.getIsOn(),
                device.getState(),
                device.getAvailableTransitions(),
                device.getUpdatableFields(),
                device.getExtraProperties()
        );
    }
}