package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.InsurancePackage;
import com.globits.hr.domain.InsurancePackageItem;
import com.globits.hr.domain.StaffType;
import com.globits.salary.domain.SalaryResultStaff;
import com.globits.salary.dto.SalaryTemplateItemDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

import java.util.*;

public class InsurancePackageDto extends BaseObjectDto {
    private static final long serialVersionUID = 991992518344617174L;

    private String code; // Mã gói bảo hiểm
    private String name; // Tên bảo hiểm
    private String description; // Mô tả

    private List<InsurancePackageItemDto> packageItems; // thành phần lương chính là các cột trong mẫu bảng lương

    public InsurancePackageDto() {
    }

    public InsurancePackageDto(InsurancePackage entity) {
        super(entity);

        if (entity == null) {
            return;
        }

        this.code = entity.getCode();
        this.name = entity.getName();
        this.description = entity.getDescription();

    }

    public InsurancePackageDto(InsurancePackage entity, boolean isDetail) {
        this(entity);

        if (!isDetail) return;

        this.packageItems = new ArrayList<>();

        if (entity.getPackageItems() != null && !entity.getPackageItems().isEmpty()) {
            for (InsurancePackageItem packageItem : entity.getPackageItems()) {
                InsurancePackageItemDto responseItem = new InsurancePackageItemDto(packageItem);

                this.packageItems.add(responseItem);
            }
        }

        Collections.sort(this.packageItems, new Comparator<InsurancePackageItemDto>() {
            @Override
            public int compare(InsurancePackageItemDto o1, InsurancePackageItemDto o2) {
                // First, compare by displayOrder
                if (o1.getDisplayOrder() == null && o2.getDisplayOrder() == null)
                    return 0;
                if (o1.getDisplayOrder() == null)
                    return 1;
                if (o2.getDisplayOrder() == null)
                    return -1;

                int orderComparison = o1.getDisplayOrder().compareTo(o2.getDisplayOrder());
                return orderComparison;
            }
        });
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

    public List<InsurancePackageItemDto> getPackageItems() {
        return packageItems;
    }

    public void setPackageItems(List<InsurancePackageItemDto> packageItems) {
        this.packageItems = packageItems;
    }
}
