package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.salary.domain.SalaryResultStaffItem;
import com.globits.task.domain.HrTask;
import jakarta.persistence.*;

import java.util.Set;

@Table(name = "tbl_staff_type")
@Entity
public class StaffType extends BaseObject {
    private static final long serialVersionUID = 991992518344617174L;

    private String code; // ma loai nhan vien
    private String name; // loai nhan vien
    private String description; // ghi chu

    @OneToMany(mappedBy = "staffType",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Staff> staffs;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Staff> getStaffs() {
        return staffs;
    }

    public void setStaffs(Set<Staff> staffs) {
        this.staffs = staffs;
    }
}
