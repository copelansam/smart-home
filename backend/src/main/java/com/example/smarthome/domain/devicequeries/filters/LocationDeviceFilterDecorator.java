package com.example.smarthome.domain.devicequeries.filters;

import com.example.smarthome.domain.devicequeries.DeviceQueryDecoratorBase;
import com.example.smarthome.domain.devicequeries.IDeviceQuery;
import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Query decorator that filters devices by location.
 *
 * Wraps another {@link IDeviceQuery} and restricts the result set to only
 * include devices matching the specified type.
 *
 * Part of the device query decorator chain used to dynamically apply filters.
 */
public class LocationDeviceFilterDecorator extends DeviceQueryDecoratorBase {

    private String location;

    /**
     * Creates a device type filter decorator.
     *
     * @param query the query to wrap
     * @param location the location to filter by
     */
    public LocationDeviceFilterDecorator(IDeviceQuery query, String location){
        super(query);
        this.location = location;
    }

    /**
     * Filters the wrapped query results to include only devices of the specified location.
     *
     * @return an immutable list of devices matching the given location
     */
    @Override
    public List<ISmartDevice> getItems() {

        // Retrieves the devices from the inner query
        List<ISmartDevice> devices = wrappedQuery.getItems();

        // Prepares a new list for the current filter
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
