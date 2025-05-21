package com.globits.hr.service;

import java.util.UUID;

public interface HrRoleService {
    // CÓ ROLE ADMIN
    // Kiểm tra nhân viên có role Admin hay không
    boolean hasRoleAdmin(UUID staffId);

    // Kiểm tra tài khoản có role Admin hay không
    boolean hasRoleAdmin(Long userId);

    // Kiểm tra người dùng hiện tại có role Admin hay không
    boolean hasRoleAdmin();


    // CÓ ROLE MANAGER
    // Kiểm tra nhân viên có role Manager hay không
    boolean hasRoleManager(UUID staffId);

    // Kiểm tra tài khoản có role Manager hay không
    boolean hasRoleManager(Long userId);

    // Kiểm tra người dùng hiện tại có role Manager hay không
    boolean hasRoleManager();


    // CÓ ROLE QUẢN LÝ PHÂN CA
    // Kiểm tra nhân viên có role HrAssignment hay không
    boolean hasRoleHrAssignment(UUID staffId);

    // Kiểm tra tài khoản có role HrAssignment hay không
    boolean hasRoleHrAssignment(Long userId);

    // Kiểm tra người dùng hiện tại có role HrAssignment hay không
    boolean hasRoleHrAssignment();


    // CÓ LÀ TRƯỞNG PHÒNG HAY KHÔNG
    // Kiểm tra nhân viên có là trưởng phòng hay không
    boolean isHeadOfDepartment(UUID staffId);

    // Kiểm tra người dùng hiện tại có là trưởng phòng hay không
    boolean isHeadOfDepartment();


    // KIỂM TRA CÓ ĐƯỢC PHÉP PHÂN CA CHO NHÂN VIÊN HAY KHÔNG
    // Kiểm tra nhân viên có được phép phân ca cho nhân viên hay không
    boolean hasShiftAssignmentPermission(UUID staffId);

    // Kiểm tra người dùng hiện tại có được phép phân ca cho nhân viên hay không
    boolean hasShiftAssignmentPermission();

    boolean isDirectManager(Long userId);

}
