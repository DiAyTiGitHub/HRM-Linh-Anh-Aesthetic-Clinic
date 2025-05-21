package com.globits.salary.repository;

import com.globits.salary.domain.SalaryTemplateItemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SalaryTemplateItemConfigRepository extends JpaRepository<SalaryTemplateItemConfig, UUID> {

}
