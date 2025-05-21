package com.globits.hr.repository;

import com.globits.hr.domain.HrDepartmentShiftWork;
import com.globits.timesheet.domain.AbsenceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HrDepartmentShiftWorkRepository extends JpaRepository<HrDepartmentShiftWork, UUID> {
    @Query("Select entity from HrDepartmentShiftWork entity " +
            " where entity.department.id = :departmentId ")
    List<HrDepartmentShiftWork> findByDepartmentId(@Param("departmentId") UUID departmentId);

    @Query("Select entity from HrDepartmentShiftWork entity " +
            " where entity.shiftWork.id = :shiftWorkId ")
    List<HrDepartmentShiftWork> findByShiftWorkId(@Param("shiftWorkId") UUID shiftWorkId);

    @Query("select ds from HrDepartmentShiftWork ds " +
            "where ds.department.id = :departmentId and ds.shiftWork.id = :shiftWorkId")
    List<HrDepartmentShiftWork> findByDepartmentIdShiftWorkId(@Param("departmentId") UUID departmentId, @Param("shiftWorkId") UUID shiftWorkId);


    @Query("select ds from HrDepartmentShiftWork ds " +
            "where ds.department.id = :departmentId and ds.shiftWork.code = :shiftWorkCode")
    List<HrDepartmentShiftWork> findByDepartmentIdShiftWorkCode(@Param("departmentId") UUID departmentId, @Param("shiftWorkCode") String shiftWorkCode);
}
