package com.example.smarthome.domain.smartdevices.statemachine.transitions;

/**
 * Represents a state machine transition within a smart device.
 *
 * <p>
 * A transition defines a user- or system-triggered action that can be executed
 * while a device is in a specific state. Each transition maps a human-readable
 * label (used for UI/display purposes) to a strongly-typed action that is
 * interpreted by the state machine.
 * </p>
 *
 * <p>
 * Transitions are state-dependent and are exposed by {@link IState} implementations
 * to indicate what operations are valid in the current state.
 * </p>
 *
 * @param <Action> the type representing the underlying action (typically an enum)
 */
public interface ITransition<Action> {

    /**
     * Returns the underlying action associated with this transition.
     *
     * <p>
     * This value is used by the state machine logic to determine how the
     * device should behave when the transition is executed.
     * </p>
     *
     * @return the action representing this transition
     */
    public Action getAction();

    /**
     * Returns a human-readable label for this transition.
     *
     * <p>
     * This is typically used for UI display or API responses to describe
     * what the transition does in user-friendly terms.
     * </p>
     *
     * @return display label for the transition
     */
    public String getLabel();
}
