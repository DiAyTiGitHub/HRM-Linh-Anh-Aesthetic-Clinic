package com.globits.hrv3.dto.view;

import com.globits.core.dto.PersonDto;
import com.globits.hr.domain.Staff;
import com.globits.security.dto.UserDto;

public class GeneralInformationDto extends PersonDto {
    private String permanentResidence;//Hộ khẩu thường trú
    private String currentResidence;
    private UserDto user;

    public GeneralInformationDto() {
        super();
    }

    public GeneralInformationDto(Staff entity) {
        if (entity == null) {
            return;
        }
        id = entity.getId();
        firstName = entity.getFirstName();
        lastName = entity.getLastName();
        displayName = entity.getDisplayName();
        gender = entity.getGender();
        birthDate = entity.getBirthDate();
        birthPlace = entity.getBirthPlace();
        startDate = entity.getStartDate();
        setMaritalStatus(entity.getMaritalStatus());
        permanentResidence = entity.getPermanentResidence();
        currentResidence = entity.getCurrentResidence();
        phoneNumber = entity.getPhoneNumber();
        if (entity.getUser() != null) {
            user = new UserDto(entity.getUser());
        }
    }

    public String getPermanentResidence() {
        return permanentResidence;
    }

    public void setPermanentResidence(String permanentResidence) {
        this.permanentResidence = permanentResidence;
    }

    public String getCurrentResidence() {
        return currentResidence;
    }

    public void setCurrentResidence(String currentResidence) {
        this.currentResidence = currentResidence;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

}
