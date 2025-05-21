package com.globits.salary.domain;

import com.globits.core.domain.BaseObject;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffLabourAgreement;
import jakarta.persistence.*;

import java.util.Set;

// Mức ngưỡng của Thành phần lương
@Table(name = "tbl_salary_item_threshold")
@Entity
public class SalaryItemThreshold extends BaseObject {
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_item_id")
    private SalaryItem salaryItem;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "threshold_value", columnDefinition = "TEXT")
    private String thresholdValue; // Công thức/Gía trị ngưỡng

    @Column(name = "in_use_value", columnDefinition = "TEXT")
    private String inUseValue; // Gía trị sử dụng khi đạt mức ngưỡng cao nhất


    public SalaryItem getSalaryItem() {
        return salaryItem;
    }

    public void setSalaryItem(SalaryItem salaryItem) {
        this.salaryItem = salaryItem;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(String thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public String getInUseValue() {
        return inUseValue;
    }

    public void setInUseValue(String inUseValue) {
        this.inUseValue = inUseValue;
    }
}
