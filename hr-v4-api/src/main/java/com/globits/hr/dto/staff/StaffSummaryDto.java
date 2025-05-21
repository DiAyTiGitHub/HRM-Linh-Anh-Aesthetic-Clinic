package com.globits.hr.dto.staff;

import com.globits.hr.domain.Staff;
import com.globits.hr.dto.StaffDto;
import com.globits.timesheet.dto.TimeSheetDetailDto;

import org.springframework.data.domain.Page;

public class StaffSummaryDto extends StaffDto {
    Page<TimeSheetDetailDto> listTimesheet;

    public StaffSummaryDto() {
    }

    public StaffSummaryDto(Staff entity) {
        super(entity);
    }

    public Page<TimeSheetDetailDto> getListTimesheet() {
        return listTimesheet;
    }

    public void setListTimesheet(Page<TimeSheetDetailDto> listTimesheet) {
        this.listTimesheet = listTimesheet;
    }
}
