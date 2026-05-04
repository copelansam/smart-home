package com.example.smarthome.domain.smartdevices.statemachine.transitions.fantransition;

import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;

/**
 * Concrete implementation of a fan transition in the state machine.
 *
 * <p>
 * This class wraps a {@link FanAction} enum value and exposes it as a
 * {@link ITransition} so that it can be consumed by the state machine and UI layer.
 * </p>
 *
 * <p>
 * It acts as a bridge between strongly typed internal actions (FanAction)
 * and a generic transition interface used by states to expose available actions.
 * </p>
 *
 * <p>
 * The label returned by this transition is intended for UI display purposes,
 * while the underlying action is used internally by the state machine logic.
 * </p>
 */
public class FanTransition implements ITransition<FanAction> {

    /**
     * The underlying fan action represented by this transition.
     */
    private FanAction fanAction;

    /**
     * Creates a transition wrapper for a specific fan action.
     *
     * @param action the fan action this transition represents
     */
    public FanTransition(FanAction action){
        this.fanAction = action;
    }

    /**
     * Returns the underlying action used by the state machine.
     */
    @Override
    public FanAction getAction() {
        return fanAction;
    }

    /**
     * Returns a human-readable label for UI display.
     */
    @Override
    public String getLabel(){
        return this.fanAction.label;
    }
}
