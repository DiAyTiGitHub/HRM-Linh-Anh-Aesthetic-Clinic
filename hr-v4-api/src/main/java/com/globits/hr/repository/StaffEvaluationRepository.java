package com.globits.hr.repository;

import com.globits.hr.domain.StaffEvaluation;
import com.globits.hr.dto.StaffEvaluationDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface StaffEvaluationRepository extends JpaRepository<StaffEvaluation, UUID> {
    @Modifying
    @Transactional
    @Query("DELETE FROM StaffEvaluation evaluation where evaluation.form.id = :form")
    void deleteByFormId(UUID form);
    @Query("SELECT NEW com.globits.hr.dto.StaffEvaluationDto(evaluation) FROM StaffEvaluation evaluation where evaluation.form.id = :form")
    List<StaffEvaluationDto> findByFormId(UUID form);
}
