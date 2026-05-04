package com.example.smarthome.domain.devicequeries.filters;

import com.example.smarthome.domain.devicequeries.DeviceQueryDecoratorBase;
import com.example.smarthome.domain.devicequeries.IDeviceQuery;
import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Query decorator that filters devices by {@link DeviceType}.
 *
 * Wraps another {@link IDeviceQuery} and restricts the result set to only
 * include devices matching the specified type.
 *
 * Part of the device query decorator chain used to dynamically apply filters.
 */
public class DeviceTypeDeviceFilterDecorator extends DeviceQueryDecoratorBase {

    private DeviceType deviceType;

    /**
     * Creates a device type filter decorator.
     *
     * @param query the query to wrap
     * @param deviceType the device type to filter by
     */
    public DeviceTypeDeviceFilterDecorator(IDeviceQuery query, DeviceType deviceType){
        super(query);
        this.deviceType = deviceType;
    }

    /**
     * Filters the wrapped query results to include only devices of the specified type.
     *
     * @return an immutable list of devices matching the given type
     */
    @Override
    public List<ISmartDevice> getItems() {

        // Retrieves the devices from the inner query
        List<ISmartDevice> devices = wrappedQuery.getItems();

        // Prepares a new list for the current filter
        List<ISmartDevice> filteredDevices = new ArrayList<>();

        // Iterate through all of the previously filtered through devices
        for (ISmartDevice device : devices){
            // If the device's type matches what the user selected, add it to the new filtered query, otherwise ignore it
            if (device.getDeviceType() == deviceType){
                filteredDevices.add(device);
            }
        }
        // Return the filtered items as an immutable list
        return Collections.unmodifiableList(filteredDevices);
    }
}
