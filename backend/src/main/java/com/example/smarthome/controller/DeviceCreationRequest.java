package com.example.smarthome.controller;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * Request object used to create a new smart device.
 *
 * <p>
 * This DTO represents validated input from the client layer and is used
 * by the controller to create new devices in the system.
 * </p>
 *
 * <p>
 * Validation is enforced using Bean Validation annotations, ensuring that
 * malformed or incomplete HTTP requests are rejected with a 400 Bad Request
 * before reaching the service layer.
 * </p>
 */
public record DeviceCreationRequest(

        /**
         * Name of the device.
         */
        @NotBlank(message = "Device name must not be blank")
        String name,

        /**
         * Location where the device is installed (e.g. "Kitchen", "Living Room").
         */
        @NotBlank(message = "Location must not be blank")
        String location,

        /**
         * Type of device being created (LIGHT, FAN, DOORLOCK, THERMOSTAT).
         */
        @NotNull(message = "Device type is required")
        DeviceType deviceType

) {}