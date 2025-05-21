package com.globits.salary.domain;

import com.globits.core.domain.BaseObject;
import com.globits.hr.domain.StaffSocialInsurance;
import jakarta.persistence.*;

import java.util.Set;

// Bảng lương
@Table(name = "tbl_salary_result")
@Entity
public class SalaryResult extends BaseObject {
    private static final long serialVersionUID = 1L;
    private String code;
    private String name;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_locked")
    private Boolean isLocked; // Bảng lương này đã bị khóa hay chưa

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_template_id")
    private SalaryTemplate salaryTemplate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_period_id")
    private SalaryPeriod salaryPeriod;

    @OneToMany(mappedBy = "salaryResult", fetch = FetchType.LAZY,
//            cascade = {CascadeType.MERGE, CascadeType.PERSIST}
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<SalaryResultStaff> salaryResultStaffs; //  Các dòng trong bảng lương => Chứa dòng dữ liệu của các nhân viên trong bảng

    @OneToMany(mappedBy = "salaryResult", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<SalaryResultItemGroup> resultItemGroups; // các nhóm cột trong bảng lương

    @OneToMany(mappedBy = "salaryResult", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SalaryResultItem> resultItems; // thành phần lương chính là các cột trong bảng lương

    // các bản ghi đóng BHXH của nhân viên được tạo từ bảng lương
    @OneToMany(mappedBy = "salaryResult", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StaffSocialInsurance> staffSocialInsurances; // thành phần lương chính là các cột trong bảng lương

    @Column(name = "approval_status")
    private Integer approvalStatus; // Trạng thái duyệt của bảng lương. Chi tiết trong: HrConstants.SalaryResulStaffApprovalStatus

    
    
    

    public Boolean getIsLocked() {
		return isLocked;
	}

	public void setIsLocked(Boolean isLocked) {
		this.isLocked = isLocked;
	}

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

    public SalaryTemplate getSalaryTemplate() {
        return salaryTemplate;
    }

    public void setSalaryTemplate(SalaryTemplate salaryTemplate) {
        this.salaryTemplate = salaryTemplate;
    }

    public SalaryPeriod getSalaryPeriod() {
        return salaryPeriod;
    }

    public void setSalaryPeriod(SalaryPeriod salaryPeriod) {
        this.salaryPeriod = salaryPeriod;
    }

    public Set<SalaryResultStaff> getSalaryResultStaffs() {
        return salaryResultStaffs;
    }

    public void setSalaryResultStaffs(Set<SalaryResultStaff> salaryResultStaffs) {
        this.salaryResultStaffs = salaryResultStaffs;
    }

    public Set<SalaryResultItemGroup> getResultItemGroups() {
        return resultItemGroups;
    }

    public void setResultItemGroups(Set<SalaryResultItemGroup> resultItemGroups) {
        this.resultItemGroups = resultItemGroups;
    }

    public Set<SalaryResultItem> getResultItems() {
        return resultItems;
    }

    public void setResultItems(Set<SalaryResultItem> resultItems) {
        this.resultItems = resultItems;
    }

    public Set<StaffSocialInsurance> getStaffSocialInsurances() {
        return staffSocialInsurances;
    }

    public void setStaffSocialInsurances(Set<StaffSocialInsurance> staffSocialInsurances) {
        this.staffSocialInsurances = staffSocialInsurances;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
}
