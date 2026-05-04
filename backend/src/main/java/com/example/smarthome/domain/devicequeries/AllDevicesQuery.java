package com.example.smarthome.domain.devicequeries;

import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.repository.ISmartDeviceRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base query that retrieves all smart devices with no filtering applied.
 *
 * Serves as the foundation of the device query system and is typically
 * wrapped by query decorators to apply filtering criteria.
 */
public class AllDevicesQuery implements IDeviceQuery{

    private final ISmartDeviceRepository repo;

    /**
     * Creates a query that retrieves all devices from the repository.
     *
     * @param repo the repository used to access device data
     */
    public AllDevicesQuery(ISmartDeviceRepository repo){
        this.repo = repo;
    }

    /**
     * Retrieves all devices from the repository.
     *
     * @return an immutable list of all smart devices
     */    @Override
    public List<ISmartDevice> getItems(){

        // Retrieve a list of all devices
        List<ISmartDevice> allItems = new ArrayList<>(repo.findAll());

        // Returns an immutable list of the items
        return Collections.unmodifiableList(allItems);
    }
}
