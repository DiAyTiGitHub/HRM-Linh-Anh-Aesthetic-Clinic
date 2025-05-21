package com.globits.hr.repository;

import com.globits.hr.domain.HrGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HrGroupRepository extends JpaRepository<HrGroup, UUID> {
}
