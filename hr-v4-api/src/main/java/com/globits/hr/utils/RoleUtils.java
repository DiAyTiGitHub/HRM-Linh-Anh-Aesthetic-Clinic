package com.globits.hr.utils;

import java.util.Objects;
import java.util.Set;

import com.globits.hr.HrConstants;
import com.globits.hr.domain.Staff;
import com.globits.hr.utils.Const.HR_DEPARTMENT_TYPE_ENUM;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;

public class RoleUtils {
    private static final Set<String> ADMIN_ROLES = Set.of(
        HrConstants.ROLE_SUPER_ADMIN,
        HrConstants.ROLE_ADMIN
    );
    
    private static final Set<String> ASSIGNMENT_ROLES = Set.of(
        HrConstants.HR_ASSIGNMENT_ROLE
    );
    
    private static final Set<String> USER_ROLES = Set.of(
        HrConstants.HR_USER, 
        HrConstants.ROLE_USER
    );
    
    // ADMIN
    public static boolean hasRoleAdmin(UserDto userDto) {
        return hasAnyRole(userDto, ADMIN_ROLES);
    }

    public static boolean hasRoleAdmin(Staff staff) {
        return staff != null && staff.getUser() != null && 
               hasAnyRole(new UserDto(staff.getUser()), ADMIN_ROLES);
    }
    
    // MANAGER
    public static boolean hasRoleHrManager(UserDto userDto) {
        return hasRole(userDto, HrConstants.HR_MANAGER);
    }

    // ASSIGNMENT
    public static boolean hasRoleHrAssignment(UserDto userDto) {
        return hasAnyRole(userDto, ASSIGNMENT_ROLES);
    }

    public static boolean hasRoleHrAssignment(Staff staff) {
        return staff != null && staff.getUser() != null && 
               hasAnyRole(new UserDto(staff.getUser()), ASSIGNMENT_ROLES);
    }
    
    // USER
    public static boolean hasRoleUser(UserDto userDto) {
        return hasAnyRole(userDto, USER_ROLES);
    }
    
    // HR_STAFF_VIEW
    public static boolean hasRoleHrStaffView(UserDto userDto) {
        return hasRole(userDto, HrConstants.HR_STAFF_VIEW);
    }
    
//    public static boolean isHrUser(UserDto userDto) {
//        return hasRole(userDto, HrConstants.HR_USER);
//    }
//    public static boolean isRoleUser(UserDto userDto) {
//        return hasRole(userDto, HrConstants.ROLE_USER);
//    }
    
    // RECRUITMENT
    public static boolean hasRoleHrRecruitment(UserDto userDto) {
        return hasRole(userDto, HrConstants.HR_RECRUITMENT);
    }
    
    // HEAD OF DEPARTMENT CHECKS
    public static boolean isHeadOfDepartment(Staff staff) {
        if (staff == null || staff.getCurrentPositions() == null || 
            staff.getCurrentPositions().isEmpty()) {
            return false;
        }

        return staff.getCurrentPositions().stream().anyMatch(position -> 
            position.getDepartment() != null && 
            position.getDepartment().getPositionManager() != null && 
            position.getDepartment().getPositionManager().getId().equals(position.getId())
        );
    }

    // SHIFT ASSIGNMENT PERMISSION CHECKS
    public static boolean hasShiftAssignmentPermission(UserDto user, Staff staff) {
        if (hasRoleAdmin(user)) {
            return true;
        }
        return hasRoleHrAssignment(user) && isHeadOfDepartment(staff);
    }

    public static boolean hasShiftAssignmentPermission(Staff staff) {
        if (hasRoleAdmin(staff)) {
            return true;
        }
        return hasRoleHrAssignment(staff) && isHeadOfDepartment(staff);
    }

    // check any role
    private static boolean hasAnyRole(UserDto userDto, Set<String> roles) {
        if (userDto == null || userDto.getRoles() == null || userDto.getRoles().isEmpty()) {
            return false;
        }
        return userDto.getRoles().stream()
            .filter(Objects::nonNull)
            .map(RoleDto::getName)
            .filter(Objects::nonNull)
            .anyMatch(roles::contains);
    }
    
    // check a role
    public static boolean hasRole(UserDto userDto, String role) {
        if (userDto == null || userDto.getRoles() == null || userDto.getRoles().isEmpty()) {
            return false;
        }
        return userDto.getRoles().stream()
            .filter(Objects::nonNull)
            .map(RoleDto::getName)
            .filter(Objects::nonNull)
            .anyMatch(role::equals);
    }
}
