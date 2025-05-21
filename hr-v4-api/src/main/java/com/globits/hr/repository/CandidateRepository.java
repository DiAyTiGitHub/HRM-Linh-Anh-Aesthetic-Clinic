package com.globits.hr.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.globits.hr.dto.CandidateDto;
import com.globits.hr.dto.StaffDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.Candidate;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, UUID> {
//    @Query("select new com.globits.hr.dto.StaffDto(s.id, s.staffCode, s.displayName, s.gender) from Staff s")
//    Page<StaffDto> findByPageBasicInfo(Pageable pageable);
//
//    @Query("select new com.globits.hr.dto.StaffDto(u, true) from Staff u where u.staffCode = ?1")
//    StaffDto getByUsername(String username);
//
//    @Query("select s from Staff s where s.user.username = ?1")
//    Staff findByUsername(String username);
//
//
//    @Query("select new com.globits.hr.dto.StaffDto(s) from Staff s where s.staffCode like %?1% or s.displayName like %?1%")
//    Page<StaffDto> findPageByCodeOrName(String staffCode, Pageable pageable);
//
//    @Query("select u from Staff u where u.staffCode = ?1")
//    List<Staff> getByCode(String staffCode);
//
//    @Query("select u from Staff u where u.id = ?1")
//    Staff findOneById(UUID id);
//
//    @Query("select count(u.id) from Staff u where u.idNumber = ?2 and (u.id <> ?1 or ?1 is null )")
//    Long countByIdNumber(UUID id, String idNumber);
//
//    @Query("select new com.globits.hr.dto.StaffDto(u) from Staff u where u.id IN :idList")
//    List<StaffDto> getAllByStaffId(List<UUID> idList);
//
//    //tìm kiếm nhân viên theo tháng
//    //TT01 là trạng thái đang làm việc
//    @Query("SELECT distinct new com.globits.hr.dto.StaffDto(s, false) FROM Staff s " +
//            "WHERE FUNCTION('MONTH', s.birthDate) = :month AND s.status.code = 'TT01'")
//    List<StaffDto> findStaffsHaveBirthDayByMonth(int month);

    @Query("select c from Candidate c where c.candidateCode = ?1")
    List<Candidate> findByCode(String code);

    @Query("select new com.globits.hr.dto.CandidateDto(c) from Candidate c where c.phoneNumber = ?1 or c.Email = ?2")
    List<CandidateDto> getByPhoneNumberOrEmail(String phoneNumber, String email);

    @Query("select new com.globits.hr.dto.CandidateDto(c) from Candidate c " +
            "where c.staff.id = :staffId " +
            "order by c.submissionDate desc ")
    List<CandidateDto> getExistCandidateProfileOfStaff(@Param("staffId") UUID staffId);

    @Query("select c from Candidate c where c.id IN :ids")
    List<Candidate> findByIdIn(Set<UUID> ids);

    @Query("select new com.globits.hr.dto.CandidateDto(c) from Candidate c " +
            "where c.recruitmentPlan.id = :recruitmentPlanId " +
            "order by c.submissionDate desc ")
    List<CandidateDto> getByPlan(@Param("recruitmentPlanId") UUID recruitmentPlanId);
    @Query("select count(entity.id) FROM Candidate entity where entity.candidateCode =?1 ")
    Long checkCode(String candidateCode);

    @Query("select count(c) FROM Candidate c where (c.voided IS NULL OR c.voided) AND c.recruitmentPlan.recruitmentRequest.id = :requestId AND c.status = :status")
    Long countNumberCandidateByStatusAndRecruitmentRequest(UUID requestId, Integer status);

    @Query(value = "SELECT candidate_code FROM tbl_candidate WHERE candidate_code LIKE CONCAT(:prefix, '_%') ORDER BY CAST(SUBSTRING(candidate_code, LENGTH(:prefix) + :zeroPadding) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String findMaxCodeByPrefix(@Param("prefix") String prefix, @Param("zeroPadding") Integer zeroPadding);
    @Query("select new com.globits.hr.dto.CandidateDto(c) from Candidate c where c.phoneNumber = ?1 or c.Email = ?2 or c.idNumber = ?3")
    List<CandidateDto> checkDuplicate(String phoneNumber, String email, String idNumber);
}
