package com.globits.hr.dto;

import com.globits.budget.dto.BaseNameCodeObjectDto;
import com.globits.hr.domain.KPI;
import com.globits.hr.domain.KPIItem;

import java.util.ArrayList;
import java.util.List;

public class KPIDto extends BaseNameCodeObjectDto {
    private List<KPIItemDto> kpiItems;

    public KPIDto() {
    }

    public KPIDto(KPI entity, Boolean isGetFull) {
        super(entity);
        if (isGetFull) {
            this.kpiItems = new ArrayList<KPIItemDto>();
            for (KPIItem item : entity.getKpiItems()) {
                this.kpiItems.add(new KPIItemDto(item, false));
            }
        }
    }

    public KPIDto(KPI entity) {
        this(entity, true);
    }

    public List<KPIItemDto> getKpiItems() {
        return kpiItems;
    }

    public void setKpiItems(List<KPIItemDto> kpiItems) {
        this.kpiItems = kpiItems;
    }
}
