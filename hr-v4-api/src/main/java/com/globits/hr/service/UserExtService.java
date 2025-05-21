package com.globits.hr.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.globits.hr.domain.Staff;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.UserExtRoleDto;
import com.globits.hr.dto.loginkeycloak.UserKeyCloackDto;
import com.globits.hr.dto.search.UserSearchDto;
import com.globits.hr.dto.staff.UserWithStaffDto;
import com.globits.security.domain.Role;
import com.globits.security.domain.User;
import com.globits.security.dto.UserDto;

@Service
public interface UserExtService {
    Page<UserDto> pagingUsers(UserSearchDto dto);

    // UserExtDto deleteById(Long userId);

    // List<UserExtDto> deleteListId(List<Long> userId);

    ResponseEntity<UserKeyCloackDto> creatUserKeyCloak(UserDto dto);

    UserDto getCurrentUser();

    User getCurrentUserEntity();

    UserDto saveUser(UserDto dto);

    UserDto updateUser(UserDto dto);

    ResponseEntity<UserKeyCloackDto> updateUserKeyCloak(UserDto dto);

    StaffDto getCurrentStaff();

    String changePassword(UserDto dto);

    String resetPassword(UserDto dto);

    Staff getCurrentStaffEntity();

    List<UserDto> findListByUserName(String username);

    UserWithStaffDto saveUserAndChooseUsingStaff(UserWithStaffDto dto);

    Page<UserWithStaffDto> pagingUserWithStaff(UserSearchDto dto);

    UserWithStaffDto getUserWithUsingStaff(Long userId);

    UserDto saveUserOnly(UserDto dto);

    List<Role> getListRole();

    List<UserWithStaffDto> saveAllUsersWithStaff(List<UserWithStaffDto> dtos);

    UserDto getUserByStaffId(UUID staffId);

    Boolean hasRoleManageHCNS();

    UserExtRoleDto getCurrentRoleUser();

    void checkIsGeneralDirectorOrDeputyGeneralDirector(StaffDto staffDto);
}
