package com.globits.salary.repository;

import com.globits.salary.domain.SalaryItemThreshold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SalaryItemThresholdRepository extends JpaRepository<SalaryItemThreshold, UUID> {

}
