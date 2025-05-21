package com.globits.salary.repository;

import com.globits.salary.domain.SalaryParameter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SalaryParameterRepository extends JpaRepository<SalaryParameter, UUID> {
}

