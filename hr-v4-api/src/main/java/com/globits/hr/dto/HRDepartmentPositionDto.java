package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.HRDepartment;
import com.globits.hr.domain.PositionTitle;

import java.io.Serializable;

public class HRDepartmentPositionDto  extends BaseObjectDto  implements Serializable {
    private HRDepartmentDto department;
    private PositionTitleDto positionTitle;

    public HRDepartmentDto getDepartment() {
        return department;
    }

    public PositionTitleDto getPositionTitle() {
        return positionTitle;
    }

    public void setDepartment(HRDepartmentDto department) {
        this.department = department;
    }

    public void setPositionTitle(PositionTitleDto positionTitle) {
        this.positionTitle = positionTitle;
    }
}
