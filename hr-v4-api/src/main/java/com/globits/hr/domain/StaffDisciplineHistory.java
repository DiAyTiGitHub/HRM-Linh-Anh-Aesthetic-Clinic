package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.core.domain.FileDescription;
import jakarta.persistence.*;

import java.util.Date;

// Quá trình kỷ luật của nhân viên
@Table(name = "tbl_staff_discipline_history")
@Entity
public class StaffDisciplineHistory extends BaseObject {
    private static final long serialVersionUID = 795781122770892206L;

    @Column(name = "reward_date")
    private Date disciplineDate; // Ngày quyết định kỷ luật

    @ManyToOne
    @JoinColumn(name = "discipline_id")
    private HRDiscipline discipline; // Hình thức kỷ luật

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private Staff staff; // Nhân viên bị kỷ luật

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private HrOrganization organization; // Đơn vị kỷ luật

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private HRDepartment department; // Phòng ban kỷ luật

    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileDescription file; // Tệp đính kèm


    public Date getDisciplineDate() {
        return disciplineDate;
    }

    public void setDisciplineDate(Date disciplineDate) {
        this.disciplineDate = disciplineDate;
    }

    public HRDiscipline getDiscipline() {
        return discipline;
    }

    public void setDiscipline(HRDiscipline discipline) {
        this.discipline = discipline;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public HrOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(HrOrganization organization) {
        this.organization = organization;
    }

    public HRDepartment getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartment department) {
        this.department = department;
    }

    public FileDescription getFile() {
        return file;
    }

    public void setFile(FileDescription file) {
        this.file = file;
    }
}
