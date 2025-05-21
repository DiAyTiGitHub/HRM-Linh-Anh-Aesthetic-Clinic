package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.timesheet.domain.TimeSheetDetail;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

// Gói đóng bảo hiểm (khác BHXH của nhân viên)
@Table(name = "tbl_insurance_package")
@Entity
public class InsurancePackage extends BaseObject {
    private static final long serialVersionUID = 991992518344617174L;

    @Column(name = "code")
    private String code; // ma bảo hiểm

    @Column(name = "name")
    private String name; // Tên bảo hiểm

    @Column(name = "description")
    private String description; // Mô tả

    @OneToMany(mappedBy = "insurancePackage", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InsurancePackageItem> packageItems; // Các hạng mục trong bảo hiểm


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

    public Set<InsurancePackageItem> getPackageItems() {
        return packageItems;
    }

    public void setPackageItems(Set<InsurancePackageItem> packageItems) {
        this.packageItems = packageItems;
    }
}
