package com.example.smarthome.simulation;

import com.example.smarthome.domain.history.DeviceLog;
import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.ThermostatMode;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import com.example.smarthome.repository.DeviceLogRepository;
import com.example.smarthome.service.SmartDeviceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ThermostatAutomationTask {

    private final SmartDeviceService deviceService;
    private final DeviceLogRepository logRepository;

    ThermostatAutomationTask(SmartDeviceService deviceService, DeviceLogRepository logRepository){

        this.deviceService = deviceService;
        this.logRepository = logRepository;
    }

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

            // If it is hotter than what the user wants and the thermostat is either in auto or cooling mode, start cooling
            if (difference >= 1){
                if (!thermostat.getState().contains("Cooling") &&
                        (thermostat.getMode() == ThermostatMode.AUTO || thermostat.getMode() == ThermostatMode.COOL)){
                    CallResult result = thermostat.execute("START_COOLING");

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
            else if (difference <= -1){ // If it is colder that the user wants and the thermostat is in auto or heating mode, start heating
                if (!thermostat.getState().contains("Heating") &&
                        (thermostat.getMode() == ThermostatMode.AUTO || thermostat.getMode() == ThermostatMode.HEAT)){
                    CallResult result = thermostat.execute("START_HEATING");

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
            else if (ambientTemperature <= desiredTemperature   // Checks if the cooling thermostat overshot it and
                    && thermostat.getState().contains("Cooling")){ // goes to idle. Possible if the user enters a decimal for temperatures
                CallResult result = thermostat.execute("STOP_COOLING");
                hasChanged = true;

                if (result != null && result.getLog() != null) {
                    logRepository.save(result.getLog());
                }

            }
            else if (ambientTemperature >= desiredTemperature  // Checks if teh heating thermostat overshot it and goes
                    && thermostat.getState().contains("Heating")){ // to idle. Possible if the user enters a decimal for temperatures
                CallResult result = thermostat.execute("STOP_HEATING");
                hasChanged = true;

                if (result != null && result.getLog() != null) {
                    logRepository.save(result.getLog());
                }
            }

            if (hasChanged) {

                deviceService.saveDeviceUpdate(thermostat);
            }
        }
    }
}
