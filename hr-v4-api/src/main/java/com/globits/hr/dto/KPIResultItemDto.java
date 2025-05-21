package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.KPIResultItem;

public class KPIResultItemDto extends BaseObjectDto {
    private Double value;
    private KPIResultDto kpiResult;
    private KPIItemDto kpiItem;

    public KPIResultItemDto() {
    }

    public KPIResultItemDto(KPIResultItem entity, Boolean isGetFull) {
        super(entity);
        if (entity != null) {
            this.value = entity.getValue();
            if (entity.getKpiItem() != null) {
                this.kpiItem = new KPIItemDto(entity.getKpiItem(), false);
            }
            if (isGetFull) {
                if (entity.getKpiResult() != null) {
                    this.kpiResult = new KPIResultDto(entity.getKpiResult(), false);
                }
            }
        }
    }

    public KPIResultItemDto(KPIResultItem entity) {
        this(entity, true);
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public KPIResultDto getKpiResult() {
        return kpiResult;
    }

    public void setKpiResult(KPIResultDto kpiResult) {
        this.kpiResult = kpiResult;
    }

    public KPIItemDto getKpiItem() {
        return kpiItem;
    }

    public void setKpiItem(KPIItemDto kpiItem) {
        this.kpiItem = kpiItem;
    }
}
