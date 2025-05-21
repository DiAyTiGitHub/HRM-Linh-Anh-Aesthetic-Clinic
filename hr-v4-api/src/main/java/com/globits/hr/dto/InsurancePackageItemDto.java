package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.InsurancePackageItem;
import com.globits.hr.domain.StaffType;
import com.globits.salary.dto.SalaryTemplateItemDto;

import java.util.List;
import java.util.UUID;

public class InsurancePackageItemDto extends BaseObjectDto {
    private static final long serialVersionUID = 991992518344617174L;

    private InsurancePackageDto insurancePackage; // Thuộc gói bảo hiểm nào
    private UUID insurancePackageId; // Thuộc gói bảo hiểm nào
    private Integer displayOrder; // Thứ tự hiển thị
    private String name; // Thông tin hạng mục trong bảo hiểm
    private String description; // Mô tả

    public InsurancePackageItemDto() {
    }

    public InsurancePackageItemDto(InsurancePackageItem entity) {
        super(entity);

        if (entity == null) {
            return;
        }

        this.name = entity.getName();
        this.description = entity.getDescription();
        this.displayOrder = entity.getDisplayOrder();
        if (entity.getInsurancePackage() != null) {
            this.insurancePackageId = entity.getInsurancePackage().getId();
        }
    }

    public InsurancePackageItemDto(InsurancePackageItem entity, boolean isDetail) {
        this(entity);

        if (!isDetail) return;

        if (entity.getInsurancePackage() != null) {
            this.insurancePackageId = entity.getInsurancePackage().getId();

            this.insurancePackage = new InsurancePackageDto();
        }
    }

    public InsurancePackageDto getInsurancePackage() {
        return insurancePackage;
    }

    public void setInsurancePackage(InsurancePackageDto insurancePackage) {
        this.insurancePackage = insurancePackage;
    }

    public UUID getInsurancePackageId() {
        return insurancePackageId;
    }

    public void setInsurancePackageId(UUID insurancePackageId) {
        this.insurancePackageId = insurancePackageId;
    }

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
}
