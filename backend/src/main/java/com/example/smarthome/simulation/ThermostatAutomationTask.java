package com.example.smarthome.simulation;

import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.domain.smartdevices.devices.smartthermostat.SmartThermostat;
import com.example.smarthome.service.SmartDeviceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ThermostatAutomationTask {

    private SmartDeviceService deviceService;

    ThermostatAutomationTask(SmartDeviceService deviceService){

        this.deviceService = deviceService;
    }

    @Scheduled(fixedRate = 1000)
    public void simulateTemperatureChange(){

        List<ISmartDevice> devices = deviceService.getDevices(DeviceType.THERMOSTAT,null,null);

        List<SmartThermostat> thermostats = devices.stream().map(device -> (SmartThermostat) device).toList();

        for (SmartThermostat thermostat : thermostats){

            double ambientTemperature = thermostat.getAmbientTemperature().getTemperature();
            double desiredTemperature = thermostat.getDesiredTemperature().getTemperature();

            // If it is hotter than what the user wants, start cooling
            if (ambientTemperature - desiredTemperature > 1){
                thermostat.execute("START_COOLING");
                thermostat.getAmbientTemperature().updateTemperature(-1);
            }
            else if (ambientTemperature - desiredTemperature < -1){ // If it is colder that the user wants, start heating
                thermostat.execute("START_HEATING");
                thermostat.getAmbientTemperature().updateTemperature(1);
            }
            else if (ambientTemperature <= desiredTemperature   // Checks if the cooling thermostat overshot it and
                    && thermostat.getState().contains("Cooling")){ // goes to idle. Possible if the user enters a decimal for temperatures
                thermostat.execute("STOP_COOLING");
            }
            else if (ambientTemperature >= desiredTemperature  // Checks if teh heating thermostat overshot it and goes
                    && thermostat.getState().contains("Heating")){ // to idle. Possible if the user enters a decimal for temperatures
                thermostat.execute("STOP_HEATING");
            }
            deviceService.saveDeviceUpdate(thermostat);
        }
    }
}
