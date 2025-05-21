package com.globits.timesheet.repository;

import com.globits.timesheet.domain.LeaveRequest;
import com.globits.timesheet.domain.ShiftChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftChangeRequestRepository extends JpaRepository<ShiftChangeRequest, UUID> {
    //    @Query("select lt from LeaveType lt where lt.code = :code")
//    List<LeaveType> findByCode(@Param("code") String code);
    @Query("SELECT scr FROM ShiftChangeRequest scr " +
            "WHERE scr.fromShiftWork.id = :fromShiftWorkId " +
            "AND DATE(scr.fromWorkingDate) = DATE(:fromWorkingDate) " +  // Chỉ so sánh ngày
            "AND scr.registerStaff.id = :registerStaffId " +
            "AND scr.approvalStatus IN :approvalStatus")
    List<ShiftChangeRequest> findByShiftWorkAndDateAndStaffAndStatus(
            @Param("fromShiftWorkId") UUID fromShiftWorkId,
            @Param("fromWorkingDate") Date fromWorkingDate,  // Vẫn truyền vào đầy đủ timestamp
            @Param("registerStaffId") UUID registerStaffId,
            @Param("approvalStatus") List<Integer> approvalStatus);

}
