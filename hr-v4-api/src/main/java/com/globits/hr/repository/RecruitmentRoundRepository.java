package com.globits.hr.repository;

import com.globits.hr.domain.CandidateRecruitmentRound;
import com.globits.hr.domain.RecruitmentRound;
import com.globits.hr.dto.RecruitmentRoundDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecruitmentRoundRepository extends JpaRepository<RecruitmentRound, UUID> {
    @Modifying
    @Transactional
    @Query("DELETE FROM RecruitmentRound round WHERE round.recruitmentPlan.id = :planId")
    void deleteByRecruitmentPlanId(UUID planId);

    @Query("SELECT new com.globits.hr.dto.RecruitmentRoundDto(round) FROM RecruitmentRound round WHERE round.recruitmentPlan.id = :planId")
    List<RecruitmentRoundDto> findByRecruitmentPlanId(UUID planId, PageRequest pageRequest);

    @Query("SELECT new com.globits.hr.dto.RecruitmentRoundDto(round) FROM RecruitmentRound round WHERE round.recruitmentPlan.id = :planId")
    List<RecruitmentRoundDto> getListRecruitmentRoundByPlanId(UUID planId);

    @Query("SELECT round FROM RecruitmentRound round WHERE round.recruitmentPlan.id = :planId ORDER BY round.roundOrder ")
    List<RecruitmentRound> findAllByRecruitmentPlan(UUID planId);
    
    @Query("SELECT round FROM RecruitmentRound round WHERE round.recruitmentPlan.id = :planId ORDER BY round.roundOrder ASC ")
    List<RecruitmentRound> findByRecruitmentPlan(UUID planId, PageRequest pageRequest);

    @Query("SELECT round FROM RecruitmentRound round WHERE round.recruitmentPlan.id = :planId AND round.roundOrder = :roundOrder ")
    RecruitmentRound findByRecruitmentPlanAndRoundOrder(UUID planId, Integer roundOrder);
}
