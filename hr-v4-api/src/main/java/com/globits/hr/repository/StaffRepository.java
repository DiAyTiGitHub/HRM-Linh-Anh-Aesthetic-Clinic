package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.Staff;
import com.globits.hr.dto.StaffDto;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {
	@Query("select new com.globits.hr.dto.StaffDto(s.id, s.staffCode, s.displayName, s.gender) from Staff s")
	Page<StaffDto> findByPageBasicInfo(Pageable pageable);

	@Query("select new com.globits.hr.dto.StaffDto(u, true) from Staff u where u.staffCode = ?1")
	StaffDto getByUsername(String username);

	@Query("select s from Staff s where s.user.username = ?1")
	Staff findByUsername(String username);

	@Query("select u from Staff u where u.staffCode = ?1")
	List<Staff> findByCode(String staffCode);

	@Query("select u from Staff u where u.taxCode = ?1")
	List<Staff> findByTaxCode(String taxCode);

	@Query("select u from Staff u where u.socialInsuranceNumber = ?1")
	List<Staff> findBySocialInsuranceNumber(String socialInsuranceNumber);

	@Query("select u from Staff u where u.healthInsuranceNumber = ?1")
	List<Staff> findByHealthInsuranceNumber(String healthInsuranceNumber);

	@Query("select new com.globits.hr.dto.StaffDto(s) from Staff s where s.staffCode like %?1% or s.displayName like %?1%")
	Page<StaffDto> findPageByCodeOrName(String staffCode, Pageable pageable);

	@Query("select new com.globits.hr.dto.StaffDto(s) from Staff s where s.staffCode= ?1")
	List<StaffDto> findDtoByCode(String staffCode);
	
	@Query("select u from Staff u where u.staffCode = ?1")
	List<Staff> getByCode(String staffCode);

	@Query("select u from Staff u where u.id = ?1")
	Staff findOneById(UUID id);

	@Query("select count(u.id) from Staff u where u.idNumber = ?2 and (u.id <> ?1 or ?1 is null )")
	Long countByIdNumber(UUID id, String idNumber);

	@Query("select new com.globits.hr.dto.StaffDto(u) from Staff u where u.id IN :idList")
	List<StaffDto> getAllByStaffId(List<UUID> idList);

	// tìm kiếm nhân viên theo tháng
	// TT01 là trạng thái đang làm việc
	@Query("SELECT distinct new com.globits.hr.dto.StaffDto(s, false) FROM Staff s "
			+ "WHERE FUNCTION('MONTH', s.birthDate) = :month AND s.status.code = 'TT01'")
	List<StaffDto> findStaffsHaveBirthDayByMonth(int month);

	@Query("select distinct staff from Staff staff " + "where staff.user.id = :userId")
	List<Staff> getStaffByUserAccountId(@Param("userId") Long userId);

	// lấy ra những nhân viên có mẫu bảng lương (salaryTemplateId) trong hợp đồng và
	// chưa có trong kỳ lương (salaryPeriodId)
	// TT01 là trạng thái đang làm việc
	@Query("SELECT distinct new com.globits.hr.dto.StaffDto(s, false) FROM Staff s "
			+ "WHERE (1=1) AND s.status.code = 'TT01' " + "AND s.id IN ( "
			+ "SELECT sla.staff.id from StaffLabourAgreement sla " + "WHERE sla.salaryTemplate.id = :templateId " + ") "
			+ "AND s.id NOT IN ( " + "SELECT srs.staff.id from SalaryResultStaff srs "
			+ "WHERE srs.salaryResult.salaryPeriod.id = :periodId " + ") ")
	List<StaffDto> findBySalaryTemplatePeriod(UUID templateId, UUID periodId);

	@Query("SELECT new com.globits.hr.dto.StaffDto(SUM(s.staffInsuranceAmount), "
			+ "SUM(s.orgInsuranceAmount), SUM(s.insuranceSalary), SUM(s.unionDuesAmount)) FROM Staff s "
			+ "where s.hasSocialIns is not null " + "and s.hasSocialIns = true ")
	StaffDto getTotalInsuranceAmounts();

	@Query("select new com.globits.hr.dto.StaffDto(u) from Staff u where u.phoneNumber = ?1 or u.Email = ?2")
	List<StaffDto> getByPhoneNumberOrEmail(String phoneNumber, String email);

	@Query(value = """
			   SELECT
			    s.id AS staffId,
			    CASE WHEN au.level = :communeLevel THEN au.code ELSE NULL END AS communeCode,
			    CASE WHEN au.level = :communeLevel THEN au.name ELSE NULL END AS communeName,
			    CASE
			        WHEN au.level = :communeLevel THEN parent1.code
			        WHEN au.level = :districtLevel THEN au.code
			        ELSE NULL
			    END AS districtCode,
			    CASE
			        WHEN au.level = :communeLevel THEN parent1.name
			        WHEN au.level = :districtLevel THEN au.name
			        ELSE NULL
			    END AS districtName,
			    CASE
			        WHEN au.level = :communeLevel THEN parent2.code
			        WHEN au.level = :districtLevel THEN parent1.code
			        WHEN au.level = :provinceLevel THEN au.code
			        ELSE NULL
			    END AS provinceCode,
			    CASE
			        WHEN au.level = :communeLevel THEN parent2.name
			        WHEN au.level = :districtLevel THEN parent1.name
			        WHEN au.level = :provinceLevel THEN au.name
			        ELSE NULL
			    END AS provinceName
			FROM tbl_staff s
			INNER JOIN tbl_administrative_unit au
			    ON s.administrativeUnit_id = au.id
			LEFT JOIN tbl_administrative_unit parent1
			    ON au.parent_id = parent1.id
			LEFT JOIN tbl_administrative_unit parent2
			    ON parent1.parent_id = parent2.id
			WHERE
			    (au.level = :communeLevel AND parent1.id IS NOT NULL AND parent2.id IS NOT NULL)
			    OR (au.level = :districtLevel AND parent1.id IS NOT NULL)
			    OR (au.level = :provinceLevel)
			   """, nativeQuery = true)
	List<Object[]> findListPermanentAddress(@Param("communeLevel") Integer communeLevel,
			@Param("districtLevel") Integer districtLevel, @Param("provinceLevel") Integer provinceLevel);

	@Query(value = """
			   SELECT
			   s.id AS staffId,
			    COUNT(s.id) AS numberOfDependents
			FROM tbl_staff s
			LEFT JOIN tbl_staff_family_relationship r
			    ON r.staff_id = s.id
			WHERE s.id IS NOT NULL
			  AND r.is_dependent = TRUE
			GROUP BY s.id
			HAVING COUNT(s.id) > 0
			   """, nativeQuery = true)
	List<Object[]> findListNumberOfDependents();

	@Query(value = """
			SELECT DISTINCT s.id
			FROM tbl_staff s
			JOIN tbl_position p ON s.id = p.staff_id
			JOIN tbl_position_relation_ship rel ON p.id = rel.position_id
			AND rel.relationship_type IN (3)
			JOIN tbl_position supervisor ON supervisor.id = rel.supervisor_id
			JOIN tbl_staff staffSupervisor ON staffSupervisor.id = supervisor.staff_id
			WHERE staffSupervisor.id IN (:staffSupervisorIds)
			""", nativeQuery = true)
	List<String> getManagedStaffList(@Param("staffSupervisorIds") List<String> staffSupervisorIds);

	@Query("FROM Staff staff WHERE staff.user.id = :user")
	Staff findByUserId(@Param("user") Long userId);

	@Query("SELECT s.staffCode FROM Staff s WHERE s.staffCode LIKE CONCAT(:prefix, '%') ORDER BY s.staffCode DESC LIMIT 1")
	String findMaxStaffCodeStartingWith(@Param("prefix") String prefix);

	// Ví dụ: tìm max mã nhân viên có dạng LAxxxxxx_yyyyyy
	@Query(value = """
    SELECT staff_code
    FROM tbl_staff
    WHERE staff_code LIKE 'LA%_____%'
      AND staff_code REGEXP '^LA[0-9]{4}_[0-9]{6}$'
    ORDER BY CAST(SUBSTRING_INDEX(staff_code, '_', -1) AS UNSIGNED) DESC
    LIMIT 1
    """, nativeQuery = true)
	String findMaxValidStaffCode();



	@Query("select u from Staff u where u.idNumber = ?1")
	List<Staff> findByIdNumber(String idNumber);

	@Query("select u from Staff u where u.personalIdentificationNumber = ?1")
	List<Staff> findByIdPersonalIdentificationNumber(String personalIdentificationNumber);
	
	@Query("select new com.globits.hr.dto.StaffDto(u) from Staff u where 1=1 ")
	List<StaffDto> findAllDtos();
}
