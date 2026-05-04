package com.example.smarthome.domain.smartdevices.statemachine.states;

import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;

import java.util.Collections;
import java.util.List;

/**
 * Base implementation of a state in the smart device state machine.
 *
 * <p>
 * Provides common functionality for all concrete states, including:
 * <ul>
 *     <li>State identity (name)</li>
 *     <li>Available transitions from the state</li>
 *     <li>Transitions representing updatable device fields</li>
 * </ul>
 * </p>
 *
 * <p>
 * Each state defines which actions a device can perform while in that state,
 * as well as which device properties are allowed to be modified.
 * These lists are immutable to ensure state integrity at runtime.
 * </p>
 *
 * <p>
 * Concrete states are expected to extend this class and define their
 * own transitions and behavior rules.
 * </p>
 *
 * @param <D> the type of smart device associated with this state
 */
public abstract class StateBase<D extends ISmartDevice> implements IState<D> {

    private static final long serialVersionUID = 1L;

    /** Unique name identifying this state. */
    public final String name;

    /** Transitions that are allowed while the device is in this state. */
    protected final List<ITransition<?>> availableTransitions;


    /**
     * Transitions that represent fields which can be modified while in this state.
     * Used for state-driven UI and validation.
     */
    protected final List<ITransition<?>> transitionsRepresentingUpdatableFields;

    /**
     * Constructs a base state with its allowed transitions and updatable fields.
     *
     * @param name name of the state
     * @param availableTransitions list of valid transitions from this state
     * @param transitionsRepresentingUpdatableFields list of transitions representing editable fields
     */
    public StateBase(String name, List<ITransition<?>> availableTransitions, List<ITransition<?>> transitionsRepresentingUpdatableFields){
        this.name = name;
        this.availableTransitions = availableTransitions;
        this.transitionsRepresentingUpdatableFields = transitionsRepresentingUpdatableFields;
    }
    /**
     * Returns the name of this state.
     *
     * @return state identifier
     */
    public String getName(){
        return this.name;
    }

    /**
     * Returns the list of transitions available in this state.
     *
     * <p>
     * The returned list is unmodifiable to preserve state integrity.
     * </p>
     *
     * @return immutable list of available transitions
     */
    @Override
    public List<ITransition<?>> provideAvailableTransitions() {
        return Collections.unmodifiableList(availableTransitions);
    }

    /**
     * Returns the transitions that represent updatable fields in this state.
     *
     * <p>
     * These define which device properties can be modified while in this state.
     * </p>
     *
     * @return immutable list of updatable field transitions
     */
    @Override
    public List<ITransition<?>> provideUpdatableFields(){
        return Collections.unmodifiableList(transitionsRepresentingUpdatableFields);
    }
}
