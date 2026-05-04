package com.example.smarthome.domain.smartdevices.statemachine.transitions.lighttransition;

import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;

/**
 * Concrete implementation of a light transition in the state machine.
 *
 * <p>
 * This class wraps a {@link LightAction} enum value and exposes it as a
 * {@link ITransition} so that it can be consumed by the state machine and UI layer.
 * </p>
 *
 * <p>
 * It acts as a bridge between strongly typed internal actions (LightAction)
 * and a generic transition interface used by states to expose available actions.
 * </p>
 *
 * <p>
 * The label returned by this transition is intended for UI display purposes,
 * while the underlying action is used internally by the state machine logic.
 * </p>
 */
public class LightTransition implements ITransition<LightAction> {

    /**
     * The underlying light action represented by this transition.
     */
    private final LightAction lightAction;

    /**
     * Creates a transition wrapper for a specific light action.
     *
     * @param action the light action this transition represents
     */
    public LightTransition(LightAction action){
        this.lightAction = action;
    }

    /**
     * Returns the underlying action used by the state machine.
     */
    @Override
    public LightAction getAction() {
        return lightAction;
    }

    /**
     * Returns a human-readable label for UI display.
     */
    @Override
    public String getLabel(){
        return this.lightAction.label;
    }
}
