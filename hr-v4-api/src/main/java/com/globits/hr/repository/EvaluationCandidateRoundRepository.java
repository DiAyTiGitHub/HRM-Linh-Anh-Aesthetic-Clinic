package com.globits.hr.repository;

import com.globits.hr.domain.EvaluationCandidateRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface EvaluationCandidateRoundRepository extends JpaRepository<EvaluationCandidateRound, UUID> {
    @Query("select ecr from EvaluationCandidateRound ecr where " +
            "ecr.candidateRecruitmentRound.id = ?1 " +
            "and ecr.template.id = ?2")
    List<EvaluationCandidateRound> findByCandidateRecruitmentRound(UUID roundId, UUID templateId);

}

