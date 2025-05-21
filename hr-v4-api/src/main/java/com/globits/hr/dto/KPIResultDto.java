package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.KPIResult;
import com.globits.hr.domain.KPIResultItem;

import java.util.ArrayList;
import java.util.List;

public class KPIResultDto extends BaseObjectDto {
    private StaffDto staff;
    private KPIDto kpi;
    private List<KPIResultItemDto> kpiResultItems;

    public KPIResultDto() {
    }

    public KPIResultDto(KPIResult entity, Boolean isGetFull) {
        super(entity);
        if (entity != null) {
            if (isGetFull) {
                if (entity.getStaff() != null) {
                    staff = new StaffDto(entity.getStaff(), false);
                }
                if (entity.getKpi() != null) {
                    kpi = new KPIDto(entity.getKpi(), true);
                }
                if (entity.getKpiResultItems() != null && !entity.getKpiResultItems().isEmpty()) {
                    this.kpiResultItems = new ArrayList<>();
                    for (KPIResultItem item : entity.getKpiResultItems()) {
                        kpiResultItems.add(new KPIResultItemDto(item, false));
                    }
                }
            }
        }
    }

    public KPIResultDto(KPIResult entity) {
        this(entity, true);
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public KPIDto getKpi() {
        return kpi;
    }

    public void setKpi(KPIDto kpi) {
        this.kpi = kpi;
    }

    public List<KPIResultItemDto> getKpiResultItems() {
        return kpiResultItems;
    }

    public void setKpiResultItems(List<KPIResultItemDto> kpiResultItems) {
        this.kpiResultItems = kpiResultItems;
    }
}
