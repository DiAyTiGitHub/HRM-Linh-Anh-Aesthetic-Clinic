package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import com.globits.hr.domain.HRDepartment;
import com.globits.hr.dto.HRDepartmentDto;

@Repository
public interface HRDepartmentRepository extends JpaRepository<HRDepartment, UUID> {
    @Query("select count(entity.id) from HRDepartment entity where entity.code =?1 and (entity.id <> ?2 or ?2 is null) ")
    Long checkCode(String code, UUID id);

    @Query("select new com.globits.hr.dto.HRDepartmentDto(ed) from HRDepartment ed")
    Page<HRDepartmentDto> getListPage(Pageable pageable);

    @Query("select c FROM HRDepartment c where c.code = ?1")
    List<HRDepartment> findByCode(String code);

    @Query("select count(c.id) FROM HRDepartment c where c.parent.id = ?1")
    Long countDepartment(UUID id);

    @Query("select count(c.id) FROM Staff c where c.department.id = ?1")
    Long countStaff(UUID id);

    @Query("select c FROM HRDepartment c where c.name = ?1")
    List<HRDepartment> findByName(String name);

    @Query("select c FROM HRDepartment c where c.parent.id = ?1")
    List<HRDepartment> findByParentId(UUID id);
    
    @Query("select c FROM HRDepartment c where c.parent.id IN (?1)")
    List<HRDepartment> findByParentIdList(List<UUID> id);
    
    @Query("select entity FROM HRDepartment entity "
    		+ " JOIN entity.positionManager positionManager "
    		+ " JOIN positionManager.staff staff ON positionManager.isMain = true "
    		+ " WHERE staff.id = ?1 ")
    List<HRDepartment> findByStaffId(UUID id);
    
    // Trả về Map<StaffId, DepartmentId>
    @Query("SELECT p.staff.id, d.id FROM HRDepartment d JOIN d.positions p " +
           "WHERE p.isMain = true AND p.staff.id IN (:staffIds)")
    List<Object[]> getMainDepartmentIdMapByStaffIds(@Param("staffIds") List<UUID> staffIds);

    // Trả về Map<StaffId, DepartmentId>
    @Query("SELECT p.staff.id, d.id FROM HRDepartment d JOIN d.positions p " +
            "WHERE  p.staff.id IN (:staffIds)")
    List<Object[]> getDepartmentIdMapByStaffIds(@Param("staffIds") List<UUID> staffIds);

    // Trả về Map<ShiftWorkId, DepartmentId>
    @Query("SELECT dsw.shiftWork.id, d.id FROM HRDepartment d JOIN d.departmentShiftWorks dsw " +
           "WHERE dsw.shiftWork.id IN (:shiftWorkIds)")
    List<Object[]> getShiftWorkAndDepartmentPairs(@Param("shiftWorkIds") List<UUID> shiftWorkIds);


    @Query(value = "SELECT code FROM tbl_department WHERE code LIKE CONCAT(:prefix, '_%') ORDER BY CAST(SUBSTRING(code, LENGTH(:prefix) + :zeroPadding) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String findMaxCodeByPrefix(@Param("prefix") String prefix, @Param("zeroPadding") Integer zeroPadding);
}
