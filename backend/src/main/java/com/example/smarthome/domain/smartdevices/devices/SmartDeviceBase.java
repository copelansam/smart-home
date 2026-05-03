package com.example.smarthome.domain.smartdevices.devices;


import com.example.smarthome.domain.smartdevices.statemachine.states.IState;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.ITransition;
import com.example.smarthome.domain.smartdevices.statemachine.transitions.CallResult;
import com.example.smarthome.repository.StateConverter;
import jakarta.persistence.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class SmartDeviceBase implements ISmartDevice{

    @Id
    @GeneratedValue
    private UUID uuid;

    private String name;
    private String location;
    protected boolean isOn;

    @Convert(converter = StateConverter.class)
    protected IState state;

    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    @Transient
    private List<ITransition<?>> availableActions;

    @Transient
    private List<ITransition<?>> updatableFields;

    public SmartDeviceBase(){}

    public SmartDeviceBase(String name, String location, DeviceType deviceType){
        this.name = name;
        this.location = location;
        this.deviceType = deviceType;
    }

    public UUID getUuid(){return this.uuid;}
    public String getName(){
        return this.name;
    }
    public String getLocation(){
        return this.location;
    }
    public DeviceType getDeviceType(){
        return this.deviceType;
    }
    public boolean getIsOn(){
        return this.isOn;
    }
    public String getState(){return this.state.getName();}
    public IState getStateObject(){return this.state;}
    public void setState(IState newState){
        this.state = newState;
    }
    public List<ITransition<?>> getAvailableTransitions(){
        return Collections.unmodifiableList(this.availableActions);
    }
    public List<ITransition<?>>  getUpdatableFields(){
        return this.updatableFields;
    }

    @PostLoad
    public void onPostLoad(){
        this.availableActions = state.provideAvailableTransitions();
        this.updatableFields = state.provideUpdatableFields();
    }

    public CallResult execute(String transition, Map<String, Object> parameters){
        return this.state.execute(transition, this, parameters);
    }

    public void setIsOn(boolean isOn){
        this.isOn = isOn;
    }
}
