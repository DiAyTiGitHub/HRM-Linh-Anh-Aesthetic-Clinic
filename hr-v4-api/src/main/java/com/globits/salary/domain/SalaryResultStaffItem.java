package com.globits.salary.domain;

import com.globits.core.domain.BaseObject;
import com.globits.hr.domain.Staff;
import jakarta.persistence.*;

// Giá trị của từng thành phần lương của nhân viên trong bảng lương => Gía trị của từng cell trong dòng dữ liệu
@Table(name = "tbl_salary_result_staff_item")
@Entity
public class SalaryResultStaffItem extends BaseObject {
    private static final long serialVersionUID = 1L;
    private String value;

    @Column(name = "reference_code")
    private String referenceCode; // Mã tham chiếu (của thành phần lương) phòng trường hợp dữ liệu cha bị thay đổi

    @Column(name = "reference_name")
    private String referenceName; // Tên tham chiếu phòng trường hợp dữ liệu cha bị thay đổi

    @Column(name = "reference_display_order")
    private Integer referenceDisplayOrder; // Thứ tự hiển thị tham chiếu phòng trường hợp dữ liệu cha bị thay đổi

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_result_staff_id")
    private SalaryResultStaff salaryResultStaff;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_result_item_id")
    private SalaryResultItem salaryResultItem;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_template_item_id")
    private SalaryTemplateItem salaryTemplateItem;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public SalaryResultStaff getSalaryResultStaff() {
        return salaryResultStaff;
    }

    public void setSalaryResultStaff(SalaryResultStaff salaryResultStaff) {
        this.salaryResultStaff = salaryResultStaff;
    }

    public Integer getReferenceDisplayOrder() {
        return referenceDisplayOrder;
    }

    public void setReferenceDisplayOrder(Integer referenceDisplayOrder) {
        this.referenceDisplayOrder = referenceDisplayOrder;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }


    public SalaryResultItem getSalaryResultItem() {
        return salaryResultItem;
    }

    public void setSalaryResultItem(SalaryResultItem salaryResultItem) {
        this.salaryResultItem = salaryResultItem;
    }

	public SalaryTemplateItem getSalaryTemplateItem() {
		return salaryTemplateItem;
	}

	public void setSalaryTemplateItem(SalaryTemplateItem salaryTemplateItem) {
		this.salaryTemplateItem = salaryTemplateItem;
	}
}
