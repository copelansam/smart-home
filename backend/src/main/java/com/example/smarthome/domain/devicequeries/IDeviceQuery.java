package com.example.smarthome.domain.devicequeries;

import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;

import java.util.List;

/**
 * Represents a query for retrieving smart devices based on specific criteria.
 *
 * Implementations define how devices are filtered and returned.
 */
public interface IDeviceQuery {

    /**
     * Returns the devices that match the query criteria.
     *
     * @return list of matching devices
     */
    public List<ISmartDevice> getItems();

}
