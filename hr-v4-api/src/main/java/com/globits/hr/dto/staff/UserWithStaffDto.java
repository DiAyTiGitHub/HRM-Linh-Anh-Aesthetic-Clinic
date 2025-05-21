package com.globits.hr.dto.staff;


import com.globits.hr.dto.StaffDto;
import com.globits.security.domain.User;
import com.globits.security.dto.UserDto;

//this dto is only used for function save user and choose staff using this user account at the same time
public class UserWithStaffDto extends UserDto {
    private StaffDto staff;

    public UserWithStaffDto(){

    }

    public UserWithStaffDto(User entity){
        super(entity);
    }

    public UserWithStaffDto(UserDto dto){
        this.setId(dto.getId());
        this.setDisplayName(dto.getDisplayName());
        this.setUsername(dto.getUsername());
        this.setLastName(dto.getLastName());
        this.setFirstName(dto.getFirstName());
        this.setDob(dto.getDob());
        this.setBirthPlace(dto.getBirthPlace());
        this.setEmail(dto.getEmail());
        this.setPerson(dto.getPerson());
        this.setRoles(dto.getRoles());
        this.setOrg(dto.getOrg());
        this.setGroups(dto.getGroups());
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }
}
