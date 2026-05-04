package com.example.smarthome.domain.smartdevices.statemachine.states;

import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;

import java.util.List;
import java.util.Map;

/**
 * Represents a state in the smart device state machine.
 *
 * <p>
 * Each state defines:
 * <ul>
 *     <li>How a device reacts to transitions</li>
 *     <li>Which transitions are available from that state</li>
 *     <li>Which device fields are allowed to be updated in that state</li>
 * </ul>
 * </p>
 *
 * <p>
 * States are responsible for enforcing valid behavior rules for a device at runtime.
 * The same device may expose different actions and editable properties depending
 * on its current state.
 * </p>
 *
 * @param <D> the type of smart device associated with this state
 */
public interface IState <D extends ISmartDevice>{

    /**
     * Executes a transition for the given device within this state.
     *
     * <p>
     * The state is responsible for validating whether the transition is allowed
     * and applying any changes to the device. The result may include logs or failure reasons.
     * </p>
     *
     * @param transition the name of the transition to execute
     * @param device the device currently in this state
     * @param parameters optional parameters required for the transition
     * @return result of the transition execution, including success/failure status
     */
    CallResult execute(String transition, D device, Map<String, Object> parameters);

    /**
     * Returns all transitions that are currently valid in this state.
     *
     * <p>
     * These transitions define the actions a device can perform while in this state.
     * </p>
     *
     * @return list of available transitions for the current state
     */
    List<ITransition<?>> provideAvailableTransitions();

    /**
     * Returns the list of device fields that are allowed to be updated in this state.
     *
     * <p>
     * Each state defines which properties of a device can be modified while in that state.
     * This enables state-driven validation of configuration changes.
     * </p>
     *
     * @return list of transitions representing updatable fields
     */
    List<ITransition<?>> provideUpdatableFields();

    /**
     * Returns the unique name of this state.
     *
     * @return state identifier used for persistence and reconstruction
     */
    String getName();
}