package com.example.smarthome.domain.smartdevices.statemachine.states;

public interface IState {
    public String name;
    public ReadOnlyList<T> availableTransitions;