package com.example.smarthome.domain.devicequeries.filters;

import com.example.smarthome.domain.devicequeries.DeviceQueryDecoratorBase;
import com.example.smarthome.domain.devicequeries.IDeviceQuery;
import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// This class will be a filter decorator responsible for filtering out devices based on whether they are on or not
// as determined by the user's input which is stored as a boolean
public class OnDeviceFilterDecorator extends DeviceQueryDecoratorBase {

    private boolean isOn;

    public OnDeviceFilterDecorator(IDeviceQuery query, boolean isOn){
        super(query);
        this.isOn = isOn;
    }

    // Filter through the devices based on their on status
    @Override
    public List<ISmartDevice> getItems() {
        List<ISmartDevice> filteredItems = new ArrayList<>();
        List<ISmartDevice> devices = wrappedQuery.getItems();

        // Iterate through all of the previously filtered through devices
        for (ISmartDevice device : devices){
            // If the device's on state matches what the user selected, add it to the filtered query, otherwise ignore it
            if (device.getIsOn() == isOn){
                filteredItems.add(device);
            }
        }
        // Return the filtered items as an immutable list
        return Collections.unmodifiableList(filteredItems);
    }
}
