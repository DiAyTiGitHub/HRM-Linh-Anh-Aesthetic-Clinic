package com.globits.hr.service.impl;

import com.globits.hr.HrConstants;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.repository.PositionRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.HrRoleService;
import com.globits.hr.service.StaffService;
import com.globits.hr.service.UserExtService;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;
import com.globits.security.repository.RoleRepository;
import com.globits.security.repository.UserRepository;
import com.globits.security.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HrRoleServiceImpl implements HrRoleService {
    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StaffService staffService;

    @Autowired
    private UserExtService userExtService;

    @Autowired
    private UserService userService;

    @Autowired
    private PositionRepository positionRepository;


    // CÓ ROLE ADMIN
    // Kiểm tra nhân viên có role Admin hay không
    @Override
    public boolean hasRoleAdmin(UUID staffId) {
        Staff staff = staffRepository.findById(staffId).orElse(null);
        if (staff == null || staff.getUser() == null) return false;

        UserDto user = new UserDto(staff.getUser());

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return false;
        }

        for (RoleDto item : user.getRoles()) {
            if (item.getName() != null
                    && (HrConstants.ROLE_ADMIN.equals(item.getName())
                    || HrConstants.ROLE_SUPER_ADMIN.equals(item.getName()))) {
                return true;
            }
        }

        return false;
    }

    // Kiểm tra tài khoản có role Admin hay không
    @Override
    public boolean hasRoleAdmin(Long userId) {
        UserDto user = userService.findByUserId(userId);
        if (user == null || user.getRoles() == null || user.getRoles().isEmpty()) {
            return false;
        }

        for (RoleDto item : user.getRoles()) {
            if (item.getName() != null
                    && (HrConstants.ROLE_ADMIN.equals(item.getName())
                    || HrConstants.ROLE_SUPER_ADMIN.equals(item.getName()))) {
                return true;
            }
        }

        return false;
    }

    // Kiểm tra người dùng hiện tại có role Admin hay không
    @Override
    public boolean hasRoleAdmin() {
        UserDto user = userExtService.getCurrentUser();
        if (user == null || user.getRoles() == null || user.getRoles().isEmpty()) {
            return false;
        }

        for (RoleDto item : user.getRoles()) {
            if (item.getName() != null
                    && (HrConstants.ROLE_ADMIN.equals(item.getName())
                    || HrConstants.ROLE_SUPER_ADMIN.equals(item.getName()))) {
                return true;
            }
        }

        return false;
    }


    // CÓ ROLE MANAGER
    // Kiểm tra nhân viên có role Manager hay không
    @Override
    public boolean hasRoleManager(UUID staffId) {
        Staff staff = staffRepository.findById(staffId).orElse(null);
        if (staff == null || staff.getUser() == null) return false;

        UserDto user = new UserDto(staff.getUser());

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return false;
        }

        for (RoleDto item : user.getRoles()) {
            if (item.getName() != null
                    && (HrConstants.HR_MANAGER.equals(item.getName()))) {
                return true;
            }
        }

        return false;
    }

    // Kiểm tra tài khoản có role Manager hay không
    @Override
    public boolean hasRoleManager(Long userId) {
        UserDto user = userService.findByUserId(userId);
        if (user == null || user.getRoles() == null || user.getRoles().isEmpty()) {
            return false;
        }

        for (RoleDto item : user.getRoles()) {
            if (item.getName() != null
                    && (HrConstants.HR_MANAGER.equals(item.getName()))) {
                return true;
            }
        }

        return false;
    }

    // Kiểm tra người dùng hiện tại có role Manager hay không
    @Override
    public boolean hasRoleManager() {
        UserDto user = userExtService.getCurrentUser();
        if (user == null || user.getRoles() == null || user.getRoles().isEmpty()) {
            return false;
        }

        for (RoleDto item : user.getRoles()) {
            if (item.getName() != null
                    && (HrConstants.HR_MANAGER.equals(item.getName()))) {
                return true;
            }
        }

        return false;
    }


    // CÓ ROLE QUẢN LÝ PHÂN CA
    // Kiểm tra nhân viên có role HrAssignment hay không
    @Override
    public boolean hasRoleHrAssignment(UUID staffId) {
        Staff staff = staffRepository.findById(staffId).orElse(null);
        if (staff == null || staff.getUser() == null) return false;

        UserDto user = new UserDto(staff.getUser());

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return false;
        }

        for (RoleDto item : user.getRoles()) {
            if (item.getName() != null && (HrConstants.HR_ASSIGNMENT_ROLE.equals(item.getName()))) {
                return true;
            }
        }

        return false;
    }

    // Kiểm tra tài khoản có role HrAssignment hay không
    @Override
    public boolean hasRoleHrAssignment(Long userId) {
        UserDto user = userService.findByUserId(userId);
        if (user == null || user.getRoles() == null || user.getRoles().isEmpty()) {
            return false;
        }

        for (RoleDto item : user.getRoles()) {
            if (item.getName() != null && (HrConstants.HR_ASSIGNMENT_ROLE.equals(item.getName()))) {
                return true;
            }
        }

        return false;
    }

    // Kiểm tra người dùng hiện tại có role HrAssignment hay không
    @Override
    public boolean hasRoleHrAssignment() {
        UserDto user = userExtService.getCurrentUser();
        if (user == null || user.getRoles() == null || user.getRoles().isEmpty()) {
            return false;
        }

        for (RoleDto item : user.getRoles()) {
            if (item.getName() != null && (HrConstants.HR_ASSIGNMENT_ROLE.equals(item.getName()))) {
                return true;
            }
        }

        return false;
    }


    // CÓ LÀ TRƯỞNG PHÒNG HAY KHÔNG
    // Kiểm tra nhân viên có là trưởng phòng hay không
    public boolean isHeadOfDepartment(UUID staffId) {
        Staff staff = staffRepository.findById(staffId).orElse(null);

        if (staff == null || staff.getCurrentPositions() == null || staff.getCurrentPositions().isEmpty())
            return false;

        for (Position position : staff.getCurrentPositions()) {
            if (position.getDepartment() == null || position.getDepartment().getPositionManager() == null) continue;

            if (position.getDepartment().getPositionManager().getId().equals(position.getId())) return true;
        }

        return false;
    }

    // Kiểm tra người dùng hiện tại có là trưởng phòng hay không
    public boolean isHeadOfDepartment() {
        Staff staff = userExtService.getCurrentStaffEntity();

        if (staff == null || staff.getCurrentPositions() == null || staff.getCurrentPositions().isEmpty())
            return false;

        for (Position position : staff.getCurrentPositions()) {
            if (position.getDepartment() == null || position.getDepartment().getPositionManager() == null) continue;

            if (position.getDepartment().getPositionManager().getId().equals(position.getId())) return true;
        }
        return false;
    }


    // KIỂM TRA CÓ ĐƯỢC PHÉP PHÂN CA CHO NHÂN VIÊN HAY KHÔNG
    // Kiểm tra nhân viên có được phép phân ca cho nhân viên hay không
    public boolean hasShiftAssignmentPermission(UUID staffId) {
        if (hasRoleAdmin(staffId)) return true;

        if (hasRoleHrAssignment(staffId) && isHeadOfDepartment(staffId)) {
            return true;
        }

        return false;
    }

    // Kiểm tra người dùng hiện tại có được phép phân ca cho nhân viên hay không
    public boolean hasShiftAssignmentPermission() {
        if (hasRoleAdmin() || hasRoleManager()) return true;

        if (hasRoleHrAssignment() && isHeadOfDepartment()) {
            return true;
        }

        return false;
    }

    @Override
    public boolean isDirectManager(Long userId) {
        if (userId == null) return false;
        List<StaffDto> staff = positionRepository.getListStaffUnderManager(userId);
        if (!CollectionUtils.isEmpty(staff)) {
            return true;
        }
        return false;
    }
}
