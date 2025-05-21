package com.globits.hr.dto.staff;

import com.globits.hr.domain.Staff;
import com.globits.hr.dto.StaffDto;

public class StaffWithTitleDto {
    private StaffDto staff;
    private String titleName;

    public StaffWithTitleDto(Staff staff, String titleName) {
        this.staff = new StaffDto(staff, true);
        this.titleName = titleName;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public String getTitleName() {
        return titleName;
    }
}
