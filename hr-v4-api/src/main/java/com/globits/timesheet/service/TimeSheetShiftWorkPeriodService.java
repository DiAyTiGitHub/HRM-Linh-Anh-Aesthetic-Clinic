package com.globits.timesheet.service;

import com.globits.core.service.GenericService;
import com.globits.timesheet.domain.TimeSheetShiftWorkPeriod;
import com.globits.timesheet.dto.TimeSheetShiftWorkPeriodDto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface TimeSheetShiftWorkPeriodService extends GenericService<TimeSheetShiftWorkPeriod, UUID> {

    public TimeSheetShiftWorkPeriod getEntityById(UUID id);

    public List<TimeSheetShiftWorkPeriodDto> getListTSShiftWorkPeriodsDoNotHaveAnyTimeSheetDetail(UUID staffId, Date fromDate, Date toDate);

    public TimeSheetShiftWorkPeriod findTSShiftWorkPeriodWrapTimeOfStaff(Date time, UUID staffId);

    public double calculateTotalWorkingTime(List<TimeSheetShiftWorkPeriod> tsdsOfStaff);
}
