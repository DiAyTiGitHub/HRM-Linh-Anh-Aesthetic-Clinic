package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// Các vị trí định biên
@Table(name = "tbl_hr_department_shift_work")
@Entity
public class HrDepartmentShiftWork extends BaseObject {

    @ManyToOne
    @JoinColumn(name = "department_id")
    private HRDepartment department;

    @ManyToOne
    @JoinColumn(name = "shift_work_id")
    private ShiftWork shiftWork;


    public HRDepartment getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartment department) {
        this.department = department;
    }

    public ShiftWork getShiftWork() {
        return shiftWork;
    }

    public void setShiftWork(ShiftWork shiftWork) {
        this.shiftWork = shiftWork;
    }
}
