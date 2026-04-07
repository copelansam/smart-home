package com.example.smarthome.domain.devicequeries.filters;

import com.example.smarthome.domain.devicequeries.DeviceQueryDecoratorBase;
import com.example.smarthome.domain.devicequeries.IDeviceQuery;
import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// This class will be a filter decorator responsible for filtering out devices based on whether they are in a location
// as determined by the user's input which is stored as a String
public class LocationDeviceFilterDecorator extends DeviceQueryDecoratorBase {

    private String location;

    public LocationDeviceFilterDecorator(IDeviceQuery query, String location){
        super(query);
        this.location = location;
    }

    // Filter through the devices based on their location
    @Override
    public List<ISmartDevice> getItems() {

        List<ISmartDevice> devices = wrappedQuery.getItems();
        List<ISmartDevice> filteredDevices = new ArrayList<>();

        // Iterate through all of the previously filtered through devices
        for(ISmartDevice device: devices){
            // If the devices location matches where the user wants to look, add it to the list of filtered devices
            if (device.getLocation().equals(location)){
                filteredDevices.add(device);
            }
        }
        // Return the filtered items as an immutable list
        return Collections.unmodifiableList(filteredDevices);
    }
}
