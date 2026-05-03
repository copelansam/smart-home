package com.example.smarthome.domain.smartdevices.statemachine.states;

import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;

import java.util.Collections;
import java.util.List;

public abstract class StateBase<D extends ISmartDevice> implements IState<D> {

    private static final long serialVersionUID = 1L;

    public final String name;
    protected final List<ITransition<?>> availableTransitions;
    protected final List<ITransition<?>> transitionsRepresentingUpdatableFields;

    public StateBase(String name, List<ITransition<?>> availableTransitions, List<ITransition<?>> transitionsRepresentingUpdatableFields){
        this.name = name;
        this.availableTransitions = availableTransitions;
        this.transitionsRepresentingUpdatableFields = transitionsRepresentingUpdatableFields;
    }

    public String getName(){
        return this.name;
    }

    @Override
    public List<ITransition<?>> provideAvailableTransitions() {
        return Collections.unmodifiableList(availableTransitions);
    }

    @Override
    public List<ITransition<?>> provideUpdatableFields(){
        return Collections.unmodifiableList(transitionsRepresentingUpdatableFields);
    }
}
