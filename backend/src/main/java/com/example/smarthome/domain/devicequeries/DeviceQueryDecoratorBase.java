package com.example.smarthome.domain.devicequeries;

import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;

import java.util.List;

// This class acts as the query decorator base essential for the decorator pattern.
// It keeps track of the wrapped query (return all devices), allowing concrete decorators
// to add or modify behavior of the getItems() method while maintaining original query functionality.
public abstract class DeviceQueryDecoratorBase implements IDeviceQuery{

    protected IDeviceQuery wrappedQuery;

    public DeviceQueryDecoratorBase(IDeviceQuery wrappedQuery){

        this.wrappedQuery = wrappedQuery;
    }

    @Override
    public List<SmartDeviceBase> getItems(){
        return wrappedQuery.getItems();
    }

}
