package com.example.smarthome.simulation;

import com.example.smarthome.domain.smartdevices.devices.DeviceDTO;
import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.repository.DeviceLogRepository;
import com.example.smarthome.service.SmartDeviceService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Orchestrates thermostat simulation cycles in the smart home system.
 *
 * This task coordinates the execution of thermostat simulation logic
 * and applies resulting state changes.
 *
 * For each simulation cycle it:
 *
 * - Retrieves all thermostat devices
 * - Delegates behavior evaluation to ThermostatSimulationService
 * - Persists updated device state when changes occur
 * - Stores generated device logs
 * - Broadcasts updated device state to connected WebSocket clients
 *
 * This class does not contain business logic itself; it only
 * coordinates simulation and side-effect handling.
 */
@Component
public class ThermostatAutomationTask {

    private final SmartDeviceService deviceService;
    private final DeviceLogRepository logRepository;
    ThermostatSimulationService thermostatSimulation;
    private SimpMessagingTemplate messagingTemplate;

    ThermostatAutomationTask(SmartDeviceService deviceService, DeviceLogRepository logRepository,
                             ThermostatSimulationService thermostatSimulation, SimpMessagingTemplate messagingTemplate){

        this.deviceService = deviceService;
        this.logRepository = logRepository;
        this.thermostatSimulation = thermostatSimulation;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Orchestrates a single thermostat simulation cycle.
     *
     * Acts as a coordination layer between device retrieval, simulation logic,
     * persistence, and real-time client notification.
     *
     * Delegates all behavioral decisions to ThermostatSimulationService.
     */
    public void simulateTemperatureChange(){

        // Retrieve a list of all of the thermostats from the device service
        // (method returns items as interfaces, cast them to thermostat class)
        List<ISmartDevice> devices = deviceService.getDevices(DeviceType.THERMOSTAT,null,null);

        List<SmartThermostat> thermostats = devices.stream().map(device -> (SmartThermostat) device).toList();

        // Iterate through the list of thermostats and perform the simulation on each
        for (SmartThermostat thermostat : thermostats){

            // Store result of the simulation
            ThermostatResult simulationResult = thermostatSimulation.evaluate(thermostat);

            // If nothing changed, go to the next thermostat
            if (!simulationResult.hasChanged()){
                continue;
            }

            // Persist device state
            deviceService.saveDeviceUpdate(thermostat);

            // Persist Logs by iterating through them and saving them to the log repository
            simulationResult.getLogs().forEach(logRepository::save);

            // Notify clients
            ISmartDevice updatedDevice = deviceService.getDeviceById(thermostat.getUuid());
            DeviceDTO updatedDto = DeviceDTO.fromISmartDevice(updatedDevice);

            messagingTemplate.convertAndSend("/topic/devices", updatedDto);

        }
    }
}
