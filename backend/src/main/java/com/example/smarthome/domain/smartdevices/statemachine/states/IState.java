package com.example.smarthome.domain.smartdevices.statemachine.states;

import java.util.List;

public interface IState {
    public String name;
    List<Transition> availableTransitions;