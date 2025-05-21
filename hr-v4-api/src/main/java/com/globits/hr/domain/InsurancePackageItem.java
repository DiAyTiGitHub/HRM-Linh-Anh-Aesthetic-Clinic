package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.timesheet.domain.LeaveType;
import com.globits.timesheet.domain.TimeSheetDetail;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Table(name = "tbl_insurance_package_item")
@Entity
public class InsurancePackageItem extends BaseObject {
    private static final long serialVersionUID = 991992518344617174L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_package_id")
    private InsurancePackage insurancePackage; // Thuộc gói bảo hiểm nào

    @Column(name = "display_order")
    private Integer displayOrder; // Thứ tự hiển thị

    @Column(name = "name")
    private String name; // Thông tin hạng mục trong bảo hiểm

    @Column(name = "description")
    private String description; // ghi chu


    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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

    public InsurancePackage getInsurancePackage() {
        return insurancePackage;
    }

    public void setInsurancePackage(InsurancePackage insurancePackage) {
        this.insurancePackage = insurancePackage;
    }
}
