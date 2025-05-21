package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
/*
 * Các chức danh của phòng ban
 */
@Table(name = "tbl_hr_department_position")
@Entity
public class HRDepartmentPosition extends BaseObject {
    private static final long serialVersionUID = -987880391142046683L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private HRDepartment department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_title_id")
    private PositionTitle positionTitle;

    public HRDepartment getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartment department) {
        this.department = department;
    }

    public PositionTitle getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(PositionTitle positionTitle) {
        this.positionTitle = positionTitle;
    }

}
