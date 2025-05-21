package com.globits.hr.repository;

import com.globits.hr.domain.StaffAllowance;
import com.globits.hr.dto.StaffAllowanceDto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffAllowanceRepository extends JpaRepository<StaffAllowance, UUID> {
    @Query("select new com.globits.hr.dto.StaffAllowanceDto(entity) from StaffAllowance entity where entity.staff.id =?1")
    List<StaffAllowanceDto> findByStaffId(UUID staffId);

    @Query("select entity from StaffAllowance entity where entity.allowance.id =?1")
    List<StaffAllowance> findByAllowanceId(UUID allowanceId);

    @Query("select entity from StaffAllowance entity where entity.staff.id =:staffId and entity.allowance.id =:allowanceId")
    List<StaffAllowance> findByStaffIdAndAllowanceId(UUID staffId, UUID allowanceId);
}
