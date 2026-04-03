package com.example.smarthome.repository;


import com.example.smarthome.domain.smartdevices.devices.SmartDeviceBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

// This class will act as the repository for all smart devices.
// Defined as an interface, Spring Boot will automatically instantiate it at start up
@Repository
public interface SmartDeviceRepository extends JpaRepository<SmartDeviceBase, UUID> {
}
