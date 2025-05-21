package com.globits.timesheet.repository;

import com.globits.timesheet.domain.LeaveType;
import com.globits.timesheet.dto.LeaveTypeDto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, UUID> {
    @Query("select lt from LeaveType lt where trim(lt.code) = trim(:code)")
    List<LeaveType> findByCode(@Param("code") String code);
    
    @Query("select new com.globits.timesheet.dto.LeaveTypeDto(entity) from LeaveType entity ORDER BY entity.code ")
    List<LeaveTypeDto> getListLeaveTypeDto();
}
