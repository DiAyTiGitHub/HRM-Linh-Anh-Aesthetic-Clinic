package com.globits.hr.repository;

import com.globits.hr.domain.EvaluationTemplate;
import com.globits.hr.domain.EvaluationTemplateItemValue;
import com.globits.hr.dto.EvaluationTemplateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EvaluationTemplateItemValueRepository extends JpaRepository<EvaluationTemplateItemValue, UUID> {

}
