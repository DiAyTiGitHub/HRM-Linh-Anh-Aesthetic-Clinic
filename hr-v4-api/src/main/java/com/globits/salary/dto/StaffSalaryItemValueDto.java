package com.globits.salary.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.core.dto.BaseObjectDto;
import com.globits.core.dto.FileDescriptionDto;
import com.globits.hr.dto.StaffDto;
import com.globits.salary.domain.StaffSalaryItemValue;
import jakarta.persistence.Column;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class StaffSalaryItemValueDto extends BaseObjectDto {
    private SalaryItemDto salaryItem;
    private SalaryTemplateItemDto templateItem;
    private StaffDto staff;
    private Double value;
    private Integer calculationType; // Cách tính giá trị của thành phần lương này: HrConstants.SalaryItemCalculationType
    private Date fromDate; // thời gian bắt đầu áp dụng tính lương
    private Date toDate; // thời gian kết thúc áp dụng tính lương
    private Boolean isCurrent; // là hiện thời
    private FileDescriptionDto file; // Tài liệu đã được lưu

    public StaffSalaryItemValueDto() {
    }

    public StaffSalaryItemValueDto(StaffSalaryItemValue entity, Boolean isDetail) {
        this(entity);

        if (isDetail == null || !isDetail) return;

//        if (entity.getTemplateItem() != null){
//            this.templateItem = new SalaryTemplateItemDto(entity.getTemplateItem());
//        }

        if (entity.getSalaryItem() != null) {
            this.salaryItem = new SalaryItemDto(entity.getSalaryItem());
        }

        if (entity.getStaff() != null) {
            this.staff = new StaffDto();

            this.staff.setId(entity.getStaff().getId());
            this.staff.setStaffCode(entity.getStaff().getStaffCode());
            this.staff.setDisplayName(entity.getStaff().getDisplayName());
        }
    }

    public StaffSalaryItemValueDto(StaffSalaryItemValue entity) {
        super(entity);

        if (entity == null) return;

        this.value = entity.getValue();
        this.calculationType = entity.getCalculationType();
        this.fromDate = entity.getFromDate();
        this.toDate = entity.getToDate();
        this.isCurrent = entity.getIsCurrent();
        if (entity.getFile() != null) {
            this.file = new FileDescriptionDto(entity.getFile());
        }
    }


//    public SalaryItemDto getSalaryItem() {
//        return salaryItem;
//    }
//
//    public void setSalaryItem(SalaryItemDto salaryItem) {
//        this.salaryItem = salaryItem;
//    }

    public Boolean getCurrent() {
        return isCurrent;
    }

    public void setCurrent(Boolean current) {
        isCurrent = current;
    }

    public FileDescriptionDto getFile() {
        return file;
    }

    public void setFile(FileDescriptionDto file) {
        this.file = file;
    }

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

    public SalaryTemplateItemDto getTemplateItem() {
        return templateItem;
    }

    public void setTemplateItem(SalaryTemplateItemDto templateItem) {
        this.templateItem = templateItem;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
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

    public SalaryItemDto getSalaryItem() {
        return salaryItem;
    }

    public void setSalaryItem(SalaryItemDto salaryItem) {
        this.salaryItem = salaryItem;
    }


}


