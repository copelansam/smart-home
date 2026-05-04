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

/**
 * Base entity for all smart devices in the system.
 *
 * Provides common properties such as name, location, power state, and device type,
 * along with integration into the state machine that governs device behavior.
 *
 * Uses JPA inheritance to allow concrete device implementations to extend this class
 * while maintaining a shared persistence structure.
 *
 * The device's behavior is driven by its current {@link IState}, which determines
 * available transitions and updatable fields.
 *
 *  Note: State-dependent fields such as available transitions are not persisted.
 *  They are recomputed after loading from the database.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class SmartDeviceBase implements ISmartDevice{

    /** Unique identifier for the device. Automatically generated */
    @Id
    @GeneratedValue
    private UUID uuid;

    /** Human-readable device name. */
    private String name;

    /** Location of the device within the smart home. */
    private String location;

    /** Indicates whether the device is powered on. */
    protected boolean isOn;

    /**
     * Current state of the device.
     *
     * Persisted as a string using {@link StateConverter}.
     */
    @Convert(converter = StateConverter.class)
    protected IState state;

    /** Type of the device (e.g., LIGHT, FAN, THERMOSTAT). */
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    /** Cached list of available transitions derived from the current state. */
    @Transient
    private List<ITransition<?>> availableActions;

    /** Cached list of updatable fields derived from the current state. */
    @Transient
    private List<ITransition<?>> updatableFields;

    public SmartDeviceBase(){}

    /**
     * Constructs a smart device with basic identifying information.
     *
     * @param name the device name
     * @param location the device location
     * @param deviceType the type of device
     */
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
    public void setState(IState newState){
        this.state = newState;
    }

    /**
     * @return an immutable list of transitions available in the current state
     */
    public List<ITransition<?>> getAvailableTransitions(){
        return Collections.unmodifiableList(this.availableActions);
    }

    /**
     * @return an immutable list of fields that can be updated in the current state
     */
    public List<ITransition<?>>  getUpdatableFields(){
        return Collections.unmodifiableList(this.updatableFields);
    }

    /**
     * Initializes transient state-dependent fields after the entity is loaded from the database.
     *
     * Recomputes available transitions and updatable fields based on the current state.
     */
    @PostLoad
    public void onPostLoad(){
        this.availableActions = state.provideAvailableTransitions();
        this.updatableFields = state.provideUpdatableFields();
    }

    /**
     * Executes a transition on the device via its current state.
     *
     * @param transition the name of the transition to execute
     * @param parameters optional parameters required for the transition
     * @return the result of the transition execution
     */
    public CallResult execute(String transition, Map<String, Object> parameters){
        return this.state.execute(transition, this, parameters);
    }

    public void setIsOn(boolean isOn){
        this.isOn = isOn;
    }
}
