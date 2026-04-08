package com.example.smarthome.domain.smartdevices.statemachine.states;

import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.TransitionResult;

import java.io.Serializable;
import java.util.List;

public abstract class StateBase<T extends ITransition<?>, D extends ISmartDevice> implements IState<T,D> {

    private static final long serialVersionUID = 1L;

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
    public List<ITransition<?>> provideAvailableTransitions() {
        return List.copyOf(availableTransitions);
    }
}
