package com.example.smarthome.domain.devicequeries.filters;

import com.example.smarthome.domain.devicequeries.DeviceQueryDecoratorBase;
import com.example.smarthome.domain.devicequeries.IDeviceQuery;
import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


// This class will be a filter decorator responsible for filtering out devices based on their device type
// as determined by the user's input which is stored as a DeviceType
public class DeviceTypeDeviceFilterDecorator extends DeviceQueryDecoratorBase {

    private DeviceType deviceType;

    public DeviceTypeDeviceFilterDecorator(IDeviceQuery query, DeviceType deviceType){
        super(query);
        this.deviceType = deviceType;
    }

    // Filter through the devices based on their device type
    @Override
    public List<ISmartDevice> getItems() {

        List<ISmartDevice> devices = wrappedQuery.getItems();
        List<ISmartDevice> filteredDevices = new ArrayList<>();

        // Iterate through all of the previously filtered through devices
        for (ISmartDevice device : devices){
            // If the device's type matches what the user selected, add it to the filtered query, otherwise ignore it
            if (device.getDeviceType() == deviceType){
                filteredDevices.add(device);
            }
        }
        // Return the filtered items as an immutable list
        return Collections.unmodifiableList(filteredDevices);
    }
}
