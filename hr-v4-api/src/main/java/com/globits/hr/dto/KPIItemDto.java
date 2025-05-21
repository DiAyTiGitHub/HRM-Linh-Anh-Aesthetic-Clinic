package com.globits.hr.dto;

import com.globits.budget.dto.BaseNameCodeObjectDto;
import com.globits.hr.domain.KPIItem;

public class KPIItemDto extends BaseNameCodeObjectDto {
    private Double weight;
    private Boolean usedForSalary;
    private KPIDto kpi;

    public KPIItemDto() {
    }

    public KPIItemDto(KPIItem entity, Boolean isGetFull) {
        super(entity);
        if (entity != null) {
            this.weight = entity.getWeight();
            this.usedForSalary = entity.getUsedForSalary();
            if (isGetFull) {
                this.kpi = new KPIDto(entity.getKpi(), false);
            }
        }
    }

    public KPIItemDto(KPIItem entity) {
        this(entity, true);
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }


    public Boolean getUsedForSalary() {
        return usedForSalary;
    }

    public void setUsedForSalary(Boolean usedForSalary) {
        this.usedForSalary = usedForSalary;
    }

    public KPIDto getKpi() {
        return kpi;
    }

    public void setKpi(KPIDto kpi) {
        this.kpi = kpi;
    }
}
