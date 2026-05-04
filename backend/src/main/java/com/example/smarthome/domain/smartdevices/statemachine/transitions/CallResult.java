package com.example.smarthome.domain.smartdevices.statemachine.transitions;

import com.example.smarthome.domain.history.DeviceLog;

/**
 * Represents the result of executing a state machine transition.
 *
 * <p>
 * A CallResult is returned after a device attempts to execute a transition
 * within its current state. It encapsulates:
 * <ul>
 *     <li>Whether the transition succeeded</li>
 *     <li>A human-readable message describing the outcome</li>
 *     <li>An optional {@link DeviceLog} entry for persistence/auditing</li>
 * </ul>
 * </p>
 *
 * <p>
 * This class is used as the standard response type for all state execution
 * operations in the smart device state machine.
 * </p>
 */
public class CallResult {

    private String message;
    private boolean success;
    private DeviceLog log;

    /**
     * Creates a failed CallResult representing an invalid or unsupported transition.
     *
     * <p>
     * This constructor is typically used when a transition is not recognized
     * or not allowed in the current state.
     * </p>
     */
    public CallResult(){
        this.message = "Invalid Transition from current state";
        this.success = false;
        this.log = null;
    }

    /**
     * Creates a CallResult with explicit outcome details.
     *
     * @param message human-readable description of the result
     * @param success whether the transition succeeded
     * @param log optional device log entry associated with this transition
     */
    public CallResult(String message, boolean success, DeviceLog log) {
        this.message = message;
        this.success = success;
        this.log = log;
    }

    public DeviceLog getLog(){
        return this.log;
    }

    public boolean getIsSuccess(){
        return this.success;
    }

    public String getMessage(){
        return this.message;
    }
}
