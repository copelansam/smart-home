package com.example.smarthome.domain.devicequeries;

import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;
import com.example.smarthome.repository.ISmartDeviceRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// This class will represent a query that will return all devices with no filters applied to it.
// This serves as the base query in the decorator pattern
// Filters will be applied based on the user's choices via the decorator pattern.
public class AllDevicesQuery implements IDeviceQuery{

    private ISmartDeviceRepository repo;

    public AllDevicesQuery(ISmartDeviceRepository repo){
        this.repo = repo;
    }

    // Retrieves all items from the repository and returns them as an immutable list to prevent external modification
    @Override
    public List<ISmartDevice> getItems(){

        // Retrieves all SmartDeviceBase objects from the repository and treats them as ISmartDevice
        List<ISmartDevice> allItems = new ArrayList<>(repo.findAll());

        // Returns an immutable list of the items
        return Collections.unmodifiableList(allItems);
    }
}
