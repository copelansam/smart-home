package com.example.smarthome.domain.devicequeries;

import com.example.smarthome.domain.devicequeries.filters.DeviceTypeDeviceFilterDecorator;
import com.example.smarthome.domain.devicequeries.filters.LocationDeviceFilterDecorator;
import com.example.smarthome.domain.devicequeries.filters.OnDeviceFilterDecorator;
import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import com.example.smarthome.repository.ISmartDeviceRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QueryBuilder {

    private final ISmartDeviceRepository repository;

    public QueryBuilder(ISmartDeviceRepository repository){
        this.repository = repository;
    }

    public IDeviceQuery buildQuery(DeviceType type, String location, Boolean isOn) {

        IDeviceQuery query = new AllDevicesQuery(repository);

        if (isOn = true) {
            query = new OnDeviceFilterDecorator(query, true);
        } else if (isOn = false) {
            query = new OnDeviceFilterDecorator(query, false);
        }

        if (type != null) {
            query = new DeviceTypeDeviceFilterDecorator(query, type);
        }

        if (!location.isEmpty()) {
            query = new LocationDeviceFilterDecorator(query, location);
        }
        return query;
    }
}
