package com.example.smarthome.simulation;

import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of a thermostat simulation step.
 *
 * This object captures whether the simulation caused any state changes
 * and collects any logs generated during execution.
 *
 * It is used as a communication object between the simulation logic
 * and the orchestration layer.
 *
 * Responsibilities:
 * - Indicates whether the thermostat state was modified
 * - Aggregates device and transition logs produced during simulation
 *
 * This class does not contain business logic; it only collects results.
 */
public class ThermostatResult {

    private boolean changed = false;
    private List<DeviceLog> logs = new ArrayList<>();

    public void markChanged() {
        this.changed = true;
    }

    public boolean hasChanged() {
        return changed;
    }

    public List<DeviceLog> getLogs() {
        return logs;
    }

    public void addLog(DeviceLog log) {
        logs.add(log);
    }

    public void addTransitionLog(CallResult result) {
        if (result != null && result.getLog() != null) {
            logs.add(result.getLog());
        }
    }
}
