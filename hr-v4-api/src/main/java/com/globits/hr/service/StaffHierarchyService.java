package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.HRDepartment;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.*;
import com.globits.hr.dto.search.SearchHrDepartmentDto;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.hr.dto.search.SearchStaffHierarchyDto;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

@Service
public interface StaffHierarchyService extends GenericService<Staff, UUID> {
    // Lấy danh sách nhân viên ở cấp bậc PHÍA TRÊN so với nhân viên hiện tại
    Page<StaffDto> pagingUpperLevelStaff(SearchStaffHierarchyDto dto);

    // Lấy danh sách nhân viên ở cấp bậc PHÍA DƯỚI so với nhân viên hiện tại
    Page<StaffDto> pagingLowerLevelStaff(SearchStaffDto dto);

    // Lấy danh sách id của nhân viên được quản lý
    List<UUID> getAllManagedStaff(UUID staffId, List<UUID> staffSupervisorIds);

    List<UUID> getManagedStaffByLevel(UUID staffId, List<UUID> staffSupervisorIds, Integer level,
                                      Boolean collectInEachLevel);

    // Lấy danh sách phòng ban nhân viên có quyền thao tác
    Page<HRDepartmentDto> pagingHasPermissionDepartments(SearchHrDepartmentDto dto);
    
    ApiResponse<Position> getManagerPosition(UUID departmentId, String role);

	List<UUID> getAllManagedAndSubDepartmentIdsByStaff(UUID staffId);

}
