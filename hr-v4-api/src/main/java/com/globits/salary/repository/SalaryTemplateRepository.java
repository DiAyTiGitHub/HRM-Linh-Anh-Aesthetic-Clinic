package com.globits.salary.repository;

import java.util.List;
import java.util.UUID;

import com.globits.salary.domain.SalaryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.globits.salary.domain.SalaryTemplate;

@Repository
public interface SalaryTemplateRepository extends JpaRepository<SalaryTemplate, UUID> {
	@Query("select st from SalaryTemplate st where st.code = ?1")
	List<SalaryTemplate> findByCode(String code);

}
