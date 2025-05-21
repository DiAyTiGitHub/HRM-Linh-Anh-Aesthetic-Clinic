package com.globits.hr.service.impl;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.globits.hr.domain.*;
import com.globits.hr.dto.staff.StaffLabourUtilReportDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.*;
import com.globits.salary.domain.SalaryPeriod;
import com.globits.salary.dto.SalaryPeriodDto;
import com.globits.salary.service.SalaryPeriodService;
import com.globits.timesheet.domain.LeaveRequest;
import com.globits.timesheet.domain.LeaveType;
import com.globits.timesheet.domain.TimeSheetDetail;
import com.globits.timesheet.dto.search.SearchStaffWorkScheduleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.ShiftWorkDto;
import com.globits.hr.dto.ShiftWorkTimePeriodDto;
import com.globits.hr.dto.StaffWorkScheduleDto;
import com.globits.salary.service.SalaryResultStaffItemService;
import com.globits.timesheet.dto.TimeSheetDetailDto;
import com.globits.timesheet.repository.LeaveRequestRepository;
import com.globits.timesheet.repository.LeaveTypeRepository;
import com.globits.timesheet.repository.OvertimeRequestRepository;
import com.globits.timesheet.repository.TimeSheetDetailRepository;
import com.globits.timesheet.service.LeaveTypeService;

import jakarta.persistence.EntityManager;

@Service
public class CalculateStaffWorkTimeServiceV2Impl extends GenericServiceImpl<StaffWorkSchedule, UUID>
        implements CalculateStaffWorkTimeServiceV2 {
    private static final Logger logger = LoggerFactory.getLogger(StaffServiceImpl.class);

    @Autowired
    private ShiftWorkRepository shiftWorkRepository;

    @Autowired
    private StaffWorkScheduleService staffWorkScheduleService;

    @Autowired
    private ShiftWorkTimePeriodRepository shiftWorkTimePeriodRepository;

    @Autowired
    private StaffHierarchyService staffHierarchyService;

    @Autowired
    private StaffWorkScheduleRepository staffWorkScheduleRepository;

    @Autowired
    private StaffWorkScheduleShiftPeriodRepository staffWorkScheduleShiftPeriodRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private UserExtService userExtService;

    @Autowired
    private TimeSheetDetailRepository timeSheetDetailRepository;

    @Autowired
    private SalaryResultStaffItemService salaryResultStaffItemService;

    @Autowired
    private OvertimeRequestRepository overtimeRequestRepository;

    @Autowired
    private AbsenceRequestRepository absenceRequestRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private HRDepartmentRepository hRDepartmentRepository;

    @Autowired
    private StaffService staffService;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private LeaveTypeService leaveTypeService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private StaffMaternityHistoryService staffMaternityHistoryService;

    @Autowired
    private SalaryPeriodService salaryPeriodService;


    @Override
    public StaffWorkScheduleDto calculateStaffWorkTimeAndSave(UUID staffWorkScheduleId) {
        if (staffWorkScheduleId == null)
            return null;

        StaffWorkSchedule entity = staffWorkScheduleRepository.findById(staffWorkScheduleId).orElse(null);
        if (entity == null)
            return null;

        staffMaternityHistoryService.handleSetDuringPregnancyStatus(entity);

        StaffWorkScheduleDto inputDto = new StaffWorkScheduleDto(entity);

        // Ca làm việc đã bị khóa => Không tính toán lại
        if (entity.getIsLocked() != null && entity.getIsLocked().equals(true)) {
            return inputDto;
        }

        StaffWorkScheduleDto recalculatedSchedule = this.calculateStaffWorkTime(inputDto,
                inputDto.getTimeSheetDetails());

        StaffWorkScheduleDto response = this.saveScheduleStatistic(recalculatedSchedule);

        return response;
    }

    @Override
    public StaffWorkScheduleDto saveScheduleStatistic(StaffWorkScheduleDto dto) {
        if (dto == null)
            return null;

        StaffWorkSchedule entity = null;

        if (dto.getId() != null) {
            entity = staffWorkScheduleRepository.findById(dto.getId()).orElse(null);
        }
        // Xử lý cho trường hợp tạo thủ công 1 ca làm việc cho 1 nhân viên
        else if (dto.getStaff() != null && dto.getWorkingDate() != null && dto.getShiftWork() != null) {
            List<StaffWorkSchedule> availableSchedules = staffWorkScheduleRepository
                    .getByStaffIdAndShiftWorkIdAndWorkingDate(dto.getStaff().getId(), dto.getShiftWork().getId(),
                            dto.getWorkingDate());

            if (availableSchedules != null && !availableSchedules.isEmpty()) {
                entity = availableSchedules.get(0);
            }
        }

        // Ca làm việc này được tạo mới hay không, Nếu được tạo mới => Tự động điền phân
        // loại thời gian nghỉ nếu có nghỉ
        boolean isCreateNew = false;

        if (entity == null) {
            entity = new StaffWorkSchedule();
            isCreateNew = true;
        }

        // Set các trường thống kê từ DTO sang entity
        entity.setLateArrivalCount(dto.getLateArrivalCount());
        entity.setLateArrivalMinutes(dto.getLateArrivalMinutes());
        entity.setEarlyExitCount(dto.getEarlyExitCount());
        entity.setEarlyExitMinutes(dto.getEarlyExitMinutes());
        entity.setEarlyArrivalMinutes(dto.getEarlyArrivalMinutes());
        entity.setLateExitMinutes(dto.getLateExitMinutes());
        entity.setTotalPaidWork(dto.getTotalPaidWork());
        entity.setTotalValidHours(dto.getTotalValidHours());

        entity.setEstimatedWorkingHours(dto.getEstimatedWorkingHours());
        entity.setTotalHours(dto.getTotalHours());
        entity.setConvertedWorkingHours(dto.getConvertedWorkingHours());
        entity.setPaidLeaveHours(dto.getPaidLeaveHours());
        entity.setUnpaidLeaveHours(dto.getUnpaidLeaveHours());
        entity.setPaidLeaveWorkRatio(dto.getPaidLeaveWorkRatio());
        entity.setUnpaidLeaveWorkRatio(dto.getUnpaidLeaveWorkRatio());
        entity.setWorkingStatus(dto.getWorkingStatus());

        entity.setConfirmedOTHoursBeforeShift(dto.getConfirmedOTHoursBeforeShift());
        entity.setConfirmedOTHoursAfterShift(dto.getConfirmedOTHoursAfterShift());

//        staffMaternityHistoryService.handleSetDuringPregnancyStatus(entity);


        if (dto.getLeaveType() != null && dto.getLeaveType().getId() != null) {
            LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveType().getId()).orElse(null);
            entity.setLeaveType(leaveType);
        } else {
            entity.setLeaveType(null);
        }
        if (entity.getLeaveScheduleShiftPeriods() == null) {
            entity.setLeaveScheduleShiftPeriods(new HashSet<>());
        }

        entity.getLeaveScheduleShiftPeriods().clear();
        entity.setDuringPregnancy(dto.getDuringPregnancy());

        if (dto.getLeavePeriod() != null && entity.getLeaveType() != null) {
            List<StaffWorkScheduleShiftPeriod> scheduleShiftPeriods = staffWorkScheduleShiftPeriodRepository
                    .getByScheduleIdAndLeavePeriodId(entity.getId(), dto.getLeavePeriod().getId());

            StaffWorkScheduleShiftPeriod scheduleLeavePeriodEntity = null;

            if (scheduleShiftPeriods == null || scheduleShiftPeriods.isEmpty()) {
                scheduleLeavePeriodEntity = new StaffWorkScheduleShiftPeriod();
            } else {
                scheduleLeavePeriodEntity = scheduleShiftPeriods.get(0);
            }

            ShiftWorkTimePeriod leavePeriodEntity = shiftWorkTimePeriodRepository.findById(dto.getLeavePeriod().getId())
                    .orElse(null);

            scheduleLeavePeriodEntity.setLeavePeriod(leavePeriodEntity);
            scheduleLeavePeriodEntity.setSchedule(entity);

            entity.getLeaveScheduleShiftPeriods().add(scheduleLeavePeriodEntity);
        }

//        // Lưu giai đoạn nghỉ trong ca
//        if (dto.getLeavePeriods() != null && !dto.getLeavePeriods().isEmpty()) {
//
//            for (ShiftWorkTimePeriodDto leavePeriod : dto.getLeavePeriods()) {
//                if (leavePeriod == null || leavePeriod.getId() == null) continue;
//
//                List<StaffWorkScheduleShiftPeriod> scheduleShiftPeriods = staffWorkScheduleShiftPeriodRepository.getByScheduleIdAndLeavePeriodId(entity.getId(), leavePeriod.getId());
//
//                StaffWorkScheduleShiftPeriod scheduleLeavePeriodEntity = null;
//
//                if (scheduleShiftPeriods == null || scheduleShiftPeriods.isEmpty()) {
//                    scheduleLeavePeriodEntity = new StaffWorkScheduleShiftPeriod();
//                } else {
//                    scheduleLeavePeriodEntity = scheduleShiftPeriods.get(0);
//                }
//
//                ShiftWorkTimePeriod leavePeriodEntity = shiftWorkTimePeriodRepository.findById(leavePeriod.getId()).orElse(null);
//                if (leavePeriodEntity == null) continue;
//
//                scheduleLeavePeriodEntity.setLeavePeriod(leavePeriodEntity);
//                scheduleLeavePeriodEntity.setSchedule(entity);
//
//                entity.getLeaveScheduleShiftPeriods().add(scheduleLeavePeriodEntity);
//            }
//        }

        // Lưu lại entity
        StaffWorkSchedule response = staffWorkScheduleRepository.saveAndFlush(entity);

        return new StaffWorkScheduleDto(response);
    }

    @Override
    public StaffWorkScheduleDto calculateStaffWorkTime(StaffWorkScheduleDto staffWorkSchedule,
                                                       List<TimeSheetDetailDto> timeSheetDetails) {
        if (staffWorkSchedule == null
                // Ca làm việc đã được chốt công
                || (staffWorkSchedule.getIsLocked() != null && staffWorkSchedule.getIsLocked()))
            return null;

        // KHÔNG NGHỈ PHÉP => Thống kê kết quả làm việc thông thường
        if (staffWorkSchedule.getLeaveType() == null) {
            return calculateStaffWorkTimeInNormalCase(staffWorkSchedule, timeSheetDetails);
        }

        // CÓ NGHỈ PHÉP
        // Là nghỉ nửa ca => Thống kê kết quả làm việc với TH nghỉ nửa ca
        if (isHalfTimeShiftLeave(staffWorkSchedule.getLeaveType().getCode())
                && staffWorkSchedule.getLeavePeriods() != null && !staffWorkSchedule.getLeavePeriods().isEmpty()) {
            return calculateStaffWorkTimeInHalfLeaveCase(staffWorkSchedule, timeSheetDetails);
        }

        // Là nghỉ cả ca làm việc => Thống kê kết quả làm việc với TH nghỉ cả ngày
        return calculateStaffWorkTimeInFullLeaveCase(staffWorkSchedule, timeSheetDetails);
    }

    // Thống kê kết quả làm việc trong trường hợp thông thường
    @Override
    public StaffWorkScheduleDto calculateStaffWorkTimeInNormalCase(StaffWorkScheduleDto staffWorkSchedule,
                                                                   List<TimeSheetDetailDto> timeSheetDetails) {
        if (staffWorkSchedule == null)
            return null;
        if (timeSheetDetails == null)
            timeSheetDetails = new ArrayList<>();

        ShiftWorkDto shiftWork = staffWorkSchedule.getShiftWork();
        if (shiftWork == null)
            return null;

        if (shiftWork.getTotalHours() == null) {
            shiftWork.setTotalHours(0D);
        }

        // Số giờ làm việc dự kiến
        Double estimatedWorkingHours = shiftWork.getTotalHours();
        staffWorkSchedule.setEstimatedWorkingHours(estimatedWorkingHours);

        // Thống kê các chỉ số liên quan đến đi muộn/sớm/số lần
//        if (!timeSheetDetails.isEmpty()) {
        Date firstCheckIn = getFirstCheckin(timeSheetDetails);
        Date shiftWorkStartTime = this.getShiftWorkStartTime(shiftWork);
        Double allowedLateMinutes = this.getAllowedLateMinutes(shiftWork);

        // Số lần đi sớm
        // Số phút đi sớm
        // Số phút đi muộn
        this.handleSetCheckInStatistics(staffWorkSchedule, shiftWorkStartTime, firstCheckIn, allowedLateMinutes);
//        }

        // Thống kê các chỉ số liên quan đến về muộn/sớm/số lần
//        if (!timeSheetDetails.isEmpty()) {
        Date lastCheckout = this.getLastCheckout(timeSheetDetails);
        Date shiftWorkEndTime = this.getShiftWorkEndTime(shiftWork);

        // Số lần về sớm
        // Số phút về sớm
        // Số phút về muộn
        this.handleSetCheckoutStatistics(staffWorkSchedule, shiftWorkEndTime, lastCheckout);
//        }

        // Số giờ thực tế nhân viên đã làm việc của ca này
        Double totalHours = calculateTotalWorkHoursByTimesheetDetails(timeSheetDetails);
        staffWorkSchedule.setTotalHours(totalHours);

        // Số giờ hợp lệ được tính trong ca làm việc
        Double totalValidHours = calculateTotalValidWorkHoursByTimesheetDetails(timeSheetDetails, shiftWork, null);
        staffWorkSchedule.setTotalValidHours(totalValidHours);

        // Số giờ nghỉ được tính công
        Double paidLeaveHours = 0D;
        staffWorkSchedule.setPaidLeaveHours(paidLeaveHours);

        // Số giờ nghỉ không được tính công
        Double unpaidLeaveHours = shiftWork.getTotalHours() - totalValidHours - paidLeaveHours;
        if (unpaidLeaveHours < 0)
            unpaidLeaveHours = 0D;
        staffWorkSchedule.setUnpaidLeaveHours(unpaidLeaveHours);

        // Số giờ công quy đổi
        Double convertedWorkingHours = calculateConvertedWorkingHours(staffWorkSchedule);
        staffWorkSchedule.setConvertedWorkingHours(convertedWorkingHours);

        // Tỷ lệ công được tính trong ca được phân
        // TH: Tính ngày công dựa trên tỉ lệ số giờ đã làm việc
//        Double totalPaidWork = calculateWorkRatio(totalValidHours, shiftWork);
        // TH: Tính ngày công dựa trên tỉ lệ ngày công được định nghĩa trong từng giai
        // đoạn làm việc
//        Double totalPaidWork = calculateWorkRatioByPeriodsRatio(timeSheetDetails, shiftWork, null);
        // TH: Mặc định nếu đã chấm công thì tính nửa ngày công
        Double totalPaidWork = calculateDefaultWorkRatio(totalValidHours, shiftWork);
        staffWorkSchedule.setTotalPaidWork(totalPaidWork);

        // Tỷ lệ công nghỉ phép được hưởng lương trong ca làm việc
        Double paidLeaveWorkRatio = 0D;
        staffWorkSchedule.setPaidLeaveWorkRatio(paidLeaveWorkRatio);

        // Tỷ lệ công nghỉ phép KHÔNG được hưởng lương trong ca làm việc
        Double totalShiftWorkRatio = this.getTotalWorkRatio(shiftWork); // Tổng tỷ lệ ngày công của ca làm việc
        Double unpaidLeaveWorkRatio = totalShiftWorkRatio - totalPaidWork;
        staffWorkSchedule.setUnpaidLeaveWorkRatio(unpaidLeaveWorkRatio);

        if (totalPaidWork == 0D) {
            this.handleResetEarlyLateStatistics(staffWorkSchedule);
        }

        // Xử lý trường hợp tính đi muộn về sớm cho nhân viên đang trong quá trình mang thai
        this.handleScheduleDuringPregnancy(staffWorkSchedule);

        // Thời gian làm việc để tính đủ công của cả giai đoạn
        Double totalMinWorkTimeHour = this.getTotalMinWorkTimeHours(shiftWork, null);

        if (totalValidHours >= totalMinWorkTimeHour) {
            staffWorkSchedule.setWorkingStatus(HrConstants.StaffWorkScheduleWorkingStatus.FULL_ATTENDANCE.getValue());
        } else if (totalValidHours > 0) {
            staffWorkSchedule
                    .setWorkingStatus(HrConstants.StaffWorkScheduleWorkingStatus.PARTIAL_ATTENDANCE.getValue());
        } else {
            staffWorkSchedule.setWorkingStatus(HrConstants.StaffWorkScheduleWorkingStatus.NOT_ATTENDANCE.getValue());
        }

        return staffWorkSchedule;
    }

    // đặt lại dữ liệu thống kê liên quan đi sớm về muộn
    private void handleResetEarlyLateStatistics(StaffWorkScheduleDto staffWorkSchedule) {
        // số phút đi làm muộn
        staffWorkSchedule.setLateArrivalCount(0);
        staffWorkSchedule.setLateArrivalMinutes(0);
        // số phút đi làm sớm
        staffWorkSchedule.setEarlyArrivalMinutes(0);

        staffWorkSchedule.setEarlyExitCount(0);
        staffWorkSchedule.setEarlyExitMinutes(0);
        staffWorkSchedule.setLateExitMinutes(0);
    }

    // Thống kê các chỉ số liên quan đến đi muộn/sớm/số lần theo giai đoạn làm việc
    private void handleSetStatisticsCheckOutByPeriod(StaffWorkScheduleDto staffWorkSchedule,
                                                     ShiftWorkTimePeriodDto shiftWorkTimePeriod, List<TimeSheetDetailDto> timeSheetDetails) {
        // Số lần về sớm
        int earlyExitCount = 0;
        // Số phút về sớm
        int earlyExitMinutes = 0;
        // Số phút về muộn
        int lateExitMinutes = 0;

        Date lastCheckout = this.getLastCheckout(timeSheetDetails);
        Date periodEndTime = shiftWorkTimePeriod.getEndTime();

        if (lastCheckout == null || periodEndTime == null
                // Nhân viên không cần tính đi muộn về sớm
                || (staffWorkSchedule.getStaff().getSkipLateEarlyCount() != null
                && staffWorkSchedule.getStaff().getSkipLateEarlyCount().equals(true))
        ) {
            staffWorkSchedule.setEarlyExitCount(0);
            staffWorkSchedule.setEarlyExitMinutes(0);
            staffWorkSchedule.setLateExitMinutes(0);
            return;
        }

        ZoneId zone = ZoneId.systemDefault();
        LocalTime checkoutTime = lastCheckout.toInstant().atZone(zone).toLocalTime();
        LocalTime shiftEnd = periodEndTime.toInstant().atZone(zone).toLocalTime();

        if (checkoutTime.isBefore(shiftEnd)) { // Về sớm
            // Số lần về sớm
            earlyExitCount += 1;
            // Số phút về sớm
            earlyExitMinutes += this.calculateEarlyMinutes(checkoutTime, shiftEnd);
            // Số phút về muộn
            lateExitMinutes += 0;
        } else if (checkoutTime.isAfter(shiftEnd)) { // Về muộn
            // Số lần về sớm
            earlyExitCount += 0;
            // Số phút về sớm
            earlyExitMinutes += 0;
            // Số phút về muộn
            lateExitMinutes += this.calculateLateMinutes(checkoutTime, shiftEnd);
        } else { // Đúng giờ
            // Số lần về sớm
            earlyExitCount += 0;
            // Số phút về sớm
            earlyExitMinutes += 0;
            // Số phút về muộn
            lateExitMinutes += 0;
        }

        staffWorkSchedule.setEarlyExitCount(earlyExitCount);
        staffWorkSchedule.setEarlyExitMinutes(earlyExitMinutes);
        staffWorkSchedule.setLateExitMinutes(lateExitMinutes);
    }

    // Thống kê các chỉ số liên quan đến đi muộn/sớm/số lần
    private void handleSetStatisticsCheckInByPeriod(StaffWorkScheduleDto staffWorkSchedule,
                                                    ShiftWorkTimePeriodDto shiftWorkTimePeriod, List<TimeSheetDetailDto> timeSheetDetails) {
        // Số lần đi sớm
        int lateArrivalCount = 0;
        // Số phút đi sớm
        int earlyArrivalMinutes = 0;
        // Số phút đi muộn
        int lateArrivalMinutes = 0;

        Date firstCheckIn = getFirstCheckin(timeSheetDetails);
        Date periodStartTime = shiftWorkTimePeriod.getStartTime();
        Double allowedLateMinutes = shiftWorkTimePeriod.getAllowedLateMinutes();
        if (allowedLateMinutes == null) {
            allowedLateMinutes = 0D;
        }

        if (firstCheckIn == null || periodStartTime == null
                // Nhân viên không cần tính đi muộn về sớm
                || (staffWorkSchedule.getStaff().getSkipLateEarlyCount() != null
                && staffWorkSchedule.getStaff().getSkipLateEarlyCount().equals(true))
        ) {
            staffWorkSchedule.setLateArrivalCount(0);
            staffWorkSchedule.setLateArrivalMinutes(0);
            staffWorkSchedule.setEarlyArrivalMinutes(0);
            return;
        }

        ZoneId zone = ZoneId.systemDefault();
        LocalTime checkInTime = firstCheckIn.toInstant().atZone(zone).toLocalTime();
        LocalTime periodStart = periodStartTime.toInstant().atZone(zone).toLocalTime();
        // Giả sử periodStart là 08:00, allowedLateTime = 10 (10 phút cho phép)
        LocalTime allowedLateTime = periodStart.plusMinutes(allowedLateMinutes.longValue());

        if (checkInTime.isAfter(periodStart)) { // Đi làm muộn
            // Thêm 1 lần đi muộn
            if (checkInTime.isAfter(allowedLateTime)) {
                lateArrivalCount += 1;
            }
            // số phút đi làm muộn
            lateArrivalMinutes += (this.calculateLateMinutes(checkInTime, periodStart));
            // số phút đi làm sớm
            earlyArrivalMinutes += 0;
        } else if (checkInTime.isBefore(periodStart)) { // Đến sớm

            lateArrivalCount += (0);
            // số phút đi làm muộn
            lateArrivalMinutes += (0);
            // số phút đi làm sớm
            earlyArrivalMinutes += (this.calculateEarlyMinutes(checkInTime, periodStart));
        }

        staffWorkSchedule.setLateArrivalCount(lateArrivalCount);
        staffWorkSchedule.setLateArrivalMinutes(lateArrivalMinutes);
        staffWorkSchedule.setEarlyArrivalMinutes(earlyArrivalMinutes);
    }

    private List<TimeSheetDetailDto> getDetailsOfShiftPeriod(ShiftWorkTimePeriodDto period,
                                                             List<TimeSheetDetailDto> timeSheetDetails) {
        List<TimeSheetDetailDto> response = new ArrayList<>();

        for (TimeSheetDetailDto timeSheetDetail : timeSheetDetails) {
            if (timeSheetDetail == null || timeSheetDetail.getShiftWorkTimePeriod() == null
                    || !timeSheetDetail.getShiftWorkTimePeriod().getId().equals(period.getId())) {
                continue;
            }

            response.add(timeSheetDetail);
        }

        return response;
    }

    private boolean isLeaveUnpaid(StaffWorkScheduleDto staffWorkSchedule) {
        if (staffWorkSchedule == null || staffWorkSchedule.getLeaveType() == null)
            return true;

        if (staffWorkSchedule.getLeaveType() != null && (staffWorkSchedule.getLeaveType().getIsPaid() == null
                || staffWorkSchedule.getLeaveType().getIsPaid().equals(false)))
            return true;

        return false;
    }

    // Thống kê kết quả làm việc trong trường hợp nghỉ nửa ca
    @Override
    public StaffWorkScheduleDto calculateStaffWorkTimeInHalfLeaveCase(StaffWorkScheduleDto staffWorkSchedule,
                                                                      List<TimeSheetDetailDto> timeSheetDetails) {
        if (staffWorkSchedule == null || staffWorkSchedule.getLeavePeriods() == null
                || staffWorkSchedule.getLeavePeriods().isEmpty())
            return null;

        if (timeSheetDetails == null)
            timeSheetDetails = new ArrayList<>();

        ShiftWorkDto shiftWork = staffWorkSchedule.getShiftWork();
        if (shiftWork == null) {
            return null;
        }

        if (shiftWork.getTotalHours() == null) {
            shiftWork.setTotalHours(0D);
        }

        // Giai đoạn cần tiếp tục làm việc
        ShiftWorkTimePeriodDto workPeriod = this.getStillWorkPeriod(shiftWork, staffWorkSchedule.getLeavePeriods());

        // Số giờ làm việc dự kiến
        Double estimatedWorkingHours = getTotalHoursOfPeriod(workPeriod);
        staffWorkSchedule.setEstimatedWorkingHours(estimatedWorkingHours);

        // Thống kê các chỉ số liên quan đến đi muộn/sớm/số lần
        handleSetStatisticsCheckInByPeriod(staffWorkSchedule, workPeriod, timeSheetDetails);

        // Thống kê các chỉ số liên quan đến về muộn/sớm/số lần
        handleSetStatisticsCheckOutByPeriod(staffWorkSchedule, workPeriod, timeSheetDetails);

        // Số giờ thực tế nhân viên đã làm việc của ca này
        Double totalHours = calculateTotalWorkHoursByTimesheetDetails(timeSheetDetails);
        staffWorkSchedule.setTotalHours(totalHours);

        // Số giờ hợp lệ được tính trong ca làm việc
        Double totalValidHours = calculateTotalValidWorkHoursByTimesheetDetails(timeSheetDetails, shiftWork,
                staffWorkSchedule.getLeavePeriods());
        staffWorkSchedule.setTotalValidHours(totalValidHours);

        // Số giờ nghỉ được tính công
        Double paidLeaveHours = 0D;
        for (ShiftWorkTimePeriodDto leavePeriod : staffWorkSchedule.getLeavePeriods()) {
            if (isLeaveUnpaid(staffWorkSchedule)) continue;

            paidLeaveHours += getTotalHoursOfPeriod(leavePeriod);
        }
        staffWorkSchedule.setPaidLeaveHours(paidLeaveHours);

        // Số giờ nghỉ không được tính công
        Double unpaidLeaveHours = shiftWork.getTotalHours() - totalValidHours - paidLeaveHours;
        if (unpaidLeaveHours < 0)
            unpaidLeaveHours = 0D;
        staffWorkSchedule.setUnpaidLeaveHours(unpaidLeaveHours);

        // Số giờ công quy đổi
        Double convertedWorkingHours = calculateConvertedWorkingHours(staffWorkSchedule);
        staffWorkSchedule.setConvertedWorkingHours(convertedWorkingHours);

        // Tỷ lệ công được tính trong ca được phân
        // Tính tỷ ngày công
        // nếu đã đi làm thì đặt tỷ lệ ngày công là tỷ lệ của ca làm việc
        Double totalPaidWork = 0D;
        if (totalValidHours > 0) {
            totalPaidWork += workPeriod.getWorkRatio();
        }
        staffWorkSchedule.setTotalPaidWork(totalPaidWork);

        // Tỷ lệ công nghỉ phép được hưởng lương trong ca làm việc
        Double paidLeaveWorkRatio = 0D;
        for (ShiftWorkTimePeriodDto leavePeriod : staffWorkSchedule.getLeavePeriods()) {
            if (isLeaveUnpaid(staffWorkSchedule)) continue;

            paidLeaveWorkRatio += leavePeriod.getWorkRatio();
        }
        staffWorkSchedule.setPaidLeaveWorkRatio(paidLeaveWorkRatio);

        // Tỷ lệ công nghỉ phép KHÔNG được hưởng lương trong ca làm việc
        Double totalShiftWorkRatio = this.getTotalWorkRatio(shiftWork); // Tổng tỷ lệ ngày công của ca làm việc
        Double unpaidLeaveWorkRatio = totalShiftWorkRatio - totalPaidWork - paidLeaveWorkRatio;
        if (unpaidLeaveWorkRatio < 0)
            unpaidLeaveWorkRatio = 0D;
        staffWorkSchedule.setUnpaidLeaveWorkRatio(unpaidLeaveWorkRatio);

        // Xử lý trường hợp tính đi muộn về sớm cho nhân viên đang trong quá trình mang thai
        this.handleScheduleDuringPregnancy(staffWorkSchedule);

        // Thời gian làm việc để tính đủ công của cả giai đoạn
        Double totalMinWorkTimeHour = this.getTotalMinWorkTimeHours(shiftWork,
//                staffWorkSchedule.getLeavePeriods()
                null
        );

        if (totalValidHours >= totalMinWorkTimeHour) {
            staffWorkSchedule.setWorkingStatus(HrConstants.StaffWorkScheduleWorkingStatus.FULL_ATTENDANCE.getValue());
        } else if (totalValidHours > 0) {
            staffWorkSchedule
                    .setWorkingStatus(HrConstants.StaffWorkScheduleWorkingStatus.PARTIAL_ATTENDANCE.getValue());
        } else {
            staffWorkSchedule.setWorkingStatus(HrConstants.StaffWorkScheduleWorkingStatus.NOT_ATTENDANCE.getValue());
        }

        return staffWorkSchedule;
    }

    public static Double getTotalHoursOfPeriod(ShiftWorkTimePeriodDto period) {
        if (period == null || period.getStartTime() == null || period.getEndTime() == null) {
            return 0.0;
        }

        // Chuyển Date -> LocalTime để chỉ lấy giờ và phút
        LocalTime start = period.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        LocalTime end = period.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

        long minutesBetween;
        if (end.isAfter(start)) {
            // Ví dụ: 08:00 -> 17:00
            minutesBetween = ChronoUnit.MINUTES.between(start, end);
        } else {
            // Qua đêm: Ví dụ 22:00 -> 06:00 (ngày hôm sau)
            minutesBetween = ChronoUnit.MINUTES.between(start, LocalTime.MIDNIGHT)
                    + ChronoUnit.MINUTES.between(LocalTime.MIN, end);
        }

        return minutesBetween / 60.0;
    }

    private ShiftWorkTimePeriodDto getStillWorkPeriod(ShiftWorkDto shiftWork,
                                                      List<ShiftWorkTimePeriodDto> leavePeriods) {
        if (shiftWork == null || leavePeriods == null || shiftWork.getTimePeriods() == null)
            return null;

        for (ShiftWorkTimePeriodDto period : shiftWork.getTimePeriods()) {
            if (isContainsPeriod(leavePeriods, period))
                continue;

            return period;
        }

        return null;
    }

    // Thống kê kết quả làm việc trong trường hợp nghỉ cả ca
    @Override
    public StaffWorkScheduleDto calculateStaffWorkTimeInFullLeaveCase(StaffWorkScheduleDto staffWorkSchedule,
                                                                      List<TimeSheetDetailDto> timeSheetDetails) {
        if (staffWorkSchedule == null)
            return null;
        if (timeSheetDetails == null)
            timeSheetDetails = new ArrayList<>();

        boolean isFullUnPaidLeave = this.isLeaveUnpaid(staffWorkSchedule);


        ShiftWorkDto shiftWork = staffWorkSchedule.getShiftWork();
        if (shiftWork == null)
            return null;

        if (shiftWork.getTotalHours() == null) {
            shiftWork.setTotalHours(0D);
        }

        // Số giờ làm việc dự kiến
        Double estimatedWorkingHours = 0D;
        staffWorkSchedule.setEstimatedWorkingHours(estimatedWorkingHours);

        // Thống kê các chỉ số liên quan đến đi muộn/sớm/số lần
//        if (!timeSheetDetails.isEmpty()) {
        staffWorkSchedule.setLateArrivalCount(0);
        staffWorkSchedule.setLateArrivalMinutes(0);
        staffWorkSchedule.setEarlyArrivalMinutes(0);
//        }

        // Thống kê các chỉ số liên quan đến về muộn/sớm/số lần
//        if (!timeSheetDetails.isEmpty()) {
        staffWorkSchedule.setEarlyExitCount(0);
        staffWorkSchedule.setEarlyExitMinutes(0);
        staffWorkSchedule.setLateExitMinutes(0);
//        }

//        // Số giờ thực tế nhân viên đã làm việc của ca này
//        Double totalHours = 0D;
//        staffWorkSchedule.setTotalHours(totalHours);

        // Số giờ hợp lệ được tính trong ca làm việc
        Double totalValidHours = 0D;
        staffWorkSchedule.setTotalValidHours(totalValidHours);


        // Là ca làm việc nghỉ có lương
        if (!isFullUnPaidLeave) {
            // Số giờ nghỉ được tính công
            Double paidLeaveHours = shiftWork.getTotalHours();
            staffWorkSchedule.setPaidLeaveHours(paidLeaveHours);

            // Số giờ nghỉ không được tính công
            Double unpaidLeaveHours = 0D;
            staffWorkSchedule.setUnpaidLeaveHours(unpaidLeaveHours);
        }
        // là ca làm việc nghỉ không lương
        else {
            // Số giờ nghỉ được tính công
            Double paidLeaveHours = 0D;
            staffWorkSchedule.setPaidLeaveHours(paidLeaveHours);

            // Số giờ nghỉ không được tính công
            Double unpaidLeaveHours = shiftWork.getTotalHours();
            staffWorkSchedule.setUnpaidLeaveHours(unpaidLeaveHours);
        }

        // Số giờ công quy đổi
        Double convertedWorkingHours = shiftWork.getConvertedWorkingHours();
        staffWorkSchedule.setConvertedWorkingHours(convertedWorkingHours);

        // Tỷ lệ công được tính trong ca được phân
        // Tính ngày công dựa trên tỉ lệ số giờ đã làm việc
        Double totalPaidWork = 0D;
        staffWorkSchedule.setTotalPaidWork(totalPaidWork);

        Double totalShiftWorkRatio = this.getTotalWorkRatio(shiftWork); // Tổng tỷ lệ ngày công của ca làm việc

        // Là ca làm việc nghỉ có lương
        if (!isFullUnPaidLeave) {
            // Tỷ lệ công nghỉ phép được hưởng lương trong ca làm việc
            Double paidLeaveWorkRatio = totalShiftWorkRatio;
            staffWorkSchedule.setPaidLeaveWorkRatio(paidLeaveWorkRatio);

            // Tỷ lệ công nghỉ phép KHÔNG được hưởng lương trong ca làm việc
            Double unpaidLeaveWorkRatio = 0D;
            staffWorkSchedule.setUnpaidLeaveWorkRatio(unpaidLeaveWorkRatio);
        }
        // là ca làm việc nghỉ không lương
        else {
            // Tỷ lệ công nghỉ phép được hưởng lương trong ca làm việc
            Double paidLeaveWorkRatio = 0D;
            staffWorkSchedule.setPaidLeaveWorkRatio(paidLeaveWorkRatio);

            // Tỷ lệ công nghỉ phép KHÔNG được hưởng lương trong ca làm việc
            Double unpaidLeaveWorkRatio = totalShiftWorkRatio;
            staffWorkSchedule.setUnpaidLeaveWorkRatio(unpaidLeaveWorkRatio);
        }


        staffWorkSchedule.setWorkingStatus(HrConstants.StaffWorkScheduleWorkingStatus.NOT_ATTENDANCE.getValue());

        return staffWorkSchedule;
    }

    // Là nghỉ nửa ca
    private boolean isHalfTimeShiftLeave(String leaveTypeCode) {
        if (!StringUtils.hasText(leaveTypeCode))
            return false;

        if (leaveTypeCode.equals(HrConstants.LeaveTypeCode.HALF_ANNUAL_LEAVE.getCode())
                || leaveTypeCode.equals(HrConstants.LeaveTypeCode.HALF_UNPAID_LEAVE.getCode())
                || leaveTypeCode.equals(HrConstants.LeaveTypeCode.HALF_BUSINESS_TRIP.getCode())
                || leaveTypeCode.equals(HrConstants.LeaveTypeCode.HALF_PUBLIC_HOLIDAY.getCode())
                || leaveTypeCode.equals(HrConstants.LeaveTypeCode.HALF_COMPENSATORY_LEAVE.getCode())
                || leaveTypeCode.equals(HrConstants.LeaveTypeCode.HALF_SPECIAL_LEAVE.getCode())) {
            return true;
        }

        return false;
    }

    public double calculateConvertedWorkingHours(StaffWorkScheduleDto staffWorkSchedule) {
        if (staffWorkSchedule.getTotalHours() == null || staffWorkSchedule.getShiftWork() == null
                || staffWorkSchedule.getShiftWork().getConvertedWorkingHours() == null) {
            return 0D;
        }

        double maximumConversionHours = staffWorkSchedule.getShiftWork().getConvertedWorkingHours();

        // Số giờ cần bị trừ
        double reducedHours = staffWorkSchedule.getShiftWork().getTotalHours() - staffWorkSchedule.getTotalValidHours();
        maximumConversionHours -= reducedHours;
//        if (maximumConversionHours >= HrConstants.STANDARD_CONVERTED_WORKING_HOURS * HrConstants.WORK_ENOUGH_THRESHOLD)
//            maximumConversionHours = HrConstants.STANDARD_CONVERTED_WORKING_HOURS;

        if (maximumConversionHours < 0)
            maximumConversionHours = 0;

        return maximumConversionHours;
    }

    public double calculateTotalWorkHoursByTimesheetDetails(List<TimeSheetDetailDto> timeSheetDetails) {
        if (timeSheetDetails == null || timeSheetDetails.isEmpty()) {
            return 0.0;
        }

        double totalHours = 0.0;

        for (TimeSheetDetailDto detail : timeSheetDetails) {
            if (detail.getStartTime() == null || detail.getEndTime() == null)
                continue;

            LocalTime workStart = LocalTime.of(detail.getStartTime().getHours(), detail.getStartTime().getMinutes());
            LocalTime workEnd = LocalTime.of(detail.getEndTime().getHours(), detail.getEndTime().getMinutes());

            Duration overlap = Duration.between(workStart, workEnd);
            totalHours += overlap.toMinutes() / 60.0;
        }

        return totalHours;
    }

    public double calculateTotalValidWorkHoursByTimesheetDetails(List<TimeSheetDetailDto> timeSheetDetails,
                                                                 ShiftWorkDto shiftWork, List<ShiftWorkTimePeriodDto> leavePeriods) {
        if (timeSheetDetails == null || timeSheetDetails.isEmpty() || shiftWork == null
                || shiftWork.getTimePeriods() == null) {
            return 0.0;
        }

        double totalHours = 0.0;

        for (ShiftWorkTimePeriodDto period : shiftWork.getTimePeriods()) {
            if (period.getStartTime() == null || period.getEndTime() == null)
                continue;
            // Không tính số giờ làm việc trong giai đoạn xin nghỉ
            if (isContainsPeriod(leavePeriods, period))
                continue;

            LocalTime periodStart = LocalTime.of(period.getStartTime().getHours(), period.getStartTime().getMinutes());
            LocalTime periodEnd = LocalTime.of(period.getEndTime().getHours(), period.getEndTime().getMinutes());

            for (TimeSheetDetailDto detail : timeSheetDetails) {
                if (detail.getStartTime() == null || detail.getEndTime() == null)
                    continue;

                LocalTime workStart = LocalTime.of(detail.getStartTime().getHours(),
                        detail.getStartTime().getMinutes());
                LocalTime workEnd = LocalTime.of(detail.getEndTime().getHours(), detail.getEndTime().getMinutes());

                // Tìm phần giao nhau giữa khoảng thời gian làm việc và khung thời gian ca làm
                LocalTime overlapStart = workStart.isAfter(periodStart) ? workStart : periodStart;
                LocalTime overlapEnd = workEnd.isBefore(periodEnd) ? workEnd : periodEnd;

                if (overlapStart.isBefore(overlapEnd)) {
                    Duration overlap = Duration.between(overlapStart, overlapEnd);
                    totalHours += overlap.toMinutes() / 60.0;
                }
            }
        }

        return totalHours;
    }

    private double getTotalWorkRatio(ShiftWorkDto shiftWork) {
        if (shiftWork == null || shiftWork.getTimePeriods() == null || shiftWork.getTimePeriods().isEmpty())
            return 0D;

        double totalWorkRatio = 0D;

        for (ShiftWorkTimePeriodDto period : shiftWork.getTimePeriods()) {
            // Cộng dồn tổng thời gian làm việc tối thiểu
            if (period.getWorkRatio() == null)
                period.setWorkRatio(0D);

            totalWorkRatio += period.getWorkRatio();
        }

        return totalWorkRatio;
    }

    private List<ShiftWorkTimePeriodDto> getLeavePeriodsOfSchedule() {

        return new ArrayList<>();
    }

    private boolean isContainsPeriod(List<ShiftWorkTimePeriodDto> availablePeriods, ShiftWorkTimePeriodDto findPeriod) {
        if (availablePeriods == null || findPeriod == null || availablePeriods.isEmpty())
            return false;

        for (ShiftWorkTimePeriodDto period : availablePeriods) {
            if (period.getId().equals(findPeriod.getId()))
                return true;
        }

        return false;
    }

    public double calculateWorkRatioByPeriodsRatio(List<TimeSheetDetailDto> timeSheetDetails, ShiftWorkDto shiftWork,
                                                   List<ShiftWorkTimePeriodDto> leavePeriods) {
        if (timeSheetDetails == null || timeSheetDetails.isEmpty() || shiftWork == null
                || shiftWork.getTimePeriods() == null) {
            return 0.0;
        }

        double totalRatio = 0.0;

        for (ShiftWorkTimePeriodDto period : shiftWork.getTimePeriods()) {
            if (period.getStartTime() == null || period.getEndTime() == null)
                continue;
            // Không tính số giờ làm việc trong giai đoạn xin nghỉ
            if (isContainsPeriod(leavePeriods, period))
                continue;

            LocalTime periodStart = LocalTime.of(period.getStartTime().getHours(), period.getStartTime().getMinutes());
            LocalTime periodEnd = LocalTime.of(period.getEndTime().getHours(), period.getEndTime().getMinutes());

            double totalHours = 0.0;

            for (TimeSheetDetailDto detail : timeSheetDetails) {
                if (detail.getStartTime() == null || detail.getEndTime() == null)
                    continue;

                LocalTime workStart = LocalTime.of(detail.getStartTime().getHours(),
                        detail.getStartTime().getMinutes());
                LocalTime workEnd = LocalTime.of(detail.getEndTime().getHours(), detail.getEndTime().getMinutes());

                // Tìm phần giao nhau giữa khoảng thời gian làm việc và khung thời gian ca làm
                LocalTime overlapStart = workStart.isAfter(periodStart) ? workStart : periodStart;
                LocalTime overlapEnd = workEnd.isBefore(periodEnd) ? workEnd : periodEnd;

                if (overlapStart.isBefore(overlapEnd)) {
                    Duration overlap = Duration.between(overlapStart, overlapEnd);
                    totalHours += overlap.toMinutes() / 60.0;
                }
            }

            if (totalHours >= period.getMinWorkTimeHour()) {
                totalRatio += period.getWorkRatio();
            }
        }

        return totalRatio;
    }

    // TH: Mặc định nếu đi làm thì tính 1 ngày công, còn lại bị trừ vào đi muộn về
    // sớm
    public double calculateDefaultWorkRatio(Double totalWorkHours, ShiftWorkDto shiftWork) {
        if (totalWorkHours == null || shiftWork == null || shiftWork.getTimePeriods() == null) {
            return 0.0;
        }

        if (totalWorkHours > 0) {
            Double totalShiftWorkRatio = this.getTotalWorkRatio(shiftWork); // Tổng tỷ lệ ngày công của ca làm việc
            return totalShiftWorkRatio;
        }

        return 0D;
    }

    // TH: Tỷ lệ ngày công được tính được trên số giờ đã làm việc trong ca. VD: Ca
    // kéo dài 8 tiếng, chỉ làm việc 5 tiếng => Tỷ lệ ngày công = 0.5
    public double calculateWorkRatio(Double totalWorkHours, ShiftWorkDto shiftWork) {
        if (totalWorkHours == null || shiftWork == null || shiftWork.getTimePeriods() == null) {
            return 0.0;
        }

        double totalWorkRatio = this.getTotalWorkRatio(shiftWork);
        double totalShiftWorkHours = shiftWork.getTotalHours();
        if (totalShiftWorkHours < 0)
            totalShiftWorkHours = 0D;

        if (totalShiftWorkHours == 0)
            return 0D;

        double result = totalWorkHours / totalShiftWorkHours * totalWorkRatio;

        if (result >= HrConstants.WORK_ENOUGH_THRESHOLD * totalWorkRatio)
            return totalWorkRatio;
        if (result >= HrConstants.WORK_ENOUGH_THRESHOLD * totalWorkRatio / 2)
            return totalWorkRatio / 2;

        return 0D;
    }

    // Thống kê các chỉ số liên quan đến Checkin trong ca làm việc
    private void handleSetCheckInStatistics(StaffWorkScheduleDto staffWorkSchedule, Date shiftStartTime,
                                            Date firstCheckin, Double allowedLateTimePeriodFirst) {

        if (firstCheckin == null || shiftStartTime == null
                // Nhân viên không cần tính đi muộn về sớm
                || (staffWorkSchedule.getStaff().getSkipLateEarlyCount() != null
                && staffWorkSchedule.getStaff().getSkipLateEarlyCount().equals(true))
        ) {
            // số phút đi làm muộn
            staffWorkSchedule.setLateArrivalCount(0);
            staffWorkSchedule.setLateArrivalMinutes(0);
            // số phút đi làm sớm
            staffWorkSchedule.setEarlyArrivalMinutes(0);

            return;
        }

        ZoneId zone = ZoneId.systemDefault();
        LocalTime checkinTime = firstCheckin.toInstant().atZone(zone).toLocalTime();
        LocalTime shiftStart = shiftStartTime.toInstant().atZone(zone).toLocalTime();
        // Giả sử shiftStart là 08:00, allowedLateTime = 10 (10 phút cho phép)
        LocalTime allowedLateTime = shiftStart.plusMinutes(allowedLateTimePeriodFirst.longValue());

        if (checkinTime.isAfter(shiftStart)) { // Đi làm muộn
            // số phút đi làm muộn
            if (checkinTime.isAfter(allowedLateTime))
                staffWorkSchedule.setLateArrivalCount(1);

            staffWorkSchedule.setLateArrivalMinutes(this.calculateLateMinutes(checkinTime, shiftStart));
            // số phút đi làm sớm
            staffWorkSchedule.setEarlyArrivalMinutes(0);
        } else { // Đến sớm
            // số phút đi làm muộn
            staffWorkSchedule.setLateArrivalCount(0);
            staffWorkSchedule.setLateArrivalMinutes(0);
            // số phút đi làm sớm
            staffWorkSchedule.setEarlyArrivalMinutes(this.calculateEarlyMinutes(checkinTime, shiftStart));
        }
    }

    private void handleScheduleDuringPregnancy(StaffWorkScheduleDto staffWorkSchedule) {
        if (staffWorkSchedule == null
                || staffWorkSchedule.getDuringPregnancy() == null
                || !staffWorkSchedule.getDuringPregnancy())
            return;

        Integer allowMins = HrConstants.EARLY_LATE_MINS_FOR_PREGNANT_STAFF;
        if (allowMins == null || allowMins <= 0) {
            return;
        }

        // Trừ vào số phút đi muộn trước
        Integer lateArrivalMinutes = staffWorkSchedule.getLateArrivalMinutes() != null ? staffWorkSchedule.getLateArrivalMinutes() : 0;
        if (lateArrivalMinutes > 0) {
            int deducted = Math.min(lateArrivalMinutes, allowMins);
            lateArrivalMinutes -= deducted;
            allowMins -= deducted;
        }
        staffWorkSchedule.setLateArrivalMinutes(Math.max(lateArrivalMinutes, 0));

        // Nếu vẫn còn allowMins thì trừ vào số phút về sớm
        if (allowMins > 0) {
            Integer earlyExitMinutes = staffWorkSchedule.getEarlyExitMinutes() != null ? staffWorkSchedule.getEarlyExitMinutes() : 0;
            if (earlyExitMinutes > 0) {
                int deducted = Math.min(earlyExitMinutes, allowMins);
                earlyExitMinutes -= deducted;
                allowMins -= deducted;
            }
            staffWorkSchedule.setEarlyExitMinutes(Math.max(earlyExitMinutes, 0));
        }
    }


    // Thống kê các chỉ số liên quan đến CheckOut trong ca làm việc
    private void handleSetCheckoutStatistics(StaffWorkScheduleDto staffWorkSchedule, Date shiftWorkEndTime,
                                             Date lastCheckout) {
        if (lastCheckout == null || shiftWorkEndTime == null
                // Nhân viên không cần tính đi muộn về sớm
                || (staffWorkSchedule.getStaff().getSkipLateEarlyCount() != null
                && staffWorkSchedule.getStaff().getSkipLateEarlyCount().equals(true))
        ) {
            staffWorkSchedule.setEarlyExitCount(0);
            staffWorkSchedule.setEarlyExitMinutes(0);
            staffWorkSchedule.setLateExitMinutes(0);

            return;
        }

        ZoneId zone = ZoneId.systemDefault();
        LocalTime checkoutTime = lastCheckout.toInstant().atZone(zone).toLocalTime();
        LocalTime shiftEnd = shiftWorkEndTime.toInstant().atZone(zone).toLocalTime();

        if (checkoutTime.isBefore(shiftEnd)) { // Về sớm
            staffWorkSchedule.setEarlyExitCount(1);
            staffWorkSchedule.setEarlyExitMinutes(this.calculateEarlyMinutes(checkoutTime, shiftEnd));
            staffWorkSchedule.setLateExitMinutes(0);
        } else if (checkoutTime.isAfter(shiftEnd)) { // Về muộn
            staffWorkSchedule.setEarlyExitCount(0);
            staffWorkSchedule.setEarlyExitMinutes(0);
            staffWorkSchedule.setLateExitMinutes(this.calculateLateMinutes(checkoutTime, shiftEnd));
        } else { // Đúng giờ
            staffWorkSchedule.setEarlyExitCount(0);
            staffWorkSchedule.setEarlyExitMinutes(0);
            staffWorkSchedule.setLateExitMinutes(0);
        }
    }

    // Hàm tính số phút đi muộn
    private int calculateLateMinutes(LocalTime checkinTime, LocalTime allowedLateTime) {
        if (checkinTime != null && checkinTime.isAfter(allowedLateTime)) {
            return (int) Duration.between(allowedLateTime, checkinTime).toMinutes();
        }
        return 0;
    }

    // Hàm tính số phút về sớm
    private int calculateEarlyMinutes(LocalTime checkoutTime, LocalTime allowedEarlyTime) {
        if (checkoutTime != null && checkoutTime.isBefore(allowedEarlyTime)) {
            return (int) Duration.between(checkoutTime, allowedEarlyTime).toMinutes();
        }
        return 0;
    }

    public Date getLastCheckout(List<TimeSheetDetailDto> timeSheetDetails) {
        if (timeSheetDetails == null || timeSheetDetails.isEmpty()) {
            return null;
        }

        return timeSheetDetails.stream().filter(detail -> detail.getEndTime() != null)
                .map(TimeSheetDetailDto::getEndTime).max(Date::compareTo).orElse(null);
    }

    public Date getFirstCheckin(List<TimeSheetDetailDto> timeSheetDetails) {
        if (timeSheetDetails == null || timeSheetDetails.isEmpty()) {
            return null;
        }

        return timeSheetDetails.stream().filter(detail -> detail.getStartTime() != null)
                .map(TimeSheetDetailDto::getStartTime).min(Date::compareTo).orElse(null);
    }

    public Date getShiftWorkStartTime(ShiftWorkDto shiftWork) {
        if (shiftWork == null || shiftWork.getTimePeriods() == null || shiftWork.getTimePeriods().isEmpty()) {
            return null;
        }

        return shiftWork.getTimePeriods().stream().filter(p -> p.getStartTime() != null)
                .map(ShiftWorkTimePeriodDto::getStartTime).min(Date::compareTo).orElse(null);
    }

    public Double getAllowedLateMinutes(ShiftWorkDto shiftWork) {
        if (shiftWork == null || shiftWork.getTimePeriods() == null || shiftWork.getTimePeriods().isEmpty()) {
            return 0D;
        }

        return shiftWork.getTimePeriods().stream().sorted(Comparator.comparing(ShiftWorkTimePeriodDto::getStartTime)) // đảm
                // bảo
                // đúng
                // thứ
                // tự
                .map(ShiftWorkTimePeriodDto::getAllowedLateMinutes).filter(Objects::nonNull).findFirst().orElse(0D);
    }

    public Date getShiftWorkEndTime(ShiftWorkDto shiftWork) {
        if (shiftWork == null || shiftWork.getTimePeriods() == null || shiftWork.getTimePeriods().isEmpty()) {
            return null;
        }

        return shiftWork.getTimePeriods().stream().filter(p -> p.getEndTime() != null)
                .map(ShiftWorkTimePeriodDto::getEndTime).max(Date::compareTo).orElse(null);
    }

    private double getTotalMinWorkTimeHours(ShiftWorkDto shiftWork, List<ShiftWorkTimePeriodDto> leavePeriods) {
        if (shiftWork == null || shiftWork.getTimePeriods() == null || shiftWork.getTimePeriods().isEmpty())
            return 0D;

        if (leavePeriods == null) {
            leavePeriods = new ArrayList<>();
        }

        double totalMinWorkTimeHour = 0D;

        for (ShiftWorkTimePeriodDto period : shiftWork.getTimePeriods()) {
            if (isContainsPeriod(leavePeriods, period)) continue;

            // Cộng dồn tổng thời gian làm việc tối thiểu
            if (period.getMinWorkTimeHour() == null)
                period.setMinWorkTimeHour(0D);

            totalMinWorkTimeHour += period.getMinWorkTimeHour();
        }

        return totalMinWorkTimeHour;
    }


    @Override
    public List<UUID> reStatisticSchedulesInRangeTime(SearchStaffWorkScheduleDto searchDto) {
        // id của các schedule đã được thống kê lại
        List<UUID> response = new ArrayList<>();

        if (searchDto == null || searchDto.getFromDate() == null || searchDto.getToDate() == null) {
            return null;
        }

        // id của các nhân viên cần được tính lại phiếu lương
        Set<UUID> needRecalculatePayslipsStaffIds = new HashSet<>();

        // Thống kê lại các kết quả làm việc
        int pageIndex = 1;
        boolean hasNextPage = true;
        long startTime = System.nanoTime();

        while (hasNextPage) {
            searchDto.setPageIndex(pageIndex);
            searchDto.setPageSize(33);

            Page<StaffWorkScheduleDto> schedulesPage = staffWorkScheduleService.searchByPage(searchDto);
            if (schedulesPage == null || schedulesPage.isEmpty()) {
                break;
            }

            for (StaffWorkScheduleDto scheduleDto : schedulesPage.getContent()) {
                StaffWorkScheduleDto reStatisticSchedule = this.calculateStaffWorkTimeAndSave(scheduleDto.getId());

                if (reStatisticSchedule != null) {
                    response.add(reStatisticSchedule.getId());

                    if (reStatisticSchedule.getStaff() != null && reStatisticSchedule.getStaff().getId() != null) {
                        needRecalculatePayslipsStaffIds.add(reStatisticSchedule.getStaff().getId());
                    }
                }

            }

            entityManager.flush();
            entityManager.clear();

            hasNextPage = schedulesPage.hasNext(); // Kiểm tra xem còn trang tiếp theo không
            pageIndex++; // Tăng pageIndex để lấy trang tiếp theo
        }

        List<SalaryPeriodDto> overlapPeriods = salaryPeriodService.findSalaryPeriodsInRangeTime(searchDto.getFromDate(), searchDto.getToDate());
        // Lấy các kỳ lương cần được tính toán lại
        for (SalaryPeriodDto period : overlapPeriods) {
            // Các nhân viên cần tính lại phiếu lương
            for (UUID staffId : needRecalculatePayslipsStaffIds) {
                // Tính lại phiếu lương
                salaryResultStaffItemService.updateTimekeepingDataForPayslips(staffId, period.getFromDate());
            }
        }

        long endTime = System.nanoTime();
        long elapsedTimeMs = (endTime - startTime) / 1_000_000;
        logger.info("Thống kê lại các ca làm việc - Xử lý mất {} ms ", elapsedTimeMs);

        return response;
    }


    @Override
    public StaffWorkScheduleDto mapOrphanedTimesheetDetailInDayToSchedule(UUID scheduleId) {
        if (scheduleId == null) return null;

        StaffWorkSchedule staffWorkSchedule = staffWorkScheduleRepository.findById(scheduleId).orElse(null);
        if (staffWorkSchedule == null || staffWorkSchedule.getWorkingDate() == null || staffWorkSchedule.getStaff() == null) {
            return null;
        }

        List<TimeSheetDetail> orphanedDetails = timeSheetDetailRepository.findByOrphanScheduleStaffIdAndStartTimeDate(staffWorkSchedule.getStaff().getId(), staffWorkSchedule.getWorkingDate());
        if (orphanedDetails == null || orphanedDetails.isEmpty()) {
            return new StaffWorkScheduleDto(staffWorkSchedule);
        }

        for (TimeSheetDetail detail : orphanedDetails) {
            detail.setStaffWorkSchedule(staffWorkSchedule);
        }

        if (staffWorkSchedule.getTimesheetDetails() == null) {
            staffWorkSchedule.setTimesheetDetails(new HashSet<>());
        }

        staffWorkSchedule.getTimesheetDetails().addAll(orphanedDetails);

        staffWorkSchedule = staffWorkScheduleRepository.saveAndFlush(staffWorkSchedule);

        StaffWorkScheduleDto response = new StaffWorkScheduleDto(staffWorkSchedule);

        entityManager.flush();
        entityManager.clear();

        return response;
    }
}
