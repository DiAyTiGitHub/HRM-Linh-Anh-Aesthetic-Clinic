package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.*;

@Entity
@Table(name = "tbl_hr_group_staff", uniqueConstraints = {@UniqueConstraint(columnNames = {"group_id", "staff_id"})})
public class HrGroupStaff extends BaseObject {

    private static final long serialVersionUID = -987880391142046683L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private HrGroup hrgroup; // Ensure this matches with the setter method

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private Staff staff;

    public HrGroupStaff() {}

    public HrGroupStaff(HrGroup hrGroup, Staff staff) {
        this.hrgroup = hrGroup;
        this.staff = staff;
    }

    public HrGroup getHrGroup() { return hrgroup; }

    public void setHrGroup(HrGroup hrGroup) { this.hrgroup = hrGroup; }

    public Staff getStaff() { return staff; }

    public void setStaff(Staff staff) { this.staff = staff; }
}


