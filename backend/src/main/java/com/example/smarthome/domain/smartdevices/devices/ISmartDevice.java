package com.example.smarthome.domain.smartdevices.devices;

import com.example.smarthome.domain.smartdevices.statemachine.states.IState;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a generic smart device within the system.
 *
 * Defines common behavior for all smart devices, including state management,
 * device metadata, available actions, and execution of state transitions.
 *
 * Implementations encapsulate device-specific logic while adhering to a shared
 * contract used by services, controllers, and the state machine framework.
 */
public interface ISmartDevice {

    /**
     * Updates the current state of the device.
     *
     * @param newState the new state to assign to the device
     */
    void setState(IState newState);

    /**
     * @return whether the device is currently powered on
     */
    boolean getIsOn();

    /**
     * @return the location of the device
     */
    String getLocation();

    /**
     * @return the type of the device
     */

    DeviceType getDeviceType();

    /**
     * @return the display name of the device
     */
    String getName();

    /**
     * Returns additional device-specific properties.
     *
     * Used for exposing attributes that vary between device types.
     *
     * @return a map of extra properties
     */
    Map<String, Object> getExtraProperties();

    /**
     * Returns the current state name of the device.
     *
     * @return the name of the current state
     */
    String getState();

    /**
     * Returns the list of transitions currently available for this device.
     *
     * @return list of valid state transitions
     */
    List<ITransition<?>> getAvailableTransitions();

    /**
     * Returns transitions that represent updatable fields.
     *
     * @return list of update-related transitions
     */
    List<ITransition<?>> getUpdatableFields();

    /**
     * @return unique identifier of the device
     */
    UUID getUuid();

    /**
     * Executes a transition on the device.
     *
     * @param transition the name of the transition to execute
     * @param parameters optional parameters required for the transition
     * @return result of the transition execution
     */
    CallResult execute(String transition, Map<String, Object> parameters);

    /**
     * Sets the power state of the device.
     *
     * @param isOn true to turn the device on, false to turn it off
     */
    void setIsOn(boolean isOn);

    /**
     * Resets the device to its default factory settings.
     */
    void factoryReset();
}
