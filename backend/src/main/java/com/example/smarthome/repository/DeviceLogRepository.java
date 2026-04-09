package com.example.smarthome.repository;

import com.example.smarthome.domain.history.DeviceLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeviceLogRepository extends JpaRepository<DeviceLog, Long> {

    List<DeviceLog> findByDeviceIdOrderByTimestampDesc(UUID deviceId);

}
