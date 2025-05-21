package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.SystemConfig;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, UUID> {
	@Query("SELECT entity FROM SystemConfig entity WHERE entity.configKey = ?1")
	List<SystemConfig> getByConfigKey(String configKey);

	SystemConfig getSystemConfigByConfigKey(String configKey);
}
