package com.globits.hr.rest;

import com.globits.hr.dto.search.UserSearchDto;
import com.globits.hr.dto.staff.UserWithStaffDto;
import com.globits.security.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.service.UserExtService;
import org.springframework.security.access.annotation.Secured;
import com.globits.security.dto.UserDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-ext")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestUserExtController {

    @Autowired
    private UserExtService userExtService;

    @RequestMapping(value = "/get-current-user", method = RequestMethod.GET)
    public UserDto getCurrentUser() {
        return userExtService.getCurrentUser();
    }

    @RequestMapping(value = "/has-manager-hr-department", method = RequestMethod.GET)
    public ResponseEntity<Boolean> hasRoleManageHCNS() {
        Boolean response = userExtService.hasRoleManageHCNS();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/get-user-by-staff/{staffId}", method = RequestMethod.GET)
    public UserDto getUserByStaffId(@PathVariable("staffId") UUID staffId) {
        return userExtService.getUserByStaffId(staffId);
    }

    //old code to save user: save user only
    @RequestMapping(method = RequestMethod.POST)
    public UserDto saveUser(@RequestBody UserDto dto) {
        return userExtService.saveUser(dto);
    }

    @RequestMapping(value = "/get-current-staff", method = RequestMethod.GET)
    public StaffDto getCurrentStaff() {
        return userExtService.getCurrentStaff();
    }

    @PostMapping(value = "/paging-user")
    public ResponseEntity<Page<UserDto>> pagingUsers(@RequestBody UserSearchDto searchDto) {
        Page<UserDto> result = userExtService.pagingUsers(searchDto);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/change-password")
    public ResponseEntity<String> changePassword(@RequestBody UserDto dto) {
        String result = userExtService.changePassword(dto);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody UserDto dto) {
        String result = userExtService.resetPassword(dto);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    //new logic: save user and choose staff using this account at the same time => this staff can be loggind with this user account
    @RequestMapping(method = RequestMethod.POST, value = "save-user-and-choose-using-staff")
    public ResponseEntity<UserWithStaffDto> saveUserAndChooseUsingStaff(@RequestBody UserWithStaffDto dto) {
        UserWithStaffDto response = userExtService.saveUserAndChooseUsingStaff(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //new logic: paging user and display staff who is currently using this user account
    @PostMapping(value = "/paging-user-with-using-staff")
    public ResponseEntity<Page<UserWithStaffDto>> pagingUserWithStaff(@RequestBody UserSearchDto searchDto) {
        Page<UserWithStaffDto> result = userExtService.pagingUserWithStaff(searchDto);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //new logic: get user and staff who is currently using this user account
    @GetMapping(value = "/user-with-using-staff/{userId}")
    public ResponseEntity<UserWithStaffDto> getUserWithUsingStaff(@PathVariable("userId") Long userId) {
        UserWithStaffDto result = userExtService.getUserWithUsingStaff(userId);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/get-list-role")
    public ResponseEntity<List<Role>> getListRole() {
        List<Role> result = userExtService.getListRole();
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
