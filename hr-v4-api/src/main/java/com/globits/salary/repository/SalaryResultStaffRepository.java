package com.globits.salary.repository;

import com.globits.hr.domain.WorkingStatus;
import com.globits.salary.domain.SalaryResult;
import com.globits.salary.domain.SalaryResultStaff;
import com.globits.salary.dto.SalaryResultStaffDto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SalaryResultStaffRepository extends JpaRepository<SalaryResultStaff, UUID> {
    @Query("select srs from SalaryResultStaff srs " +
            "where srs.salaryResult.id = :salaryResultId and srs.staff.id = :staffId")
    List<SalaryResultStaff> findBySalaryResultIdAndStaffId(@Param("salaryResultId") UUID salaryResultId, @Param("staffId") UUID staffId);

    @Query("select srs from SalaryResultStaff srs " +
            "where srs.salaryResult.id = :salaryResultId " +
            "order by srs.displayOrder ")
    List<SalaryResultStaff> getAllBySalaryResultId(@Param("salaryResultId") UUID salaryResultId);

    @Query("select new com.globits.salary.dto.SalaryResultStaffDto(entity) from SalaryResultStaff entity where entity.salaryPeriod.id =?1 and entity.salaryTemplate.id =?2 and (entity.voided is null or entity.voided = false)")
    List<SalaryResultStaffDto> getByPeriodIdAndTemplateId(@Param("salaryPeriodId") UUID salaryPeriodId, @Param("salaryTemplateId") UUID salaryTemplateId);

    @Query("select new com.globits.salary.dto.SalaryResultStaffDto(entity) from SalaryResultStaff entity where entity.staff.id =?1 and entity.salaryPeriod.id =?2 and (entity.voided is null or entity.voided = false)")
    List<SalaryResultStaffDto> getByStaffIdAndPeriodId(@Param("staffId") UUID staffId, @Param("salaryPeriodId") UUID salaryPeriodId);

    @Query("select entity from SalaryResultStaff entity " +
            "where entity.staff.id = :staffId " +
            "and entity.salaryPeriod.id = :salaryPeriodId " +
            "and entity.salaryTemplate.id = :salaryTemplateId " +
            "and (entity.voided is null or entity.voided = false)")
    List<SalaryResultStaff> findByStaffIdPeriodIdAndTemplateId(@Param("staffId") UUID staffId, @Param("salaryPeriodId") UUID salaryPeriodId, @Param("salaryTemplateId") UUID salaryTemplateId);

    @Query("select entity from SalaryResultStaff entity " +
            "where entity.staff.staffCode = :staffCode " +
            "and entity.salaryPeriod.code = :periodCode " +
            "and entity.salaryTemplate.code = :templateCode " +
            "and (entity.voided is null or entity.voided = false)")
    List<SalaryResultStaff> findByStaffCodePeriodCodeAndTemplateCode(@Param("staffCode") String staffCode,
                                                                     @Param("periodCode") String periodCode,
                                                                     @Param("templateCode") String templateCode);


    @Query("select entity from SalaryResultStaff entity " +
            " where entity.salaryPeriod.id = :salaryPeriodId " +
            "and entity.salaryTemplate.id = :salaryTemplateId " +
            "and entity.salaryResult.id is null " +
            "and (entity.voided is null or entity.voided = false) ")
    List<SalaryResultStaff> findOrphanedPayslipsByPeriodIdAndTemplateId(@Param("salaryPeriodId") UUID salaryPeriodId,
                                                 @Param("salaryTemplateId") UUID salaryTemplateId);


}

