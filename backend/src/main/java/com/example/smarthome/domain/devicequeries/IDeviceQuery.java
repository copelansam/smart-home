package com.example.smarthome.domain.devicequeries;

import com.example.smarthome.domain.smartdevices.devices.ISmartDevice;

import java.util.List;

// Interface for queries in the decorator pattern.
// Defines the standard operations that both the base query and decorators must implement.
public interface IDeviceQuery {

    // Retrieves a list of devices based on the criteria defined by the concrete class.
    // Base queries and decorators can override this to modify the selection logic
    public List<ISmartDevice> getItems();

}
