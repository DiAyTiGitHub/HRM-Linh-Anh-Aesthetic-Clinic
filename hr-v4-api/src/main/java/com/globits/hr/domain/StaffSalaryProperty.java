package com.globits.hr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.globits.core.domain.BaseObject;
import com.globits.salary.domain.SalaryItem;

/**
 * @author dunghq
 * Bảng thuộc tính lương của Staff
 */

@Table(name = "tbl_staff_salary_property")
@Entity
public class StaffSalaryProperty extends BaseObject {
    private static final long serialVersionUID = 2169442983827528358L;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_item_id")
    private SalaryItem salaryItem;
    @Column(name = "value")
    private Double value;//Giá trị của thuộc tính lương tương ứng với salaryItem (ví dụ :SalaryItem là HSL thì giá trị có thể là 3.0)
    @Column(name = "code")
    private String code;
    @Column(name = "name")
    private String name;

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public SalaryItem getSalaryItem() {
        return salaryItem;
    }

    public void setSalaryItem(SalaryItem salaryItem) {
        this.salaryItem = salaryItem;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;

    }
}
