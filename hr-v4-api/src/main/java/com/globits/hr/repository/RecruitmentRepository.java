package com.globits.hr.repository;

import com.globits.hr.domain.Recruitment;
import com.globits.hr.domain.RecruitmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecruitmentRepository extends JpaRepository<Recruitment, UUID> {
//    @Query("select entity from AllowanceType entity where entity.name =?1")
//    List<AllowanceType> findByName(String name);
//    @Query("select new com.globits.hr.dto.AllowanceTypeDto(s) from AllowanceType s")
//    Page<AllowanceTypeDto> getListPage(Pageable pageable);

    @Query("select rec from Recruitment rec where rec.code = ?1")
    List<Recruitment> findByCode(String code);

    @Query("SELECT COUNT(c) FROM Candidate c WHERE c.recruitment.id = :recruitmentId")
    Long countCandidatesByRecruitmentId(@Param("recruitmentId") UUID recruitmentId);
}
