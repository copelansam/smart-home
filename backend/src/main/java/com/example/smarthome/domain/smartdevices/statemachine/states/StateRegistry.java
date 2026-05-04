package com.example.smarthome.domain.smartdevices.statemachine.states;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Central registry for all state machine states.
 *
 * <p>
 * This class acts as a factory and lookup mechanism for {@link IState} implementations.
 * It maps state names to suppliers that can construct new instances of those states.
 * </p>
 *
 * <p>
 * It is primarily used during deserialization of smart devices, where a stored
 * state name must be converted back into a concrete state object.
 * </p>
 *
 * <p>
 * All states must be registered in this registry before they can be created.
 * </p>
 */
public class StateRegistry {

    private static final Map<String, Supplier<IState>> registry = new HashMap<>();

    /**
     * Registers a state in the registry.
     *
     * @param name unique name of the state
     * @param supplier factory method used to create instances of the state
     */
    public static void register(String name, Supplier<IState> supplier){
        registry.put(name, supplier);
    }

    /**
     * Creates a new instance of a registered state.
     *
     * <p>
     * This method is used to reconstruct state objects from their persisted string names.
     * </p>
     *
     * @param name the name of the state to create
     * @return a new instance of the requested state
     * @throws IllegalArgumentException if no state is registered under the given name
     */
    public static IState create(String name){

        // Retrieves a state class supplier based on the specified state name.
        Supplier<IState> supplier = registry.get(name);

        // If no supplier was found, throw an exception
        if (supplier == null){
            throw new IllegalArgumentException("Unknown State");
        }
        // Otherwise, return the supplier
        return supplier.get();
    }
}
