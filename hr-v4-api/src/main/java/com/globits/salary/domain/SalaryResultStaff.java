package com.globits.salary.domain;

import com.globits.core.domain.BaseObject;
import com.globits.hr.domain.Staff;
import jakarta.persistence.*;

import java.util.Set;

// Các dòng trong bảng lương => Chứa dòng dữ liệu của các nhân viên trong bảng
@Table(name = "tbl_salary_result_staff")
@Entity
public class SalaryResultStaff extends BaseObject {
    private static final long serialVersionUID = 1L;

    @Column(name = "display_order")
    private Integer displayOrder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_result_id")
    private SalaryResult salaryResult;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id")
    private Staff staff;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_period_id")
    private SalaryPeriod salaryPeriod;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_template_id")
    private SalaryTemplate salaryTemplate;

    @OneToMany(mappedBy = "salaryResultStaff", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("referenceDisplayOrder")
    private Set<SalaryResultStaffItem> salaryResultStaffItems; // Giá trị của từng thành phần lương của nhân viên trong bảng lương => Gía trị của từng cell trong dòng dữ liệu

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "status")
    private Integer approvalStatus; // Trạng thái duyệt phiếu lương. Chi tiết trong: HrConstants.SalaryResulStaffApprovalStatus

    @Column(name = "paid_status")
    private Integer paidStatus; // Trạng thái chi trả phiếu lương. Chi tiết trong: HrConstants.SalaryResulStaffPaidStatus


    public Integer getPaidStatus(){
        return paidStatus;
    }

    public void setPaidStatus(Integer paidStatus) {
        this.paidStatus = paidStatus;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public SalaryResult getSalaryResult() {
        return salaryResult;
    }

    public void setSalaryResult(SalaryResult salaryResult) {
        this.salaryResult = salaryResult;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Set<SalaryResultStaffItem> getSalaryResultStaffItems() {
        return salaryResultStaffItems;
    }

    public void setSalaryResultStaffItems(Set<SalaryResultStaffItem> salaryResultStaffItems) {
        this.salaryResultStaffItems = salaryResultStaffItems;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

	public SalaryPeriod getSalaryPeriod() {
		return salaryPeriod;
	}

	public void setSalaryPeriod(SalaryPeriod salaryPeriod) {
		this.salaryPeriod = salaryPeriod;
	}

	public SalaryTemplate getSalaryTemplate() {
		return salaryTemplate;
	}

	public void setSalaryTemplate(SalaryTemplate salaryTemplate) {
		this.salaryTemplate = salaryTemplate;
	}
}
