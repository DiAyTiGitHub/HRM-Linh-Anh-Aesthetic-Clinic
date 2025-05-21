package com.globits.timesheet.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.timesheet.domain.TimeSheetLabel;

public class TimeSheetLabelDto extends BaseObjectDto {
    private TimeSheetDto timeSheet;
    private LabelDto label;

    public TimeSheetLabelDto(TimeSheetLabel entity) {
        if (entity != null) {
            if (entity.getTimesheet() != null) {
                this.timeSheet = new TimeSheetDto(entity.getTimesheet());
            }
            if (entity.getLabel() != null) {
                this.label = new LabelDto(entity.getLabel());
            }
        }
    }

    public TimeSheetDto getTimeSheet() {
        return timeSheet;
    }

    public void setTimeSheet(TimeSheetDto timeSheet) {
        this.timeSheet = timeSheet;
    }

    public LabelDto getLabel() {
        return label;
    }

    public void setLabel(LabelDto label) {
        this.label = label;
    }
}
