package com.example.smarthome.domain.devicequeries;

import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;

import java.util.List;

/**
 * Base class for device query decorators.
 *
 * Implements the Decorator pattern by wrapping another {@link IDeviceQuery}
 * and delegating calls to it. Subclasses can extend or modify the behavior
 * of {@link #getItems()} to apply additional filtering or transformation logic.
 */
public abstract class DeviceQueryDecoratorBase implements IDeviceQuery{

    protected IDeviceQuery wrappedQuery;

    /**
     * Creates a new decorator wrapping the given query.
     *
     * @param wrappedQuery the query to be decorated
     */
    public DeviceQueryDecoratorBase(IDeviceQuery wrappedQuery){

        this.wrappedQuery = wrappedQuery;
    }

    /**
     * Delegates retrieval to the wrapped query.
     * Subclasses may override to modify the result.
     */
    @Override
    public List<ISmartDevice> getItems(){
        return wrappedQuery.getItems();
    }

}
