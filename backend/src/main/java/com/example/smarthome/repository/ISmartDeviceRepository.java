package com.example.smarthome.repository;


import com.example.smarthome.domain.smartdevices.devices.DeviceType;
import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for managing smart device persistence.
 *
 * Provides CRUD operations for SmartDevice entities and supports
 * custom query methods for device lookups based on business rules.
 */
@Repository
public interface ISmartDeviceRepository extends JpaRepository<SmartDeviceBase, UUID> {

    boolean existsByDeviceTypeAndLocation(DeviceType deviceType, String location);
}
