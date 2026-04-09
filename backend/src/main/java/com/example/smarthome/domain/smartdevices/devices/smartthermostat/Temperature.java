package com.example.smarthome.domain.smartdevices.devices.smartthermostat;

import jakarta.persistence.Transient;

import java.util.Random;

// This class represents temperatures in Fahrenheit
public class Temperature{

    private double temperature;

    @Transient
    private Random rand = new Random();

    public Temperature(){
        this.temperature = rand.nextInt(60,80);
    }

    public Temperature(double temperature){
        this.temperature = temperature;
    }

    public double getTemperature() {
        return temperature;
    }

    public void updateTemperature(double amount){
        this.temperature += amount;
    }
}
