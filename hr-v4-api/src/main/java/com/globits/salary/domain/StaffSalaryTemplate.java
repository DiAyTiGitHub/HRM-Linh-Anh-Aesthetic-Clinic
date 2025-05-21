package com.globits.salary.domain;

import com.globits.core.domain.BaseObject;
import com.globits.hr.domain.Staff;
import jakarta.persistence.*;

// Các mẫu bảng lương được áp dụng cho nhân viên
@Table(name = "tbl_staff_salary_template")
@Entity
public class StaffSalaryTemplate extends BaseObject {
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id")
    private Staff staff; // nhân viên dùng mẫu bảng lương

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_template_id")
    private SalaryTemplate salaryTemplate; // Mẫu bảng lương được dùng

//    @Column(name = "from_date")
//    private Date fromDate; // thời gian bắt đầu áp dụng tính lương cho nhân viên theo mẫu
//
//    @Column(name = "to_date")
//    private Date toDate; // thời gian kết thúc áp dụng tính lương cho nhân viên theo mẫu


    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public SalaryTemplate getSalaryTemplate() {
        return salaryTemplate;
    }

    public void setSalaryTemplate(SalaryTemplate salaryTemplate) {
        this.salaryTemplate = salaryTemplate;
    }

//    public Date getFromDate() {
//        return fromDate;
//    }
//
//    public void setFromDate(Date fromDate) {
//        this.fromDate = fromDate;
//    }
//
//    public Date getToDate() {
//        return toDate;
//    }
//
//    public void setToDate(Date toDate) {
//        this.toDate = toDate;
//    }
}
