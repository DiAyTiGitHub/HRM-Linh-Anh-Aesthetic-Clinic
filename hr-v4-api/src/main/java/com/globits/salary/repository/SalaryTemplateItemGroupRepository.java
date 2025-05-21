package com.globits.salary.repository;

import com.globits.salary.domain.SalaryItem;
import com.globits.salary.domain.SalaryTemplate;
import com.globits.salary.domain.SalaryTemplateItemGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SalaryTemplateItemGroupRepository extends JpaRepository<SalaryTemplateItemGroup, UUID> {
	// @Query("select st from SalaryTemplateItemGroup st where si.code = ?1")
	// List<SalaryTemplateItemGroup> findByCode(String code);
}
