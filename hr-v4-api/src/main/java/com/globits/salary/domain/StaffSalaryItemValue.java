package com.globits.salary.domain;

import com.globits.core.domain.BaseObject;
import com.globits.core.domain.FileDescription;
import com.globits.hr.domain.Staff;

import jakarta.persistence.*;

import java.util.Date;

@Table(name = "tbl_salary_value")
@Entity
public class StaffSalaryItemValue extends BaseObject {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_item_id")
    private SalaryItem salaryItem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "template_item_id")
    private SalaryTemplateItem templateItem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @Column(name = "value")
    private Double value;

    @Column(name = "calculation_type")
    private Integer calculationType; // Cách tính giá trị của thành phần lương này: HrConstants.SalaryItemCalculationType

    @Column(name = "from_date")
    private Date fromDate; // thời gian bắt đầu áp dụng tính lương

    @Column(name = "to_date")
    private Date toDate; // thời gian kết thúc áp dụng tính lương

    @Column(name = "is_current")
    private Boolean isCurrent; // là hiện thời

    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileDescription file; // Tài liệu đã được lưu

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Boolean getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(Boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    public SalaryItem getSalaryItem() {
        return salaryItem;
    }

    public void setSalaryItem(SalaryItem salaryItem) {
        this.salaryItem = salaryItem;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Integer getCalculationType() {
        return calculationType;
    }

    public void setCalculationType(Integer calculationType) {
        this.calculationType = calculationType;
    }

    public SalaryTemplateItem getTemplateItem() {
        return templateItem;
    }

    public void setTemplateItem(SalaryTemplateItem templateItem) {
        this.templateItem = templateItem;
    }

    public Boolean getCurrent() {
        return isCurrent;
    }

    public void setCurrent(Boolean current) {
        isCurrent = current;
    }

    public FileDescription getFile() {
        return file;
    }

    public void setFile(FileDescription file) {
        this.file = file;
    }
}
