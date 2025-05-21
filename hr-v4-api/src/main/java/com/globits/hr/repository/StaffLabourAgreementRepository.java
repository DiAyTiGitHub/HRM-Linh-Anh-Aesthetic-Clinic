package com.globits.hr.repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.globits.hr.dto.LabourAgreementTypeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.StaffLabourAgreement;
import com.globits.hr.dto.StaffLabourAgreementDto;


@Repository
public interface StaffLabourAgreementRepository extends JpaRepository<StaffLabourAgreement, UUID> {

    @Query("select new com.globits.hr.dto.StaffLabourAgreementDto(agreement) from StaffLabourAgreement agreement where agreement.id = ?1")
    StaffLabourAgreementDto getAgreementById(UUID id);

    @Query("select new com.globits.hr.dto.StaffLabourAgreementDto(agreement) from StaffLabourAgreement agreement where agreement.staff.id = ?1")
    List<StaffLabourAgreementDto> getAll(UUID id);

    @Query("SELECT agreement FROM StaffLabourAgreement agreement WHERE agreement.staff.staffCode = :staffCode AND agreement.labourAgreementNumber = :labourAgreementNumber")
    List<StaffLabourAgreement> getStaffLabourAgreementByStaffCodeAndNumberContract(@Param("staffCode") String staffCode, @Param("labourAgreementNumber") String labourAgreementNumber);

    @Query("select new com.globits.hr.dto.StaffLabourAgreementDto(agreement)  from StaffLabourAgreement agreement")
    Page<StaffLabourAgreementDto> getPages(Pageable pageable);


    @Query("select sla from StaffLabourAgreement sla " +
            "where sla.staff.id = :staffId " +
            "and date(sla.startDate) <= :periodStartDate " +
            "and date(sla.endDate) >= :periodEndDate " +
            "order by sla.modifyDate desc ")
    List<StaffLabourAgreement> getLabourAgreementOfStaffCoverRangeTime(@Param("staffId") UUID staffId,
                                                                       @Param("periodStartDate") Date periodStartDate,
                                                                       @Param("periodEndDate") Date periodEndDate);

    @Query("SELECT new com.globits.hr.dto.StaffLabourAgreementDto(SUM(agreement.staffTotalInsuranceAmount), SUM(agreement.orgTotalInsuranceAmount), "
            + "SUM(agreement.insuranceSalary)) FROM StaffLabourAgreement agreement " +
            "where agreement.hasSocialIns is not null " +
            "and agreement.hasSocialIns = true ")
    StaffLabourAgreementDto getTotalInsuranceAmounts();

    @Query("SELECT sla FROM StaffLabourAgreement sla " +
            "WHERE sla.agreementStatus = :agreementStatus " +
            "AND (DATE(sla.startDate) <= :periodEndDate) " +
            "AND (sla.endDate IS NULL OR DATE(sla.endDate) >= :periodStartDate) " +
            "AND (sla.salaryTemplate.id = :salaryTemplateId) " +
            "ORDER BY sla.modifyDate DESC")
    List<StaffLabourAgreement> getByStatusAndPeriodAndTemplate(
            @Param("agreementStatus") Integer agreementStatus,
            @Param("periodStartDate") Date periodStartDate,
            @Param("periodEndDate") Date periodEndDate,
            @Param("salaryTemplateId") UUID salaryTemplateId);

    @Query("SELECT sla FROM StaffLabourAgreement sla " +
            "WHERE sla.agreementStatus = :agreementStatus " +
            "AND (DATE(sla.startDate) <= :periodEndDate) " +
            "AND (sla.endDate IS NULL OR DATE(sla.endDate) >= :periodStartDate) " +
            "AND (sla.salaryTemplate.id = :salaryTemplateId) " +
            "AND (sla.staff.id = :staffId) " +
            "ORDER BY sla.modifyDate DESC")
    List<StaffLabourAgreement> getByStatusAndPeriodAndTemplateAndStaff(
            @Param("agreementStatus") Integer agreementStatus,
            @Param("periodStartDate") Date periodStartDate,
            @Param("periodEndDate") Date periodEndDate,
            @Param("salaryTemplateId") UUID salaryTemplateId,
            @Param("staffId") UUID staffId);

    @Query("SELECT sla FROM StaffLabourAgreement sla " +
            "WHERE sla.agreementStatus = :agreementStatus " +
            "AND (DATE(sla.startDate) <= :periodEndDate) " +
            "AND (sla.endDate IS NULL OR DATE(sla.endDate) >= :periodStartDate) " +
            "AND (sla.staff.id = :staffId) " +
            "ORDER BY sla.modifyDate DESC")
    List<StaffLabourAgreement> getByStatusAndPeriodAndStaff(
            @Param("agreementStatus") Integer agreementStatus,
            @Param("periodStartDate") Date periodStartDate,
            @Param("periodEndDate") Date periodEndDate,
            @Param("staffId") UUID staffId);

    @Query("SELECT sla FROM StaffLabourAgreement sla " +
            "WHERE sla.staff.id = :staffId")
    List<StaffLabourAgreement> findByStaffId(
            @Param("staffId") UUID staffId);


    @Query("SELECT new com.globits.hr.dto.StaffLabourAgreementDto(entity) FROM StaffLabourAgreement entity " +
            " WHERE entity.agreementStatus = :agreementStatus " +
            " AND entity.endDate IS NOT NULL AND entity.endDate >= current_date" +
            " AND DATEDIFF(entity.endDate, current_date) <= :expiryDays " +
            " ORDER BY entity.endDate DESC")
    List<StaffLabourAgreementDto> getListOverdueContract(@Param("agreementStatus") Integer agreementStatus, @Param("expiryDays") Integer expiryDays);

    @Query(value = """
                   SELECT
                   sla.staff_id AS staffId,
                   org.name AS nameOrg,
                   org.code AS codeOrg,
                   sla.labour_agreement_number AS labourAgreementNumber,
                   sla.start_date AS startDate,
                   sla.end_date AS endDate,
                   DATEDIFF(sla.end_date, sla.start_date) AS totalDays,
                   sla.insurance_salary as insuranceSalary,
                   ct.name AS contactTypeName,
                   sla.signed_date AS signDate,
                   sla.salary AS salary
                   FROM tbl_staff_labour_agreement sla
                   INNER JOIN (
                   SELECT
                        staff_id,
                        MAX(start_date) AS max_start_date
                   FROM tbl_staff_labour_agreement
                   WHERE (voided IS NULL OR voided = FALSE)
                   GROUP BY staff_id
                   ) latest ON sla.staff_id = latest.staff_id
                       AND sla.start_date = latest.max_start_date
                   LEFT JOIN tbl_organization org ON sla.contract_organization_id = org.id
                   LEFT JOIN tbl_contract_type ct ON ct.id = sla.contract_type_id
                   WHERE (sla.voided IS NULL OR sla.voided = FALSE) AND sla.staff_id IS NOT NULL
            """, nativeQuery = true)
    List<Object[]> findLatestLabourAgreements();


    @Query("select agreement from StaffLabourAgreement agreement " +
            "where agreement.staff.id = :staffId and agreement.labourAgreementNumber = :labourAgreementNumber ")
    List<StaffLabourAgreement> findByStaffIdAndAgreementNumber(@Param("staffId") UUID staffId, @Param("labourAgreementNumber") String labourAgreementNumber);

    @Query("SELECT NEW com.globits.hr.dto.StaffLabourAgreementDto(labour) " +
            "FROM StaffLabourAgreement labour " +
            "WHERE labour.staff.id = :staffId " +
            "AND labour.startDate = (SELECT MAX(s.startDate) FROM StaffLabourAgreement s WHERE s.staff.id = :staffId)")
    List<StaffLabourAgreementDto> getLastLabourAgreement(UUID staffId);

    @Query("SELECT entity " +
            "FROM StaffLabourAgreement entity " +
            "WHERE entity.staff.id = :staffId " +
            "and entity.contractType.code = :contractTypeCode " +
            "order by entity.startDate desc, entity.endDate desc ")
    List<StaffLabourAgreement> findLatestLabourByContractTypeCodeOfStaff(@Param("staffId") UUID staffId,
                                                                         @Param("contractTypeCode") String contractTypeCode);

    @Query("SELECT entity " +
            "FROM StaffLabourAgreement entity " +
            "WHERE entity.staff.id = :staffId " +
            "and entity.contractType.code in :contractTypeCodeList " +
            "order by entity.startDate desc, entity.endDate desc ")
    List<StaffLabourAgreement> findLatestLabourByContractTypeCodeListOfStaff(@Param("staffId") UUID staffId,
                                                                         @Param("contractTypeCodeList") List<String> contractTypeCodeList);

    @Query("SELECT NEW com.globits.hr.dto.StaffLabourAgreementDto(labour) FROM StaffLabourAgreement labour " +
            "WHERE (labour.voided is null or labour.voided = false) " +
            "AND labour.staff.id = :staff " +
            "AND labour.startDate = (SELECT MAX(last.startDate) FROM StaffLabourAgreement last WHERE (last.voided is null or last.voided = false) and last.staff.id = :staff)")
    List<LabourAgreementTypeDto> getAllLabourAgreement(UUID staff);
}
