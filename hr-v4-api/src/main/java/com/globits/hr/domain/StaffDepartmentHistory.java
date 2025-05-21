package com.globits.hr.domain;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;
import com.globits.core.domain.Department;

/**
 * @author dunghq
 * Quá trình điều chuyển nhân viên
 */

@Table(name = "tbl_staff_department_history")
@Entity
public class StaffDepartmentHistory extends BaseObject {
    private static final long serialVersionUID = 1L;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;
    @Column(name = "note")
    private String note;//Ghi chú
    @Column(name = "position_title_name")
    private String positionTitleName;//Tên chức vụ
    @Column(name = "department_name")
    private String departmentName;//Tên phòng
    @ManyToOne
    @JoinColumn(name = "from_partment_id")
    private Department fromDepartment;
    @ManyToOne
    @JoinColumn(name = "to_partment_id")
    private Department toDepartment;
    @ManyToOne
    @JoinColumn(name = "position_id")
    private PositionTitle position;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPositionTitleName() {
        return positionTitleName;
    }

    public void setPositionTitleName(String positionTitleName) {
        this.positionTitleName = positionTitleName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Department getFromDepartment() {
        return fromDepartment;
    }

    public void setFromDepartment(Department fromDepartment) {
        this.fromDepartment = fromDepartment;
    }

    public Department getToDepartment() {
        return toDepartment;
    }

    public void setToDepartment(Department toDepartment) {
        this.toDepartment = toDepartment;
    }

    public PositionTitle getPosition() {
        return position;
    }

    public void setPosition(PositionTitle position) {
        this.position = position;
    }
}
