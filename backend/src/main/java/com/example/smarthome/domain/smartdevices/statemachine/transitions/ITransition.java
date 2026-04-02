package com.example.smarthome.domain.smartdevices.statemachine.transitions;

public interface ITransition<Action> {

    public Action getAction();
}
