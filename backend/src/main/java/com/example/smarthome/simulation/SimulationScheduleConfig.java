package com.example.smarthome.simulation;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.Instant;
import java.util.Date;

@Configuration
@EnableScheduling
public class SimulationScheduleConfig implements SchedulingConfigurer {

    private final ThermostatAutomationTask thermostatAutomationTask;
    private final SimulationSettings settings;

    public SimulationScheduleConfig(ThermostatAutomationTask thermostatAutomationTask, SimulationSettings settings){

        this.thermostatAutomationTask = thermostatAutomationTask;
        this.settings = settings;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar){

        taskRegistrar.addTriggerTask(
                () -> thermostatAutomationTask.simulateTemperatureChange(),
                triggerContext -> {

                    long intervalMilliseconds = (long) (settings.calculateSecondsPerDegree() * 1000);

                    Instant lastCompletionTime = triggerContext.lastCompletion();
                    if (lastCompletionTime == null){
                        lastCompletionTime = Instant.now();
                    }

                    return lastCompletionTime.plusMillis(intervalMilliseconds);
                }
        );
    }
}
