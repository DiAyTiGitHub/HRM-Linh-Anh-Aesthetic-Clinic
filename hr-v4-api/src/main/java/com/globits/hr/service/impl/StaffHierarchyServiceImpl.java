package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.*;
import com.globits.hr.dto.*;
import com.globits.hr.dto.search.SearchHrDepartmentDto;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.hr.dto.search.SearchStaffHierarchyDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.*;
import com.globits.hr.utils.RoleUtils;
import com.globits.security.domain.Role;
import com.globits.security.domain.User;
import com.globits.security.dto.UserDto;

import org.apache.http.HttpStatus;
import org.keycloak.common.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StaffHierarchyServiceImpl extends GenericServiceImpl<Staff, UUID> implements StaffHierarchyService {

    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private StaffService staffService;

    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private PositionTitleRepository positionTitleRepository;
    @Autowired
    private PositionRelationShipRepository positionRelationShipRepository;

    @Autowired
    private UserExtService userExtService;
    @Autowired
    private HRDepartmentRepository departmentRepository;

    @Override
    public Page<StaffDto> pagingUpperLevelStaff(SearchStaffHierarchyDto dto) {
        if (dto == null) {
            return null;
        }
        StaffDto staff = userExtService.getCurrentStaff();
        if (staff == null) return null;

        return null;
    }

    @Override
    public Page<StaffDto> pagingLowerLevelStaff(SearchStaffDto searchStaffDto) {
        if (searchStaffDto == null) {
            return Page.empty();
        }
        if (searchStaffDto.getLevelNumber() != null) {
            return staffService.searchByPage(searchStaffDto);
        } else {
            return Page.empty();
        }
    }

    @Override
    public List<UUID> getManagedStaffByLevel(UUID staffId, List<UUID> staffSupervisorIds, Integer level, Boolean collectInEachLevel) {
        if (staffSupervisorIds == null || staffId == null) return new ArrayList<>();

        Set<UUID> managedStaff = new HashSet<>();

        // Xử lý giá trị null cho level và collectInEachLevel
        if (level == null || level < 1) {
            level = 0; // Nếu level không hợp lệ, không lấy cấp nào
        }
        if (collectInEachLevel == null) {
            collectInEachLevel = false; // Mặc định là false nếu null
        }

        // Chuyển staffSupervisorIds thành List<String> để gọi repository
        List<String> staffSupervisorIdStrings = staffSupervisorIds.stream()
                .map(UUID::toString)
                .collect(Collectors.toList());

        // Trường hợp collectInEachLevel = true: Chỉ lấy nhân viên ở level được chỉ định
        if (collectInEachLevel) {
            List<UUID> currentLevelStaff = new ArrayList<>(staffSupervisorIds);

            // Lặp qua các level để đến đúng level cần lấy
            for (int currentLevel = 1; currentLevel <= level; currentLevel++) {
                if (currentLevelStaff.isEmpty()) break; // Không có nhân viên cấp dưới -> Dừng

                List<String> currentLevelStaffStrings = currentLevelStaff.stream()
                        .map(UUID::toString)
                        .collect(Collectors.toList());
                List<String> nextLevelStaffStr = staffRepository.getManagedStaffList(currentLevelStaffStrings);
                List<UUID> nextLevelStaff = nextLevelStaffStr.stream()
                        .map(UUID::fromString)
                        .collect(Collectors.toList());

                // Nếu đã đến level cần lấy, thêm vào managedStaff và dừng
                if (currentLevel == level) {
                    managedStaff.addAll(nextLevelStaff);
                    break;
                }

                // Chuyển sang level tiếp theo
                currentLevelStaff = nextLevelStaff;
            }
        } else {
            // Trường hợp collectInEachLevel = false: Lấy tất cả staff từ level 1 đến level được chỉ định
            // Thêm staffId vào kết quả
            managedStaff.add(staffId);

            if (level >= 1) {
                List<String> managedStaffList = staffRepository.getManagedStaffList(staffSupervisorIdStrings);
                List<UUID> firstLevelStaff = managedStaffList.stream()
                        .map(UUID::fromString)
                        .collect(Collectors.toList());

                managedStaff.addAll(firstLevelStaff);
                staffSupervisorIds = firstLevelStaff;
            }

            if (level > 1) {
                List<UUID> currentLevelStaff = new ArrayList<>(staffSupervisorIds);

                for (int currentLevel = 2; currentLevel <= level; currentLevel++) {
                    if (currentLevelStaff.isEmpty()) break;

                    List<String> currentLevelStaffStrings = currentLevelStaff.stream()
                            .map(UUID::toString)
                            .collect(Collectors.toList());
                    List<String> nextLevelStaffStr = staffRepository.getManagedStaffList(currentLevelStaffStrings);
                    List<UUID> nextLevelStaff = nextLevelStaffStr.stream()
                            .map(UUID::fromString)
                            .collect(Collectors.toList());

                    managedStaff.addAll(nextLevelStaff);
                    currentLevelStaff = nextLevelStaff;
                }
            }
        }

        return new ArrayList<>(managedStaff);
    }

    @Override
    public List<UUID> getAllManagedStaff(UUID staffId, List<UUID> staffSupervisorIds) {
        if (staffId == null || staffSupervisorIds == null) return new ArrayList<>();

        Set<UUID> managedStaff = new HashSet<>();
        managedStaff.add(staffId);

        if (staffSupervisorIds.isEmpty()) {
            return new ArrayList<>(managedStaff);
        }

        List<String> currentSupervisors = staffSupervisorIds.stream()
                .map(UUID::toString)
                .collect(Collectors.toList());

        while (!currentSupervisors.isEmpty()) {
            List<String> subordinates = staffRepository.getManagedStaffList(currentSupervisors);
            if (subordinates == null || subordinates.isEmpty()) {
                break;
            }

            List<UUID> subordinateUUIDs = subordinates.stream()
                    .map(UUID::fromString)
                    .collect(Collectors.toList());

            managedStaff.addAll(subordinateUUIDs);
            currentSupervisors = subordinates;
        }

        return new ArrayList<>(managedStaff);
    }


    @Autowired
    private HRDepartmentService hrDepartmentService;

    @Autowired
    private HRDepartmentRepository hrDepartmentRepository;

    @Override
    public Page<HRDepartmentDto> pagingHasPermissionDepartments(SearchHrDepartmentDto dto) {
        Page<HRDepartmentDto> resultEmpty = Page.empty();
        if (dto == null) {
            return resultEmpty;
        }
        UserDto userDto = userExtService.getCurrentUser();
        Boolean isAdmin = RoleUtils.hasRoleAdmin(userDto);
        Boolean isManager = RoleUtils.hasRoleHrManager(userDto);
        Boolean isStaffView = RoleUtils.hasRoleHrStaffView(userDto);

        Boolean isAssignment = RoleUtils.hasRoleHrAssignment(userDto);
        Staff staff = userExtService.getCurrentStaffEntity();
        Boolean isShiftAssignment = RoleUtils.hasShiftAssignmentPermission(staff);

        if (isAdmin || isManager || isStaffView) {
            return hrDepartmentService.pagingDepartments(dto);
        } else if (isAssignment && isShiftAssignment) {
            if (staff == null || staff.getId() == null) {
                return resultEmpty;
            }
            List<UUID> departmentIdsManagedByStaff = this.getAllManagedAndSubDepartmentIdsByStaff(staff.getId());
            // Cập nhật DTO để lọc theo danh sách phòng ban có quyền
            dto.setDepartmentIdList(departmentIdsManagedByStaff);

            return hrDepartmentService.pagingDepartments(dto);
        } else {
            return resultEmpty;
        }
    }
    
    @Override
    public List<UUID> getAllManagedAndSubDepartmentIdsByStaff(UUID staffId) {
        if (staffId == null) {
            return Collections.emptyList();
        }

        // Lấy danh sách phòng ban mà nhân viên làm trưởng phòng
        List<HRDepartment> departmentsManagedByStaff = hrDepartmentRepository.findByStaffId(staffId);
        if (departmentsManagedByStaff.isEmpty()) {
            return Collections.emptyList();
        }

        // Lấy danh sách ID của phòng ban chính
        List<UUID> departmentIdsManagedByStaff = departmentsManagedByStaff.stream()
                .map(HRDepartment::getId)
                .collect(Collectors.toList());

        // Lấy tất cả phòng ban cấp dưới
        List<HRDepartment> allSubDepartments = this.getAllSubDepartments(departmentIdsManagedByStaff);
        if (allSubDepartments != null && !allSubDepartments.isEmpty()) {
            departmentIdsManagedByStaff.addAll(
                    allSubDepartments.stream().map(HRDepartment::getId).collect(Collectors.toList())
            );
        }

        return departmentIdsManagedByStaff;
    }

    private List<HRDepartment> getAllSubDepartments(List<UUID> departmentIds) {
        List<HRDepartment> allSubDepartments = new ArrayList<>();
        findSubDepartmentsRecursive(departmentIds, allSubDepartments);
        return allSubDepartments;
    }

    private void findSubDepartmentsRecursive(List<UUID> parentIds, List<HRDepartment> allSubDepartments) {
        if (parentIds.isEmpty()) return;

        // Tìm tất cả các phòng ban con của danh sách parentIds
        List<HRDepartment> subDepartments = hrDepartmentRepository.findByParentIdList(parentIds);
        if (subDepartments.isEmpty()) return;

        allSubDepartments.addAll(subDepartments);

        // Gọi đệ quy tiếp để tìm cấp dưới nữa
        List<UUID> subDepartmentIds = subDepartments.stream()
                .map(HRDepartment::getId)
                .collect(Collectors.toList());

        findSubDepartmentsRecursive(subDepartmentIds, allSubDepartments);
    }

    public ApiResponse<Position> getManagerPosition(UUID departmentId, String role) {
        if (departmentId != null) {
            HRDepartment department = departmentRepository.findById(departmentId).orElse(null);
            if (department != null) {
                Position managePosition = department.getPositionManager();
                if (managePosition != null) {
                    Staff manager = managePosition.getStaff();
                    if (manager != null) {
                        User user = manager.getUser();
                        if (user != null) {
                            if (!CollectionUtil.isEmpty(user.getRoles()) && user.getRoles().stream().map(Role::getName).toList().contains(role)) {
                                return new ApiResponse<>(HttpStatus.SC_OK, "OK", managePosition);
                            } else {
                                if (department.getParent() != null) {
                                    return getManagerPosition(department.getParent().getId(), role);
                                }
                            }
                        } else {
                            if (department.getParent() != null) {
                                return getManagerPosition(department.getParent().getId(), role);
                            }
                        }
                    } else {
                        if (department.getParent() != null) {
                            // vị trí quản lý chưa có ai đảm nhận thì tìm tiếp thằng phòng quản lý của phòng ban cấp cha
                            return getManagerPosition(department.getParent().getId(), role);
                        } else {
                            return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Phòng ban " + department.getName() + " không có vị trí quản lý và cũng không có phòng ban cấp trên", null);
                        }
                    }
                } else {
                    if (department.getParent() != null) {
                        // phòng ban chưa có vị trí quản lý nên tìm vị trí quản lý của phòng ban cha
                        return getManagerPosition(department.getParent().getId(), role);
                    } else {
                        return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Không tìm thấy phòng ban cấp trên", null);
                    }
                }
            } else {
                return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Không tìm thấy phòng ban!", null);
            }
        }
        return new ApiResponse<>(HttpStatus.SC_NOT_FOUND, "Không tìm thấy phòng ban", null);
    }
    
}
