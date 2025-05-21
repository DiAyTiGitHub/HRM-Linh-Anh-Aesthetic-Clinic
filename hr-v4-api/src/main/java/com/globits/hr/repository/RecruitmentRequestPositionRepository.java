package com.globits.hr.repository;

import com.globits.hr.domain.Recruitment;
import com.globits.hr.domain.RecruitmentRequestPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface RecruitmentRequestPositionRepository extends JpaRepository<RecruitmentRequestPosition, UUID> {
    @Modifying
    @Transactional
    @Query("DELETE FROM RecruitmentRequestPosition rrq where rrq.recruitment.id = :recruitment")
    void deleteByRecruitment(UUID recruitment);
}
