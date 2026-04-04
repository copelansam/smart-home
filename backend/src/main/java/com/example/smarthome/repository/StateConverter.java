package com.example.smarthome.repository;

import com.example.smarthome.domain.smartdevices.statemachine.states.IState;
import com.example.smarthome.domain.smartdevices.statemachine.states.StateRegistry;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// This class will be responsible for converting IStates into Strings that the database can store.
// The database cannot store interfaces, so I need to convert them
@Converter(autoApply = true)
public class StateConverter implements AttributeConverter<IState, String> {

    // Convert interface state into something the database can store (String in this instance)
    @Override
    public String convertToDatabaseColumn(IState state){
        if (state == null){
            return null;
        }
        return state.getName();
    }

    @Override
    public IState convertToEntityAttribute(String stateName){
        return stateName == null? null : StateRegistry.create(stateName);
    }
}
