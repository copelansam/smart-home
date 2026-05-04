package com.example.smarthome.domain.devicequeries;

import com.example.smarthome.domain.devicequeries.filters.DeviceTypeDeviceFilterDecorator;
import com.example.smarthome.domain.devicequeries.filters.LocationDeviceFilterDecorator;
import com.example.smarthome.domain.devicequeries.filters.OnDeviceFilterDecorator;
import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.repository.ISmartDeviceRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/***
 * Builds {@link IDeviceQuery} instances by dynamically applying filters based on the provided filters.
 *
 * This class uses the Decorator pattern to wrap a base query ({@link AllDevicesQuery}) with additional filtering behavior
 * (device type, location, power status)
 *
 * Each non-null parameter results in a corresponding filter decorator being applied to the query.
 */
@Component
public class QueryBuilder {

    private final ISmartDeviceRepository repository;

    public QueryBuilder(ISmartDeviceRepository repository){
        this.repository = repository;
    }

    /***
     * Builds a device query with optional filtering criteria
     *
     * @param type the device type to filter by (nullable)
     * @param location the location to filter by (nullable)
     * @param isOn the power status to filter by (nullable)
     * @return a composed {@link IDeviceQuery} with the requested filters applied
     *
     * If a parameter is {@code null}, its corresponding filter is not applied.
     */
    public IDeviceQuery buildQuery(DeviceType type, String location, Boolean isOn) {

        // Create base query
        IDeviceQuery query = new AllDevicesQuery(repository);

        // Check each filter and apply their values, if not null, by wrapping the base query

        // power state filter
        if (isOn != null) {
            if (isOn == true) {
                query = new OnDeviceFilterDecorator(query, true);
            } else if (isOn == false) {
                query = new OnDeviceFilterDecorator(query, false);
            }
        }

        // device type filter
        if (type != null) {
            query = new DeviceTypeDeviceFilterDecorator(query, type);
        }

        // location filter
        if (location != null) {
            query = new LocationDeviceFilterDecorator(query, location);
        }

        // return composed query
        return query;
    }
}
