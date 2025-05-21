package com.globits.hr.repository;

import com.globits.hr.domain.EvaluationTemplateItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EvaluationTemplateItemRepository extends JpaRepository<EvaluationTemplateItem, UUID> {
}
