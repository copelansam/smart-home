package com.example.smarthome.domain.smartdevices.statemachine.states;

import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;

import java.util.List;

public abstract class StateBase implements IState {

    public final String name;
    protected final List<ITransition<?>> availableTransitions;

    public StateBase(String name, List<ITransition<?>> availableTransitions){
        this.name = name;
        this.availableTransitions = availableTransitions;
    }

    public String getName(){
        return this.name;
    }

    @Override
    public abstract TransitionResult execute();

    @Override
    public List<ITransition<?>> provideAvailableTransitions() {
        return List.copyOf(availableTransitions);
    }
}
