package com.example.smarthome.simulation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/***
 *  Request payload for updating the temperature of a location.
 *
 *  Contains the target location and the desired temperature value.
 */
public class TempRequest {

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "temperature is required")
    private Double temperature;

    public TempRequest(){}

    public void setLocation(String location){
        this.location = location;
    }

    public String getLocation(){
        return this.location;
    }

    public void setTemperature(Double temperature){
        this.temperature = temperature;
    }

    public Double getTemperature(){
        return this.temperature;
    }
}
