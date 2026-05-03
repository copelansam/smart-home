package com.example.smarthome.simulation;

import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.devices.DeviceDTO;
import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.ThermostatMode;
import com.example.smarthome.domain.smartdevices.statemachine.states.IState;
import com.example.smarthome.domain.smartdevices.statemachine.states.thermostatstates.ThermostatCoolingState;
import com.example.smarthome.domain.smartdevices.statemachine.states.thermostatstates.ThermostatHeatingState;
import com.example.smarthome.domain.smartdevices.statemachine.states.thermostatstates.ThermostatIdleState;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import com.example.smarthome.repository.DeviceLogRepository;
import com.example.smarthome.service.SmartDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Automates thermostat behavior in the smart home simulation.
 *
 * Periodically evaluates all thermostat devices and adjusts their state
 * and ambient temperature based on desired temperature settings.
 *
 * Handles:
 * - Heating and cooling state transitions
 * - Temperature adjustments toward target values
 * - Logging of device activity
 * - Broadcasting updates to connected WebSocket clients
 */
@Component
public class ThermostatAutomationTask {

    private final SmartDeviceService deviceService;
    private final DeviceLogRepository logRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    ThermostatAutomationTask(SmartDeviceService deviceService, DeviceLogRepository logRepository){

        this.deviceService = deviceService;
        this.logRepository = logRepository;
    }

    /**
     * Executes one simulation cycle for all thermostat devices.
     *
     * Evaluates current and desired temperatures, updates thermostat state
     * (heating, cooling, idle), persists logs, and broadcasts updates
     * to connected clients if changes occur.
     */
    public void simulateTemperatureChange(){

        List<ISmartDevice> devices = deviceService.getDevices(DeviceType.THERMOSTAT,null,null);

        List<SmartThermostat> thermostats = devices.stream().map(device -> (SmartThermostat) device).toList();

        for (SmartThermostat thermostat : thermostats){

            if (thermostat.getState().contains("Off")){
                continue;
            }

            double ambientTemperature = thermostat.getAmbientTemperature().getTemperature();
            double desiredTemperature = thermostat.getDesiredTemperature().getTemperature();
            double difference = ambientTemperature - desiredTemperature;
            boolean hasChanged = false;
            ThermostatMode mode = thermostat.getMode();

            System.out.println("Mode: " + thermostat.getMode());
            System.out.println("State: " + thermostat.getState());

            // If it is hotter than what the user wants and the thermostat is either in auto or cooling mode, start cooling
            if (difference >= 1){
                if (mode == ThermostatMode.AUTO || mode == ThermostatMode.COOL){
                    if (!thermostat.getState().contains("Cooling")) {
                        CallResult result = thermostat.execute("START_COOLING", null);

                        if (result != null && result.getLog() != null) {
                            logRepository.save(result.getLog());
                        }
                    }
                    thermostat.getAmbientTemperature().updateTemperature(-1);
                    hasChanged = true;
                    logRepository.save(new DeviceLog(
                            thermostat.getUuid(),
                            "Temperature Update",
                            "The temperature in " + thermostat.getLocation() + " has been adjusted to "
                                    + (ambientTemperature - 1)));
                }
            }
            else if (difference <= -1){ // If it is colder that the user wants and the thermostat is in auto or heating mode, start heating
                if (thermostat.getMode() == ThermostatMode.AUTO || thermostat.getMode() == ThermostatMode.HEAT){
                    if (!thermostat.getState().contains("Heating")) {
                        CallResult result = thermostat.execute("START_HEATING", null);

                        if (result != null && result.getLog() != null) {
                            logRepository.save(result.getLog());
                        }
                    }
                    thermostat.getAmbientTemperature().updateTemperature(1);
                    hasChanged = true;

                    logRepository.save(new DeviceLog(
                            thermostat.getUuid(),
                            "Temperature Update",
                            "The temperature in " + thermostat.getLocation() + " has been adjusted to "
                                    + (ambientTemperature + 1)));
                }
            }
            else if (ambientTemperature <= desiredTemperature   // Checks if the cooling thermostat overshot it and
                    && thermostat.getState().contains("Cooling")){ // goes to idle. Possible if the user enters a decimal for temperatures
                CallResult result = thermostat.execute("STOP_COOLING", null);
                hasChanged = true;

                if (result != null && result.getLog() != null) {
                    logRepository.save(result.getLog());
                }

            }
            else if (ambientTemperature >= desiredTemperature  // Checks if the heating thermostat overshot it and goes
                    && thermostat.getState().contains("Heating")){ // to idle. Possible if the user enters a decimal for temperatures
                CallResult result = thermostat.execute("STOP_HEATING", null);
                hasChanged = true;

                if (result != null && result.getLog() != null) {
                    logRepository.save(result.getLog());
                }
            }

            if (hasChanged) {

                deviceService.saveDeviceUpdate(thermostat);

                ISmartDevice updatedDevice = deviceService.getDeviceById(thermostat.getUuid());

                DeviceDTO dto = DeviceDTO.fromISmartDevice(updatedDevice);

                messagingTemplate.convertAndSend("/topic/devices", dto);
            }
        }
    }
}
