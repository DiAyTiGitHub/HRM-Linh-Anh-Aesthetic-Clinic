package com.globits.hr.repository;

import com.globits.hr.domain.EvaluationTemplate;
import com.globits.hr.domain.RecruitmentPlan;
import com.globits.hr.dto.EvaluationTemplateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EvaluationTemplateRepository extends JpaRepository<EvaluationTemplate, UUID> {

    @Query("SELECT new com.globits.hr.dto.EvaluationTemplateDto(entity,false) FROM EvaluationTemplate entity where (entity.voided IS NULL OR entity.voided = FALSE)")
    Page<EvaluationTemplateDto> paging(Pageable pageable);

    @Query("select entity from EvaluationTemplate entity where entity.code = ?1")
    List<EvaluationTemplate> findByCode(String code);
}
