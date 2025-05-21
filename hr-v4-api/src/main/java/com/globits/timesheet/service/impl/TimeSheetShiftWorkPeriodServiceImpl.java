package com.globits.timesheet.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.ShiftWorkTimePeriod;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.ShiftWorkTimePeriodDto;
import com.globits.hr.service.UserExtService;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.timesheet.domain.TimeSheet;
import com.globits.timesheet.domain.TimeSheetShiftWorkPeriod;
import com.globits.timesheet.dto.TimeSheetDto;
import com.globits.timesheet.dto.TimeSheetShiftWorkPeriodDto;
import com.globits.timesheet.repository.TimeSheetDetailRepository;
import com.globits.timesheet.repository.TimeSheetRepository;
import com.globits.timesheet.repository.TimeSheetShiftWorkPeriodRepository;
import com.globits.timesheet.service.TimeSheetShiftWorkPeriodService;

import jakarta.persistence.Query;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TimeSheetShiftWorkPeriodServiceImpl extends GenericServiceImpl<TimeSheetShiftWorkPeriod, UUID> implements TimeSheetShiftWorkPeriodService {
    @Autowired
    private TimeSheetShiftWorkPeriodRepository repos;

    @Autowired
    private UserExtService userExtService;

    @Autowired
    private TimeSheetRepository timeSheetRepository;

    @Override
    public TimeSheetShiftWorkPeriod getEntityById(UUID id) {
        TimeSheetShiftWorkPeriod entity = null;
        Optional<TimeSheetShiftWorkPeriod> projectOptional = repos.findById(id);
        if (projectOptional.isPresent()) {
            entity = projectOptional.get();
        }
        return entity;
    }

    @Override
    public List<TimeSheetShiftWorkPeriodDto> getListTSShiftWorkPeriodsDoNotHaveAnyTimeSheetDetail(UUID staffId, Date fromDate, Date toDate) {
        if (staffId == null || fromDate == null || toDate == null) return null;
        Staff currentStaff = userExtService.getCurrentStaffEntity();
//        if (currentStaff == null || currentStaff.getId() == null) return null;

        Date fromDateBegin = DateTimeUtil.getStartOfDay(fromDate);
        Date toDateEnd = DateTimeUtil.getEndOfDay(toDate);

        String subQueryGetItemDoNotHaveAnyTSD = "(select count(distinct tsd.id) from TimeSheetDetail tsd " +
                "where tsd.timeSheetShiftWorkPeriod.id = entity.id)";

        String mainQuery = "select distinct new com.globits.timesheet.dto.TimeSheetShiftWorkPeriodDto(entity) from TimeSheetShiftWorkPeriod entity " +
                "where entity.timeSheet.workingDate >= :fromDateBegin and entity.timeSheet.workingDate <= :toDateEnd " +
                "and entity.timeSheet.staff.id = :staffId and " + subQueryGetItemDoNotHaveAnyTSD + " = 0";

        Query query = manager.createQuery(mainQuery, TimeSheetShiftWorkPeriodDto.class);

        query.setParameter("staffId", staffId);
        query.setParameter("fromDateBegin", fromDateBegin);
        query.setParameter("toDateEnd", toDateEnd);

        List<TimeSheetShiftWorkPeriodDto> result = query.getResultList();
        return result;
    }

    @Override
    public TimeSheetShiftWorkPeriod findTSShiftWorkPeriodWrapTimeOfStaff(Date time, UUID staffId) {
        if (time == null || staffId == null) return null;

        //find timesheet which wraps input time of staff
        List<TimeSheet> availableTimeSheets = timeSheetRepository.findTimeSheetWrapsInputTimeOfStaff(time, staffId);
        if (availableTimeSheets == null || availableTimeSheets.size() == 0) {
            //user hasn't taken attendance in this timesheet
            return null;
        }

        for (TimeSheet timeSheet : availableTimeSheets) {
            //get all work shift of staff in this day (timesheet)
            if (timeSheet.getTimeSheetShiftWorkPeriod() != null
                    && timeSheet.getTimeSheetShiftWorkPeriod().size() > 0) {
                //subTaskItem can be in status DOING in multiple shifts, in many days
                for (TimeSheetShiftWorkPeriod timeSheetShiftWorkPeriod : timeSheet.getTimeSheetShiftWorkPeriod()) {
                    //checking for work shift of the day
                    ShiftWorkTimePeriod shiftWork = timeSheetShiftWorkPeriod.getShiftWorkTimePeriod();
                    Date shiftStartTime = DateTimeUtil.setTimeToDate(
                            shiftWork.getStartTime(),
                            timeSheetShiftWorkPeriod.getTimeSheet().getWorkingDate());
                    Date shiftEndTime = DateTimeUtil.setTimeToDate(shiftWork.getEndTime(),
                            timeSheetShiftWorkPeriod.getTimeSheet().getWorkingDate());

                    if (shiftStartTime == null || shiftEndTime == null) continue;
                    if (shiftStartTime.getTime() <= time.getTime() && time.getTime() <= shiftEndTime.getTime()) {
                        //THIS IS MY NEED
                        return timeSheetShiftWorkPeriod;
                    }
                }
            }
        }

        //NOT FOUND satisfied timesheet shift period
        return null;
    }

    @Override
    public double calculateTotalWorkingTime(List<TimeSheetShiftWorkPeriod> tsdsOfStaff) {
        if (tsdsOfStaff == null || tsdsOfStaff.isEmpty()) {
            return 0.0;
        }

        double totalWorkingHours = 0.0;

        for (TimeSheetShiftWorkPeriod tsd : tsdsOfStaff) {
            ShiftWorkTimePeriod shiftWorkTimePeriod = tsd.getShiftWorkTimePeriod();
            if (shiftWorkTimePeriod != null) {
                Date startTime = shiftWorkTimePeriod.getStartTime();
                Date endTime = shiftWorkTimePeriod.getEndTime();

                if (startTime != null && endTime != null) {
                    long durationInMillis = endTime.getTime() - startTime.getTime();
                    double durationInHours = TimeUnit.MILLISECONDS.toHours(durationInMillis)
                            + (TimeUnit.MILLISECONDS.toMinutes(durationInMillis) % 60) / 60.0;
                    totalWorkingHours += durationInHours;
                }
            }
        }

        return totalWorkingHours;
    }
}
