package com.example.smarthome.domain.devicequeries.filters;

import com.example.smarthome.domain.devicequeries.DeviceQueryDecoratorBase;
import com.example.smarthome.domain.devicequeries.IDeviceQuery;
import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Query decorator that filters devices by isOn value.
 *
 * Wraps another {@link IDeviceQuery} and restricts the result set to only
 * include devices matching the specified type.
 *
 * Part of the device query decorator chain used to dynamically apply filters.
 */
public class OnDeviceFilterDecorator extends DeviceQueryDecoratorBase {

    private boolean isOn;

    /**
     * Creates a device type filter decorator.
     *
     * @param query the query to wrap
     * @param isOn the power status to filter by (null for all power statuses)
     */
    public OnDeviceFilterDecorator(IDeviceQuery query, boolean isOn){
        super(query);
        this.isOn = isOn;
    }

    /**
     * Filters the wrapped query results to include only devices of the specified power status.
     *
     * @return an immutable list of devices matching the given power status
     */    @Override
    public List<ISmartDevice> getItems() {

        // Retrieves the devices from the inner query
        List<ISmartDevice> filteredItems = new ArrayList<>();

        // Prepares a new list for the current filter
        List<ISmartDevice> devices = wrappedQuery.getItems();

        // Iterate through all of the previously filtered through devices
        for (ISmartDevice device : devices){
            System.out.println(device.getName() + " -> " + device.getIsOn());
            // If the device's on state matches what the user selected, add it to the filtered query, otherwise ignore it
            if (device.getIsOn() == isOn){
                filteredItems.add(device);
            }
        }

        // Return the filtered items as an immutable list
        return Collections.unmodifiableList(filteredItems);
    }
}
