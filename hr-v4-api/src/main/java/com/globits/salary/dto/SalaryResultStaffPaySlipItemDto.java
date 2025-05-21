package com.globits.salary.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.dto.StaffDto;
import com.globits.salary.domain.SalaryResultStaff;
import com.globits.salary.domain.SalaryResultStaffItem;

import java.util.*;

// Hạng mục lương trong Phiếu lương của nhân viên
public class SalaryResultStaffPaySlipItemDto extends BaseObjectDto {
    private String value;
    // Kiểu giá trị
    private Integer valueType;
    private String referenceCode; // Mã tham chiếu (của thành phần lương) phòng trường hợp dữ liệu cha bị thay đổi
    private String referenceName; // Tên tham chiếu (của thành phần lương) phòng trường hợp dữ liệu cha bị thay
    // đổi
    private Integer referenceDisplayOrder; // Thứ tự hiển thị tham chiếu phòng trường hợp dữ liệu cha bị thay đổi
    private UUID salaryResultStaffId;
    private SalaryResultItemDto salaryResultItem;
    private SalaryTemplateItemDto salaryTemplateItem;

    public SalaryResultStaffPaySlipItemDto() {

    }

    public SalaryResultStaffPaySlipItemDto(SalaryResultStaffItem entity) {
        super();

        this.id = entity.getId();
        this.value = entity.getValue();

        this.referenceCode = entity.getReferenceCode();
        this.referenceDisplayOrder = entity.getReferenceDisplayOrder();
        this.referenceName = entity.getReferenceName();

        if (entity.getSalaryResultItem() != null && entity.getSalaryResultItem().getValueType() != null) {
            this.valueType = entity.getSalaryResultItem().getValueType();
        }else if(entity.getSalaryTemplateItem()!=null) {
        	this.valueType = entity.getSalaryTemplateItem().getValueType();
        }

        if (entity.getSalaryResultStaff() != null) {
            this.salaryResultStaffId = entity.getSalaryResultStaff().getId();
        }

        if (entity.getSalaryResultItem() != null) {
            this.salaryResultItem = new SalaryResultItemDto(entity.getSalaryResultItem());
        }

        if(entity.getSalaryTemplateItem()!=null) {
            this.salaryTemplateItem = new SalaryTemplateItemDto(entity.getSalaryTemplateItem());
        }
    }

    public SalaryResultStaffPaySlipItemDto(SalaryResultStaffItem entity, Boolean isDetail) {
        this(entity);

        if (isDetail == null || isDetail.equals(false))
            return;

    }

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

    public Integer getReferenceDisplayOrder() {
        return referenceDisplayOrder;
    }

    public void setReferenceDisplayOrder(Integer referenceDisplayOrder) {
        this.referenceDisplayOrder = referenceDisplayOrder;
    }

    public UUID getSalaryResultStaffId() {
        return salaryResultStaffId;
    }

    public void setSalaryResultStaffId(UUID salaryResultStaffId) {
        this.salaryResultStaffId = salaryResultStaffId;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public Integer getValueType() {
        return valueType;
    }

    public void setValueType(Integer valueType) {
        this.valueType = valueType;
    }

    public SalaryResultItemDto getSalaryResultItem() {
        return salaryResultItem;
    }

    public void setSalaryResultItem(SalaryResultItemDto salaryResultItem) {
        this.salaryResultItem = salaryResultItem;
    }

    public SalaryTemplateItemDto getSalaryTemplateItem() {
        return salaryTemplateItem;
    }

    public void setSalaryTemplateItem(SalaryTemplateItemDto salaryTemplateItem) {
        this.salaryTemplateItem = salaryTemplateItem;
    }
}
