package com.globits.hr.repository;

import com.globits.hr.domain.RecruitmentRequest;
import com.globits.hr.projection.RecruitmentRequestSummary;
import com.globits.salary.domain.SalaryItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface RecruitmentRequestRepository extends JpaRepository<RecruitmentRequest, UUID> {
//    @Query("select entity from AllowanceType entity where entity.name =?1")
//    List<AllowanceType> findByName(String name);
//    @Query("select new com.globits.hr.dto.AllowanceTypeDto(s) from AllowanceType s")
//    Page<AllowanceTypeDto> getListPage(Pageable pageable);

    @Query("select rr from RecruitmentRequest rr where rr.code = ?1")
    List<RecruitmentRequest> findByCode(String code);

    @Query(value = "SELECT code FROM tbl_recruitment_request WHERE code LIKE CONCAT(:prefix, '_%') ORDER BY CAST(SUBSTRING(code, LENGTH(:prefix) + :zeroPadding) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String findMaxCodeByPrefix(@Param("prefix") String prefix, @Param("zeroPadding") Integer zeroPadding);

    @Query(
            value = """
                    SELECT rr.name                                                       as name,
                            rr.recruiting_start_date                                     as startDate,
                            rr.recruiting_end_date                                       as endDate,
                           COUNT(DISTINCT c.id)                                          AS totalCandidates,
                           COUNT(DISTINCT IF(crr.result_status IS NOT NULL, c.id, NULL)) AS candidatesWithResultStatus,
                           COUNT(DISTINCT IF(c.is_send_mail_offer = true, c.id, NULL))   AS candidatesSentOfferMail
                    FROM tbl_recruitment_request rr
                             JOIN tbl_recruitment_plan rp ON rr.id = rp.recruitment_request_id
                             JOIN tbl_recruitment_round r ON rp.id = r.recruitment_plan_id
                             JOIN tbl_candidate_recruitment_round crr ON r.id = crr.recruitment_round_id
                             JOIN tbl_candidate c ON crr.candidate_id = c.id
                             JOIN tbl_person p ON p.id = c.id
                    WHERE rr.voided IS NOT TRUE
                      AND rp.voided IS NOT TRUE
                      AND p.voided IS NOT TRUE
                      AND (:keyword IS NULL OR LOWER(rr.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(rr.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
                      AND (:requestId IS NULL OR rr.id = :requestId)
                      AND (:plainId IS NULL OR r.id = :plainId)
                      AND (rr.recruiting_start_date >= :fromDate OR :fromDate IS NULL)
                      AND (rr.recruiting_start_date <= :toDate OR :toDate IS NULL)
                      AND (rr.recruiting_end_date >= :fromEndDate OR :fromEndDate IS NULL)
                      AND (rr.recruiting_end_date <= :toEndDate OR :toEndDate IS NULL)
                    GROUP BY rr.id
                    """,
            countQuery = """
                    SELECT COUNT(*) FROM (
                        SELECT rr.id
                        FROM tbl_recruitment_request rr
                                 JOIN tbl_recruitment_plan rp ON rr.id = rp.recruitment_request_id
                                 JOIN tbl_recruitment_round r ON rp.id = r.recruitment_plan_id
                                 JOIN tbl_candidate_recruitment_round crr ON r.id = crr.recruitment_round_id
                                 JOIN tbl_candidate c ON crr.candidate_id = c.id
                                 JOIN tbl_person p ON p.id = c.id
                        WHERE rr.voided IS NOT TRUE
                          AND rp.voided IS NOT TRUE
                          AND p.voided IS NOT TRUE
                          AND (:keyword IS NULL OR LOWER(rr.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(rr.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
                          AND (:requestId IS NULL OR rr.id = :requestId)
                          AND (:plainId IS NULL OR r.id = :plainId)
                          AND (rr.recruiting_start_date >= :fromDate OR :fromDate IS NULL)
                          AND (rr.recruiting_start_date <= :toDate OR :toDate IS NULL)
                          AND (rr.recruiting_end_date >= :fromEndDate OR :fromEndDate IS NULL)
                          AND (rr.recruiting_end_date <= :toEndDate OR :toEndDate IS NULL)
                        GROUP BY rr.id
                    ) as grouped
                    """,
            nativeQuery = true
    )
    Page<RecruitmentRequestSummary> getRecruitmentRequestSummaries(
            @Param("requestId") String requestId,
            @Param("plainId") String plainId,
            @Param("keyword") String keyword,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("fromEndDate") Date fromEndDate,
            @Param("toEndDate") Date toEndDate,
            Pageable pageable
    );

}
