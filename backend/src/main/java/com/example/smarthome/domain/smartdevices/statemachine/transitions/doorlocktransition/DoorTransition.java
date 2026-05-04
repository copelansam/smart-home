package com.example.smarthome.domain.smartdevices.statemachine.transitions.doorlocktransition;

import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;

/**
 * Concrete implementation of a door lock transition in the state machine.
 *
 * <p>
 * This class wraps a {@link DoorLockAction} enum value and exposes it as a
 * {@link ITransition} so that it can be consumed by the state machine and UI layer.
 * </p>
 *
 * <p>
 * It acts as a bridge between strongly typed internal actions (DoorLockAction)
 * and a generic transition interface used by states to expose available actions.
 * </p>
 *
 * <p>
 * The label returned by this transition is intended for UI display purposes,
 * while the underlying action is used internally by the state machine logic.
 * </p>
 */
public class DoorTransition implements ITransition<DoorLockAction> {

    /**
     * The underlying door lock action represented by this transition.
     */
    private DoorLockAction doorLockAction;

    /**
     * Creates a transition wrapper for a specific door lock action.
     *
     * @param action the door lock action this transition represents
     */
    public DoorTransition(DoorLockAction action){
        this.doorLockAction = action;
    }

    /**
     * Returns the underlying action used by the state machine.
     */
    @Override
    public DoorLockAction getAction() {
        return doorLockAction;
    }

    /**
     * Returns a human-readable label for UI display.
     */
    @Override
    public String getLabel(){
        return this.doorLockAction.label;
    }
}
