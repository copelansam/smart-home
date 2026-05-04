package com.example.smarthome.repository;

import com.example.smarthome.domain.smartdevices.statemachine.states.IState;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/***
 * JPA attribute converter for persisting state machine states
 *
 * Converts {@link IState} objects to their String representation for database storage, and reconstructs state instances
 * from stored state names when loading from the database
 *
 * This is required because JPA cannot persist interface types directly
 *
 */
@Converter(autoApply = true)
public class StateConverter implements AttributeConverter<IState, String> {

    /***
     * Converts a state into is String representation so that it can be stored in the database
     *
     * @param state The state to convert
     * @return The state's corresponding String that will be stored
     * @throws IllegalStateException if a device does not have a state persisted.
     * All device's should have their state persisted
     */
    @Override
    public String convertToDatabaseColumn(IState state){
        // Null check the state field. State should not be null
        if (state == null){
            throw new IllegalStateException("Device with out a state");
        }
        // Retrieves the String field name from the state which will be stored in the database
        return state.getName();
    }

    /***
     * Converts a string representing a state into its corresponding state class
     *
     * @param stateName A String that represents a state
     * @return corresponding {@link IState} instance
     */
    @Override
    public IState convertToEntityAttribute(String stateName){

        // If the
        return stateName == null? null : StateRegistry.create(stateName);
    }
}
