package com.globits.salary.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.core.dto.BaseObjectDto;
import com.globits.salary.domain.SalaryResultStaffItem;
import com.globits.salary.domain.SalaryTemplateItem;

@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class SalaryResultStaffItemDto extends BaseObjectDto {
    private String value;
    // Kiểu giá trị
    private Integer valueType;
    private String referenceCode; // Mã tham chiếu (của thành phần lương) phòng trường hợp dữ liệu cha bị thay đổi
    private String referenceName; // Tên tham chiếu (của thành phần lương) phòng trường hợp dữ liệu cha bị thay
    // đổi
    private Integer referenceDisplayOrder; // Thứ tự hiển thị tham chiếu phòng trường hợp dữ liệu cha bị thay đổi
    private UUID salaryResultStaffId;
    private UUID salaryResultItemId;
    private SalaryTemplateItemDto salaryTemplateItem;
    
    public SalaryResultStaffItemDto() {

    }

    public SalaryResultStaffItemDto(SalaryResultStaffItem entity) {
        super();
        if(entity!=null) {
        	this.id = entity.getId();
            this.value = entity.getValue();

            this.referenceCode = entity.getReferenceCode();
            this.referenceDisplayOrder = entity.getReferenceDisplayOrder();
            this.referenceName = entity.getReferenceName();

            if (entity.getSalaryResultItem() != null && entity.getSalaryResultItem().getValueType() != null) {
                this.valueType = entity.getSalaryResultItem().getValueType();
            }else if(entity.getSalaryTemplateItem() != null) {
            	this.valueType = entity.getSalaryTemplateItem().getValueType();
            }

            if (entity.getSalaryResultStaff() != null) {
                this.salaryResultStaffId = entity.getSalaryResultStaff().getId();
            }

            if (entity.getSalaryResultItem() != null) {
                this.salaryResultItemId = entity.getSalaryResultItem().getId();
            }
            if(entity.getSalaryTemplateItem()!=null) {
            	this.salaryTemplateItem = new SalaryTemplateItemDto(entity.getSalaryTemplateItem());
            }
        }
    }

    public SalaryResultStaffItemDto(SalaryResultStaffItem entity, Boolean isDetail) {
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

    public UUID getSalaryResultItemId() {
        return salaryResultItemId;
    }

    public void setSalaryResultItemId(UUID salaryResultItemId) {
        this.salaryResultItemId = salaryResultItemId;
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

	public SalaryTemplateItemDto getSalaryTemplateItem() {
		return salaryTemplateItem;
	}

	public void setSalaryTemplateItem(SalaryTemplateItemDto salaryTemplateItem) {
		this.salaryTemplateItem = salaryTemplateItem;
	}
}
