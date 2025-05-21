package com.globits.salary.repository;

import com.globits.salary.domain.StaffSalaryTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffSalaryTemplateRepository extends JpaRepository<StaffSalaryTemplate, UUID> {

    @Query("SELECT sst FROM StaffSalaryTemplate sst " +
		   "WHERE sst.staff.id = :staffId ")
	List<StaffSalaryTemplate> findByStaffId(@Param("staffId") UUID staffId);

    @Query("select sst FROM StaffSalaryTemplate sst " +
            "where sst.salaryTemplate.id = :salaryTemplateId ")
    List<StaffSalaryTemplate> findBySalaryTemplateId(@Param("salaryTemplateId") UUID salaryTemplateId);

	@Query("select sst from StaffSalaryTemplate sst " +
            "where sst.staff.id = :staffId " +
            "and sst.salaryTemplate.code = 'ML_02_V2'")
	List<StaffSalaryTemplate> findTaxByStaffId(UUID staffId);

	@Query("select entity from StaffSalaryTemplate entity " +
            "where entity.salaryTemplate.id = :salaryTemplateId " +
            "and entity.staff.id = :staffId ")
	List<StaffSalaryTemplate> findBySalaryTemplateIdAndStaffId(@Param("salaryTemplateId") UUID salaryTemplateId, @Param("staffId") UUID staffId);
}
