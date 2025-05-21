package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.ShiftWork;
import com.globits.hr.domain.ShiftWorkTimePeriod;
import com.globits.hr.domain.StaffWorkSchedule;
import com.globits.hr.dto.RelaxPeriod;
import com.globits.hr.dto.StaffWorkScheduleDto;
import com.globits.hr.dto.function.Interval;
import com.globits.hr.repository.*;
import com.globits.hr.service.CalculateStaffWorkTimeService;
import com.globits.hr.service.StaffHierarchyService;
import com.globits.hr.service.StaffService;
import com.globits.hr.service.UserExtService;
import com.globits.salary.service.SalaryResultStaffItemService;
import com.globits.timesheet.domain.AbsenceRequest;
import com.globits.timesheet.domain.LeaveRequest;
import com.globits.timesheet.domain.LeaveType;
import com.globits.timesheet.domain.TimeSheetDetail;
import com.globits.timesheet.repository.LeaveRequestRepository;
import com.globits.timesheet.repository.LeaveTypeRepository;
import com.globits.timesheet.repository.OvertimeRequestRepository;
import com.globits.timesheet.repository.TimeSheetDetailRepository;
import com.globits.timesheet.service.LeaveTypeService;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CalculateStaffWorkTimeServiceImpl extends GenericServiceImpl<StaffWorkSchedule, UUID>
        implements CalculateStaffWorkTimeService {
    @Autowired
    private ShiftWorkRepository shiftWorkRepository;

    @Autowired
    private StaffHierarchyService staffHierarchyService;

    @Autowired
    private StaffWorkScheduleRepository staffWorkScheduleRepository;

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


    @Override
    public StaffWorkScheduleDto calculateStaffWorkTime(UUID staffWorkScheduleId) {
        if (staffWorkScheduleId == null)
            return null;

        StaffWorkSchedule staffWorkSchedule = staffWorkScheduleRepository.findById(staffWorkScheduleId).orElse(null);

        if (staffWorkSchedule == null) {
            return null;
        }

        // Chấm công vào ra 1 lần
        if ((staffWorkSchedule.getAllowOneEntryOnly() != null && staffWorkSchedule.getAllowOneEntryOnly())) {
            staffWorkSchedule = this.calculateStaffWorkTimeWithOnlyOneEntry(staffWorkScheduleId);
        }
        // Chấm công vào ra nhiều lần
        else {
//            staffWorkSchedule = this.calculateStaffWorkTimeWithMultipleEntries(staffWorkScheduleId);
            staffWorkSchedule = this.calculateStaffWorkTimeWithMultipleEntriesV2(staffWorkScheduleId);
        }

        StaffWorkScheduleDto response = new StaffWorkScheduleDto(staffWorkSchedule);

        entityManager.flush();
        entityManager.clear();

        return response;
    }


    // Tính số giờ công quy đổi đối với trường hợp chấm công vào ra nhiều lần
    private double calculateConvertedWorkingHoursForMultipleEntry(StaffWorkSchedule staffWorkSchedule) {
        if (staffWorkSchedule == null || staffWorkSchedule.getShiftWork() == null) {
            return 0.0;
        }

        ShiftWork shiftWork = staffWorkSchedule.getShiftWork();
        List<TimeSheetDetail> timeSheetDetails = new ArrayList<>(staffWorkSchedule.getTimesheetDetails());

        if (timeSheetDetails == null || timeSheetDetails.isEmpty()) {
            return 0.0;
        }

        double totalConvertedHours = shiftWork.getConvertedWorkingHours() != null ? shiftWork.getConvertedWorkingHours() : 0.0;

        // Sắp xếp các lần chấm công theo thời gian bắt đầu
        timeSheetDetails.sort(new Comparator<TimeSheetDetail>() {
            @Override
            public int compare(TimeSheetDetail o1, TimeSheetDetail o2) {
                if (o1.getStartTime() == null) return 1;
                if (o2.getStartTime() == null) return -1;
                return o1.getStartTime().compareTo(o2.getStartTime());
            }
        });

        List<ShiftWorkTimePeriod> periods = new ArrayList<>(shiftWork.getTimePeriods());
        periods.sort(new Comparator<ShiftWorkTimePeriod>() {
            @Override
            public int compare(ShiftWorkTimePeriod o1, ShiftWorkTimePeriod o2) {
                return o1.getStartTime().compareTo(o2.getStartTime());
            }
        });

        double totalLateMinutes = 0;
        double totalEarlyLeaveMinutes = 0;
        double totalBreakMinutes = 0;

        for (ShiftWorkTimePeriod period : periods) {
            Date periodStart = period.getStartTime();
            Date periodEnd = period.getEndTime();

            // Lọc các lần chấm công có mặt trong giai đoạn ca làm việc
            List<TimeSheetDetail> inPeriodDetails = new ArrayList<>();
            for (TimeSheetDetail detail : timeSheetDetails) {
                Date checkIn = detail.getStartTime();
                Date checkOut = detail.getEndTime();
                if (checkIn == null || checkOut == null) {
                    continue;
                }
                if (!(checkOut.before(periodStart) || checkIn.after(periodEnd))) {
                    inPeriodDetails.add(detail);
                }
            }

            if (!inPeriodDetails.isEmpty()) {
                // Tính thời gian đi muộn
                Date firstCheckIn = inPeriodDetails.get(0).getStartTime();
                if (firstCheckIn.after(periodStart)) {
                    long lateMillis = firstCheckIn.getTime() - periodStart.getTime();
                    totalLateMinutes += lateMillis / (60 * 1000);
                }

                // Tính thời gian về sớm
                Date lastCheckOut = inPeriodDetails.get(inPeriodDetails.size() - 1).getEndTime();
                if (lastCheckOut.before(periodEnd)) {
                    long earlyMillis = periodEnd.getTime() - lastCheckOut.getTime();
                    totalEarlyLeaveMinutes += earlyMillis / (60 * 1000);
                }

                // Tính thời gian nghỉ giữa ca
                for (int i = 0; i < inPeriodDetails.size() - 1; i++) {
                    Date prevEnd = inPeriodDetails.get(i).getEndTime();
                    Date nextStart = inPeriodDetails.get(i + 1).getStartTime();
                    if (nextStart.after(prevEnd)) {
                        long breakMillis = nextStart.getTime() - prevEnd.getTime();
                        totalBreakMinutes += breakMillis / (60 * 1000);
                    }
                }
            } else {
                // Không có chấm công trong giai đoạn này => xem như nghỉ toàn bộ
                long fullPeriodMillis = periodEnd.getTime() - periodStart.getTime();
                totalLateMinutes += fullPeriodMillis / (60 * 1000);
            }
        }

        double totalDeductedMinutes = totalLateMinutes + totalEarlyLeaveMinutes + totalBreakMinutes;
        double totalDeductedHours = totalDeductedMinutes / 60.0;

        return Math.max(0.0, totalConvertedHours - totalDeductedHours);
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

    // Thống kê các chỉ số liên quan đến Checkin trong ca làm việc
    private void handleSetCheckInStatistics(StaffWorkSchedule staffWorkSchedule, Date shiftStartTime,
                                            Date firstCheckin, Double allowedLateTimePeriodFirst) {

        if (firstCheckin == null || shiftStartTime == null) {
            // số phút đi làm muộn
            staffWorkSchedule.setLateArrivalCount(0);
            staffWorkSchedule.setLateArrivalMinutes(0);
            // số phút đi làm sớm
            staffWorkSchedule.setEarlyArrivalMinutes(0);
            return;
        } else {
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
            } else if (checkinTime.isBefore(shiftStart)) { // Đến sớm
                // số phút đi làm muộn
                staffWorkSchedule.setLateArrivalCount(0);
                staffWorkSchedule.setLateArrivalMinutes(0);
                // số phút đi làm sớm
                staffWorkSchedule.setEarlyArrivalMinutes(this.calculateEarlyMinutes(checkinTime, shiftStart));
            }
        }
    }

    // Thống kê các chỉ số liên quan đến CheckOut trong ca làm việc
    private void handleSetCheckoutStatistics(StaffWorkSchedule staffWorkSchedule, Date shiftWorkEndTime,
                                             Date lastCheckout) {
        if (lastCheckout == null || shiftWorkEndTime == null) {
            staffWorkSchedule.setEarlyExitCount(0);
            staffWorkSchedule.setEarlyExitMinutes(0);
            staffWorkSchedule.setLateExitMinutes(0);
            return;
        } else {
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
    }


    public Date getLastCheckout(List<TimeSheetDetail> timeSheetDetails) {
        if (timeSheetDetails == null || timeSheetDetails.isEmpty()) {
            return null;
        }

        return timeSheetDetails.stream()
                .filter(detail -> detail.getEndTime() != null)
                .map(TimeSheetDetail::getEndTime)
                .max(Date::compareTo)
                .orElse(null);
    }

    public LocalDateTime getLastCheckoutFromIntervals(List<Interval> intervals) {
        if (intervals == null || intervals.isEmpty()) {
            return null;
        }

        return intervals.stream()
                .filter(i -> i != null && i.getEnd() != null)
                .map(Interval::getEnd)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }


    public Double getAllowedLateMinutes(ShiftWork shiftWork) {
        if (shiftWork == null || shiftWork.getTimePeriods() == null || shiftWork.getTimePeriods().isEmpty()) {
            return 0D;
        }

        return shiftWork.getTimePeriods().stream()
                .sorted(Comparator.comparing(ShiftWorkTimePeriod::getStartTime)) // đảm bảo đúng thứ tự
                .map(ShiftWorkTimePeriod::getAllowedLateMinutes)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(0D);
    }

    public LocalDateTime getFirstStartTimeFromIntervals(List<Interval> intervals) {
        if (intervals == null || intervals.isEmpty()) {
            return null;
        }

        return intervals.stream()
                .map(Interval::getStart)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    public Date getFirstCheckin(List<TimeSheetDetail> timeSheetDetails) {
        if (timeSheetDetails == null || timeSheetDetails.isEmpty()) {
            return null;
        }

        return timeSheetDetails.stream()
                .filter(detail -> detail.getStartTime() != null)
                .map(TimeSheetDetail::getStartTime)
                .min(Date::compareTo)
                .orElse(null);
    }


    public Date getShiftWorkStartTime(ShiftWork shiftWork) {
        if (shiftWork == null || shiftWork.getTimePeriods() == null || shiftWork.getTimePeriods().isEmpty()) {
            return null;
        }

        return shiftWork.getTimePeriods().stream()
                .filter(p -> p.getStartTime() != null)
                .map(ShiftWorkTimePeriod::getStartTime)
                .min(Date::compareTo)
                .orElse(null);
    }


    public Date getShiftWorkEndTime(ShiftWork shiftWork) {
        if (shiftWork == null || shiftWork.getTimePeriods() == null || shiftWork.getTimePeriods().isEmpty()) {
            return null;
        }

        return shiftWork.getTimePeriods().stream()
                .filter(p -> p.getEndTime() != null)
                .map(ShiftWorkTimePeriod::getEndTime)
                .max(Date::compareTo)
                .orElse(null);
    }


    @Override
    public double calculatePaidLeaveHours(List<LeaveRequest> leaveRequests, ShiftWork shiftWork, Date workingDate) {
        if (leaveRequests == null || leaveRequests.isEmpty() || shiftWork == null || workingDate == null) {
            return 0D;
        }

        double paidLeaveHours = 0D;

        LocalDate targetDate = workingDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        for (LeaveRequest request : leaveRequests) {
            if (request.getFromDate() == null || request.getToDate() == null || request.getLeaveType() == null) {
                continue;
            }

            if (!Boolean.TRUE.equals(request.getLeaveType().getIsPaid())) {
                continue; // Chỉ tính loại nghỉ có tính công
            }

            LocalDateTime leaveStart = request.getFromDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime leaveEnd = request.getToDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            // Cắt leaveInterval để chỉ lấy phần trong ngày làm việc
            LocalDateTime dayStart = targetDate.atStartOfDay();
            LocalDateTime dayEnd = targetDate.atTime(LocalTime.MAX); // 23:59:59.999999999

            Interval leaveInterval = new Interval(leaveStart, leaveEnd).getIntersection(new Interval(dayStart, dayEnd));
            if (leaveInterval == null) continue;

            for (ShiftWorkTimePeriod period : shiftWork.getTimePeriods()) {
                if (period.getStartTime() == null || period.getEndTime() == null) continue;

                LocalDateTime periodStart = LocalDateTime.of(targetDate, toLocalTime(period.getStartTime()));
                LocalDateTime periodEnd = LocalDateTime.of(targetDate, toLocalTime(period.getEndTime()));

                Interval shiftInterval = new Interval(periodStart, periodEnd);
                Interval intersection = leaveInterval.getIntersection(shiftInterval);

                if (intersection != null) {
                    paidLeaveHours += intersection.getDurationInMinutes() / 60.0;
                }
            }
        }

        return paidLeaveHours;
    }


    /*
     * Xử lý trường hợp chấm công chỉ 1 lần vào ra trong ca làm việc
     */
    @Override
    public StaffWorkSchedule calculateStaffWorkTimeWithOnlyOneEntry(UUID staffWorkScheduleId) {
        if (staffWorkScheduleId == null)
            return null;

        StaffWorkSchedule staffWorkSchedule = staffWorkScheduleRepository.findById(staffWorkScheduleId).orElse(null);

        if (staffWorkSchedule == null || staffWorkSchedule.getShiftWork() == null || staffWorkSchedule.getStaff() == null || staffWorkSchedule.getWorkingDate() == null)
            return null;

        ShiftWork shiftWork = staffWorkSchedule.getShiftWork();
        if (shiftWork.getTotalHours() == null) {
            shiftWork.setTotalHours(0D);
        }

        List<LeaveRequest> availableRequests = leaveRequestRepository.findByStaffIdWorkingDateAndApprovalStatus(
                staffWorkSchedule.getStaff().getId(),
                staffWorkSchedule.getWorkingDate(),
                HrConstants.AbsenceRequestApprovalStatus.APPROVED.getValue());

        if (availableRequests == null) {
            availableRequests = new ArrayList<>();
        }

        // Số giờ làm việc dự kiến
        Double estimatedWorkingHours = 0D;
        if (shiftWork != null) {
            estimatedWorkingHours = this.getEstimatedWorkingHours(staffWorkSchedule, availableRequests);
        }
        staffWorkSchedule.setEstimatedWorkingHours(estimatedWorkingHours);

        // Các lần chấm công thực tế
        List<TimeSheetDetail> timeSheetDetails = timeSheetDetailRepository
                .getByStaffWorkSchedule(staffWorkSchedule.getId());

        if (timeSheetDetails == null) timeSheetDetails = new ArrayList<>();

        // Các khoảng thời gian làm việc trong ca
        List<Interval> validWorkIntervals = this.getValidWorkIntervals(timeSheetDetails, shiftWork, availableRequests, staffWorkSchedule.getWorkingDate());

        // Thống kê các chỉ số liên quan đến đi muộn/sớm/số lần
        if (!timeSheetDetails.isEmpty()) {
            Date firstCheckIn = toDate(this.getFirstStartTimeFromIntervals(validWorkIntervals));
            Date shiftWorkStartTime = this.getShiftWorkStartTime(shiftWork);
            Double allowedLateMinutes = this.getAllowedLateMinutes(shiftWork);

            // Số lần đi sớm
            // Số phút đi sớm
            // Số phút đi muộn
            this.handleSetCheckInStatistics(staffWorkSchedule, shiftWorkStartTime, firstCheckIn, allowedLateMinutes);
        }

        // Thống kê các chỉ số liên quan đến về muộn/sớm/số lần
        if (!timeSheetDetails.isEmpty()) {
            Date lastCheckout = toDate(this.getLastCheckoutFromIntervals(validWorkIntervals));
            Date shiftWorkEndTime = this.getShiftWorkEndTime(shiftWork);

            // Số lần về sớm
            // Số phút về sớm
            // Số phút về muộn
            this.handleSetCheckoutStatistics(staffWorkSchedule, shiftWorkEndTime, lastCheckout);
        }


        // Số giờ thực tế nhân viên đã làm việc của ca này
        Double totalHours = calculateTotalWorkHoursByWorkIntervals(validWorkIntervals, shiftWork);
        staffWorkSchedule.setTotalHours(totalHours);

        // Số giờ nghỉ được tính công
        Double paidLeaveHours = this.calculatePaidLeaveHours(availableRequests, shiftWork, staffWorkSchedule.getWorkingDate());
        staffWorkSchedule.setPaidLeaveHours(paidLeaveHours);

        // Số giờ nghỉ không được tính công
        Double unpaidLeaveHours = shiftWork.getTotalHours() - totalHours - paidLeaveHours;
        if (unpaidLeaveHours < 0) unpaidLeaveHours = 0D;
        staffWorkSchedule.setUnpaidLeaveHours(unpaidLeaveHours);

        // Số giờ công quy đổi
        Double convertedWorkingHours = calculateConvertedWorkingHoursForOneEntry(staffWorkSchedule);
        Double reduceLeaveHours = shiftWork.getTotalHours() - staffWorkSchedule.getEstimatedWorkingHours();
        convertedWorkingHours -= reduceLeaveHours;
        if (convertedWorkingHours < 0) convertedWorkingHours = 0D;
        staffWorkSchedule.setConvertedWorkingHours(convertedWorkingHours);

        // Tỷ lệ công được tính trong ca được phân
        // TH: Tính ngày công dựa trên tỉ lệ ngày công được định nghĩa trong từng giai đoạn làm việc
        Double totalPaidWork = calculateStaffWorkRatioByWorkIntervals(validWorkIntervals, shiftWork);
        // TH: Tính ngày công dựa trên tỉ lệ số giờ đã làm việc
//        Double totalPaidWork = calculateWorkRatioForOneEntryTimekeepingV2(totalHours, shiftWork);
        staffWorkSchedule.setTotalPaidWork(totalPaidWork);

        // Tỷ lệ công nghỉ phép được hưởng lương trong ca làm việc
        Double paidLeaveWorkRatio = calculatePaidLeaveWorkRatio(staffWorkSchedule);
        staffWorkSchedule.setPaidLeaveWorkRatio(paidLeaveWorkRatio);

        // Tỷ lệ công nghỉ phép KHÔNG được hưởng lương trong ca làm việc
        Double unpaidLeaveWorkRatio = calculateUnPaidLeaveWorkRatio(staffWorkSchedule);
        staffWorkSchedule.setUnpaidLeaveWorkRatio(unpaidLeaveWorkRatio);

        // Thời gian làm việc để tính đủ công của cả giai đoạn
        Double totalMinWorkTimeHour = this.getTotalMinWorkTimeHours(shiftWork);

        if (totalHours >= totalMinWorkTimeHour) {
            staffWorkSchedule.setWorkingStatus(HrConstants.StaffWorkScheduleWorkingStatus.FULL_ATTENDANCE.getValue());
        } else if (totalHours > 0) {
            staffWorkSchedule
                    .setWorkingStatus(HrConstants.StaffWorkScheduleWorkingStatus.PARTIAL_ATTENDANCE.getValue());
        } else {
            staffWorkSchedule.setWorkingStatus(HrConstants.StaffWorkScheduleWorkingStatus.NOT_ATTENDANCE.getValue());
        }

        staffWorkSchedule = staffWorkScheduleRepository.saveAndFlush(staffWorkSchedule);

        return staffWorkSchedule;
    }

    // Tính tỷ lệ ngày công KHÔNG được hưởng lương trong ca làm việc
    private double calculateUnPaidLeaveWorkRatio(StaffWorkSchedule staffWorkSchedule) {
        if (staffWorkSchedule == null) {
            return 0.0;
        }

        ShiftWork shiftWork = staffWorkSchedule.getShiftWork();

        // Tỷ lệ công được tính của ca làm việc
        double totalWorkRatio = this.getTotalWorkRatio(shiftWork);

        // Tỷ lệ ngày công nghỉ KHÔNG hưởng lương được tính
        double unpaidLeaveWorkRatio = totalWorkRatio - staffWorkSchedule.getTotalPaidWork() - staffWorkSchedule.getPaidLeaveWorkRatio();

        if (unpaidLeaveWorkRatio <= 0) unpaidLeaveWorkRatio = 0D;

        return unpaidLeaveWorkRatio;
    }

    // Tính tỷ lệ ngày công được hưởng lương trong ca làm việc
    private double calculatePaidLeaveWorkRatio(StaffWorkSchedule staffWorkSchedule) {
        if (staffWorkSchedule == null) {
            return 0.0;
        }

        ShiftWork shiftWork = staffWorkSchedule.getShiftWork();

        // Tỷ lệ công được tính của ca làm việc
        double totalWorkRatio = this.getTotalWorkRatio(shiftWork);
        // Số giờ làm việc của ca làm việc
        double totalShiftWorkHours = shiftWork.getTotalHours();
        if (totalShiftWorkHours <= 0) return 0D;


        // Tỷ lệ công làm việc đã được tính
        double staffWorkRatio = staffWorkSchedule.getTotalPaidWork();
        if (staffWorkRatio <= 0) staffWorkRatio = 0D;

        // Số giờ nghỉ được tính lương
        double paidLeaveHours = staffWorkSchedule.getPaidLeaveHours();
        if (paidLeaveHours < 0) paidLeaveHours = 0D;


        // Tỷ lệ nghỉ phép được tính
        double paidLeaveWorkRatio = paidLeaveHours / totalShiftWorkHours * totalWorkRatio;

        if (paidLeaveWorkRatio <= 0) paidLeaveWorkRatio = 0D;

        // Nếu tỷ lệ ngày công nghỉ được trả lương
        // cộng với
        // công được tính lương mà lớn hơn tổng tỷ lệ công được hưởng lương của ca làm việc, chỉ lấy tối đa = tổng công của ca làm việc - công đã làm
        if (totalWorkRatio < paidLeaveWorkRatio + staffWorkRatio) {
            paidLeaveWorkRatio = totalWorkRatio - staffWorkRatio;
        }

        return paidLeaveWorkRatio;
    }


    public double calculateTotalWorkHoursByWorkIntervals(List<Interval> validWorkIntervals, ShiftWork shiftWork) {
        if (validWorkIntervals == null || shiftWork == null || shiftWork.getTimePeriods() == null) {
            return 0.0;
        }

        double totalHours = 0.0;

        for (ShiftWorkTimePeriod period : shiftWork.getTimePeriods()) {
            if (period.getStartTime() == null || period.getEndTime() == null) continue;

            LocalDateTime periodStart = LocalDateTime.of(0, 1, 1,
                    period.getStartTime().getHours(), period.getStartTime().getMinutes());
            LocalDateTime periodEnd = LocalDateTime.of(0, 1, 1,
                    period.getEndTime().getHours(), period.getEndTime().getMinutes());

            Interval periodInterval = new Interval(periodStart, periodEnd);

            for (Interval workInterval : validWorkIntervals) {
                // Chỉ lấy giờ và phút, bỏ qua ngày
                LocalDateTime workStart = LocalDateTime.of(0, 1, 1,
                        workInterval.getStart().getHour(), workInterval.getStart().getMinute());
                LocalDateTime workEnd = LocalDateTime.of(0, 1, 1,
                        workInterval.getEnd().getHour(), workInterval.getEnd().getMinute());

                Interval shiftedWork = new Interval(workStart, workEnd);
                Interval overlap = shiftedWork.getIntersection(periodInterval);

                if (overlap != null) {
                    totalHours += overlap.getDurationInMinutes() / 60.0;
                }
            }
        }

        return totalHours;
    }

    // TH: Tỷ lệ ngày công được tính nếu vượt qua số giờ làm việc tối thiểu của giai đoạn làm việc trong ca
    public double calculateStaffWorkRatioByWorkIntervals(List<Interval> validWorkIntervals, ShiftWork shiftWork) {
        if (validWorkIntervals == null || shiftWork == null || shiftWork.getTimePeriods() == null) {
            return 0.0;
        }

        double totalWorkRatio = 0.0;

        for (ShiftWorkTimePeriod period : shiftWork.getTimePeriods()) {
            if (period.getStartTime() == null || period.getEndTime() == null) continue;

            LocalDateTime periodStart = LocalDateTime.of(0, 1, 1,
                    period.getStartTime().getHours(), period.getStartTime().getMinutes());
            LocalDateTime periodEnd = LocalDateTime.of(0, 1, 1,
                    period.getEndTime().getHours(), period.getEndTime().getMinutes());

            Interval periodInterval = new Interval(periodStart, periodEnd);
            double workedHoursInPeriod = 0.0;

            for (Interval workInterval : validWorkIntervals) {
                LocalDateTime workStart = LocalDateTime.of(0, 1, 1,
                        workInterval.getStart().getHour(), workInterval.getStart().getMinute());
                LocalDateTime workEnd = LocalDateTime.of(0, 1, 1,
                        workInterval.getEnd().getHour(), workInterval.getEnd().getMinute());

                Interval shiftedWork = new Interval(workStart, workEnd);
                Interval overlap = shiftedWork.getIntersection(periodInterval);

                if (overlap != null) {
                    workedHoursInPeriod += overlap.getDurationInMinutes() / 60.0;
                }
            }

            if (workedHoursInPeriod >= Optional.ofNullable(period.getMinWorkTimeHour()).orElse(0.0)) {
                totalWorkRatio += Optional.ofNullable(period.getWorkRatio()).orElse(0.0);
            }
        }

        return totalWorkRatio;
    }

    // TH: Tỷ lệ ngày công được tính được trên số giờ đã làm việc trong ca. VD: Ca kéo dài 8 tiếng, chỉ làm việc 5 tiếng => Tỷ lệ ngày công = 0.5
    public double calculateWorkRatioForOneEntryTimekeepingV2(Double totalWorkHours, ShiftWork shiftWork) {
        if (totalWorkHours == null || shiftWork == null || shiftWork.getTimePeriods() == null) {
            return 0.0;
        }

        double totalWorkRatio = this.getTotalWorkRatio(shiftWork);
        double totalShiftWorkHours = shiftWork.getTotalHours();
        if (totalShiftWorkHours < 0) totalShiftWorkHours = 0D;


        if (totalShiftWorkHours == 0) return 0D;

        double result = totalWorkHours / totalShiftWorkHours * totalWorkRatio;

        if (result >= HrConstants.WORK_ENOUGH_THRESHOLD * totalWorkRatio) return totalWorkRatio;
        if (result >= HrConstants.WORK_ENOUGH_THRESHOLD * totalWorkRatio / 2) return totalWorkRatio / 2;

        return 0D;
    }

    public double calculateConvertedWorkingHoursForOneEntry(StaffWorkSchedule staffWorkSchedule) {
        if (staffWorkSchedule.getTotalHours() == null || staffWorkSchedule.getShiftWork() == null
                || staffWorkSchedule.getShiftWork().getConvertedWorkingHours() == null) {
            return 0D;
        }

        int lateArrivalMinutes = Optional.ofNullable(staffWorkSchedule.getLateArrivalMinutes()).orElse(0);
        int earlyExitMinutes = Optional.ofNullable(staffWorkSchedule.getEarlyExitMinutes()).orElse(0);

        double maximumConversionHours = staffWorkSchedule.getShiftWork().getConvertedWorkingHours();

        maximumConversionHours -= (lateArrivalMinutes + earlyExitMinutes) / 60.0;

//        if (maximumConversionHours >= HrConstants.STANDARD_CONVERTED_WORKING_HOURS * HrConstants.WORK_ENOUGH_THRESHOLD)
//            maximumConversionHours = HrConstants.STANDARD_CONVERTED_WORKING_HOURS;

        if (maximumConversionHours < 0)
            maximumConversionHours = 0;

        return maximumConversionHours;
    }


    private double getTotalWorkRatio(ShiftWork shiftWork) {
        if (shiftWork == null || shiftWork.getTimePeriods() == null || shiftWork.getTimePeriods().isEmpty()) return 0D;

        double totalWorkRatio = 0D;

        for (ShiftWorkTimePeriod period : shiftWork.getTimePeriods()) {
            // Cộng dồn tổng thời gian làm việc tối thiểu
            if (period.getWorkRatio() == null) period.setWorkRatio(0D);

            totalWorkRatio += period.getWorkRatio();
        }

        return totalWorkRatio;
    }

    private double getTotalMinWorkTimeHours(ShiftWork shiftWork) {
        if (shiftWork == null || shiftWork.getTimePeriods() == null || shiftWork.getTimePeriods().isEmpty()) return 0D;

        double totalMinWorkTimeHour = 0D;

        for (ShiftWorkTimePeriod period : shiftWork.getTimePeriods()) {
            // Cộng dồn tổng thời gian làm việc tối thiểu
            if (period.getMinWorkTimeHour() == null) period.setMinWorkTimeHour(0D);

            totalMinWorkTimeHour += period.getMinWorkTimeHour();
        }

        return totalMinWorkTimeHour;
    }

    // Tính thời gian làm việc ước tính của nhân viên sau khi đã loại bỏ các khoảng thời gian xin nghỉ
    @Override
    public double getEstimatedWorkingHours(StaffWorkSchedule entity, List<LeaveRequest> availableRequests) {
        if (entity == null || entity.getStaff() == null || entity.getWorkingDate() == null) return 0D;
        if (entity.getShiftWork() == null || entity.getShiftWork().getTimePeriods() == null) return 0D;


        // ✅ Chuyển từ Set sang List và sắp xếp theo thời gian bắt đầu
        List<ShiftWorkTimePeriod> timePeriods = new ArrayList<>(entity.getShiftWork().getTimePeriods());
        if (timePeriods.isEmpty()) return 0D;
        timePeriods.sort(Comparator.comparing(ShiftWorkTimePeriod::getStartTime));

        LocalDate workingDate = toLocalDate(entity.getWorkingDate());

        // ✅ Tạo các khoảng thời gian làm việc trong ngày
        List<Interval> workingIntervals = new ArrayList<>();
        for (ShiftWorkTimePeriod period : timePeriods) {
            LocalTime start = toLocalTime(period.getStartTime());
            LocalTime end = toLocalTime(period.getEndTime());
            workingIntervals.add(new Interval(LocalDateTime.of(workingDate, start), LocalDateTime.of(workingDate, end)));
        }

        // ✅ Tạo các khoảng thời gian nghỉ giao với ngày làm việc
        LocalDateTime workingStartOfDay = workingDate.atStartOfDay();
        LocalDateTime workingEndOfDay = workingDate.plusDays(1).atStartOfDay();
        List<Interval> leaveIntervals = new ArrayList<>();

        for (LeaveRequest leave : availableRequests) {
            if (leave.getFromDate() == null || leave.getToDate() == null) continue;

            LocalDateTime from = toLocalDateTime(leave.getFromDate());
            LocalDateTime to = toLocalDateTime(leave.getToDate());

            // ✅ Cắt phần nghỉ sao cho chỉ nằm trong ngày làm việc
            LocalDateTime leaveStart = from.isAfter(workingStartOfDay) ? from : workingStartOfDay;
            LocalDateTime leaveEnd = to.isBefore(workingEndOfDay) ? to : workingEndOfDay;

            if (leaveStart.isBefore(leaveEnd)) {
                leaveIntervals.add(new Interval(leaveStart, leaveEnd));
            }
        }

        // ✅ Hợp nhất các khoảng nghỉ chồng chéo
        List<Interval> mergedLeaveIntervals = mergeIntervals(leaveIntervals);

        // ✅ Tính tổng thời gian nghỉ trùng với ca làm việc
        double totalOverlapMinutes = 0;
        for (Interval workInterval : workingIntervals) {
            for (Interval leaveInterval : mergedLeaveIntervals) {
                Interval overlap = workInterval.getIntersection(leaveInterval);
                if (overlap != null) {
                    totalOverlapMinutes += overlap.getDurationInMinutes();
                }
            }
        }

        // ✅ Tổng thời gian làm việc trong ngày
        double totalWorkMinutes = workingIntervals.stream()
                .mapToDouble(Interval::getDurationInMinutes)
                .sum();

        // ✅ Trả về số giờ làm việc ước tính
        double estimatedMinutes = totalWorkMinutes - totalOverlapMinutes;
        return Math.max(estimatedMinutes / 60.0, 0);
    }

    public static List<Interval> mergeIntervals(List<Interval> intervals) {
        if (intervals.isEmpty()) return List.of();

        // Sắp xếp theo thời gian bắt đầu
        intervals.sort(Comparator.comparing(Interval::getStart));
        List<Interval> merged = new ArrayList<>();
        Interval current = intervals.get(0);

        for (int i = 1; i < intervals.size(); i++) {
            Interval next = intervals.get(i);
            if (!current.getEnd().isBefore(next.getStart())) {
                current = new Interval(current.getStart(), current.getEnd().isAfter(next.getEnd()) ? current.getEnd() : next.getEnd());
            } else {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);
        return merged;
    }


    // Helper convert Date → LocalTime
    private LocalTime toLocalTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    // Helper convert Date → LocalDate
    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // Helper convert Date → LocalDateTime
    private LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public Date toDate(LocalDateTime localDateTime) {
        return localDateTime == null ? null : Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


    // Các khoảng thực tế nhân viên đã làm việc từ dữ liệu chấm công sau khi đã trừ các khoảng xin nghỉ
    @Override
    public List<Interval> getValidWorkIntervals(
            List<TimeSheetDetail> timeSheetDetails,
            ShiftWork shiftWork,
            List<LeaveRequest> approvedLeaveRequests,
            Date workingDate
    ) {
        if (timeSheetDetails == null || shiftWork == null || shiftWork.getTimePeriods() == null || workingDate == null) {
            return Collections.emptyList();
        }

        // B1: Chuyển Date -> LocalDate
        LocalDate workingLocalDate = toLocalDate(workingDate);
        LocalDateTime dayStart = workingLocalDate.atStartOfDay();
        LocalDateTime dayEnd = workingLocalDate.plusDays(1).atStartOfDay();

        // B2: Tạo danh sách các khoảng làm việc trong ca
        List<Interval> shiftWorkIntervals = new ArrayList<>();
        for (ShiftWorkTimePeriod period : shiftWork.getTimePeriods()) {
            LocalTime start = toLocalTime(period.getStartTime());
            LocalTime end = toLocalTime(period.getEndTime());
            if (start != null && end != null) {
                shiftWorkIntervals.add(new Interval(LocalDateTime.of(workingLocalDate, start),
                        LocalDateTime.of(workingLocalDate, end)));
            }
        }

        // B3: Tạo các khoảng nghỉ phép giao với ngày làm việc
        List<Interval> leaveIntervals = new ArrayList<>();
        for (LeaveRequest leave : approvedLeaveRequests) {
            if (leave.getFromDate() == null || leave.getToDate() == null) continue;

            LocalDateTime from = toLocalDateTime(leave.getFromDate());
            LocalDateTime to = toLocalDateTime(leave.getToDate());

            LocalDateTime leaveStart = from.isAfter(dayStart) ? from : dayStart;
            LocalDateTime leaveEnd = to.isBefore(dayEnd) ? to : dayEnd;

            if (leaveStart.isBefore(leaveEnd)) {
                leaveIntervals.add(new Interval(leaveStart, leaveEnd));
            }
        }

        List<Interval> mergedLeaveIntervals = mergeIntervals(leaveIntervals);

        // B4: Lấy các khoảng chấm công hợp lệ sau khi trừ thời gian nghỉ
        List<Interval> validWorkIntervals = new ArrayList<>();

        for (TimeSheetDetail detail : timeSheetDetails) {
            if (detail.getStartTime() == null || detail.getEndTime() == null) continue;

            LocalDateTime checkIn = toLocalDateTime(detail.getStartTime());
            LocalDateTime checkOut = toLocalDateTime(detail.getEndTime());
            if (checkIn.isAfter(checkOut)) continue;

            Interval timeSheetInterval = new Interval(checkIn, checkOut);

            for (Interval shiftInterval : shiftWorkIntervals) {
                Interval overlap = timeSheetInterval.getIntersection(shiftInterval);
                if (overlap != null) {
                    List<Interval> result = new ArrayList<>();
                    result.add(overlap);

                    for (Interval leave : mergedLeaveIntervals) {
                        result = subtractIntervals(result, leave);
                        if (result.isEmpty()) break;
                    }

                    validWorkIntervals.addAll(result);
                }
            }
        }

        return validWorkIntervals;
    }


    private List<Interval> subtractIntervals(List<Interval> sourceIntervals, Interval subtract) {
        List<Interval> result = new ArrayList<>();
        for (Interval source : sourceIntervals) {
            Interval overlap = source.getIntersection(subtract);
            if (overlap == null) {
                result.add(source);
            } else {
                if (source.getStart().isBefore(overlap.getStart())) {
                    result.add(new Interval(source.getStart(), overlap.getStart()));
                }
                if (source.getEnd().isAfter(overlap.getEnd())) {
                    result.add(new Interval(overlap.getEnd(), source.getEnd()));
                }
            }
        }
        return result;
    }


    // Chuyển đổi Date -> LocalTime và loại bỏ giây
    private LocalTime getLocalTimeWithoutSeconds(Date date) {
        if (date == null)
            return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime().withSecond(0).withNano(0);
    }

    private double getMinutesDifference(Date actual, Date reference) {
        // Chuyển Date -> LocalTime (chỉ lấy giờ và phút)
        LocalTime timeActual = actual.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        LocalTime timeRef = reference.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

        // Tính khoảng cách phút giữa 2 mốc thời gian
        return java.time.Duration.between(timeRef, timeActual).toMinutes();
    }

    public double getLateMinutes(Date checkIn, Date shiftStart) {
        double totalLateMinutes = getMinutesDifference(checkIn, shiftStart);
        return totalLateMinutes > 0 ? totalLateMinutes : 0;
    }

    public double getEarlyMinutes(Date checkIn, Date shiftStart) {
        return Math.max(0, -getMinutesDifference(checkIn, shiftStart));
    }

    public double getOvertimeMinutes(Date checkOut, Date shiftEnd) {
        return Math.max(0, getMinutesDifference(checkOut, shiftEnd));
    }

    public double getLeaveEarlyMinutes(Date checkOut, Date shiftEnd) {
        return Math.max(0, -getMinutesDifference(checkOut, shiftEnd));
    }

    public boolean isLateForShiftPeriod(Date checkIn, double allowedLateMinutes, Date shiftStart) {
        return getLateMinutes(checkIn, shiftStart) > allowedLateMinutes;
    }

    public boolean isLeaveEarly(Date checkOut, Date shiftEnd) {
        return getLeaveEarlyMinutes(checkOut, shiftEnd) > 0;
    }

    // Xử lý trường hợp chấm công nhiều lần trong 1 ca làm việc
    @Override
    public StaffWorkSchedule calculateStaffWorkTimeWithMultipleEntriesV2(UUID staffWorkScheduleId) {
        StaffWorkSchedule staffWorkSchedule = staffWorkScheduleRepository.findById(staffWorkScheduleId).orElse(null);
        if (staffWorkSchedule == null || staffWorkSchedule.getShiftWork() == null
                || staffWorkSchedule.getStaff() == null) {
            return null;
        }

        // Ca làm việc
        ShiftWork shiftWork = staffWorkSchedule.getShiftWork();

        // Các yêu cầu nghỉ phép đã được phê duyệt trong ngày làm việc này
        List<LeaveRequest> availableRequests = leaveRequestRepository.findByStaffIdWorkingDateAndApprovalStatus(
                staffWorkSchedule.getStaff().getId(),
                staffWorkSchedule.getWorkingDate(),
                HrConstants.AbsenceRequestApprovalStatus.APPROVED.getValue());

        if (availableRequests == null) {
            availableRequests = new ArrayList<>();
        }

        Integer timekeepingCalculationType = staffWorkSchedule.getTimekeepingCalculationType();
        // Nếu không có loại tính toán, sử dụng FILO làm mặc định
        if (timekeepingCalculationType == null) {
            timekeepingCalculationType = HrConstants.TimekeepingCalculationType.FIRST_IN_LAST_OUT.getValue();
            staffWorkSchedule.setTimekeepingCalculationType(timekeepingCalculationType);
        }

        if (shiftWork == null || shiftWork.getTimePeriods() == null || shiftWork.getTimePeriods().isEmpty()) {
            return null;
        }


        // Số giờ làm việc dự kiến
        Double estimatedWorkingHours = 0D;
        if (shiftWork != null) {
            estimatedWorkingHours = this.getEstimatedWorkingHours(staffWorkSchedule, availableRequests);
        }
        staffWorkSchedule.setEstimatedWorkingHours(estimatedWorkingHours);

        // Các lần chấm công thực tế
        List<TimeSheetDetail> timeSheetDetails = timeSheetDetailRepository
                .getByStaffWorkSchedule(staffWorkSchedule.getId());

        if (timeSheetDetails == null) timeSheetDetails = new ArrayList<>();

        // Các khoảng thời gian làm việc trong ca
        List<Interval> validWorkIntervals = this.getValidWorkIntervals(timeSheetDetails, shiftWork, availableRequests, staffWorkSchedule.getWorkingDate());

        // Thống kê các chỉ số đi muộn về sớm cho trường hợp chấm công vào ra nhiều lần
        this.handleStatisticEarlyLateCriteriaForMultipleEntriesTimekeeping(staffWorkSchedule, validWorkIntervals);

        // Số giờ thực tế nhân viên đã làm việc của ca này
        Double totalHours = calculateTotalWorkHoursByWorkIntervals(validWorkIntervals, shiftWork);
        staffWorkSchedule.setTotalHours(totalHours);

        // Số giờ nghỉ được tính công
        Double paidLeaveHours = this.calculatePaidLeaveHours(availableRequests, shiftWork, staffWorkSchedule.getWorkingDate());
        staffWorkSchedule.setPaidLeaveHours(paidLeaveHours);

        // Số giờ nghỉ không được tính công
        Double unpaidLeaveHours = totalHours - paidLeaveHours;
        if (unpaidLeaveHours < 0) unpaidLeaveHours = 0D;
        staffWorkSchedule.setUnpaidLeaveHours(unpaidLeaveHours);


//        // Số giai đoạn đi làm đủ
//        int workEnoughCount = 0;
//        int totalShiftPeriods = shiftWork.getTimePeriods().size();
        // Cập nhật trạng thái làm việc
//		int currentStatus = staffWorkSchedule.getWorkingStatus();
//		if (currentStatus == HrConstants.StaffWorkScheduleWorkingStatus.LEAVE_WITH_PERMISSION.getValue()
//				|| currentStatus == HrConstants.StaffWorkScheduleWorkingStatus.LEAVE_WITHOUT_PERMISSION.getValue()) {
//		} else
//        if (workEnoughCount > 0) {
//            staffWorkSchedule
//                    .setWorkingStatus(HrConstants.StaffWorkScheduleWorkingStatus.PARTIAL_ATTENDANCE.getValue());
//            if (totalShiftPeriods > 0 && workEnoughCount == totalShiftPeriods) {
//                staffWorkSchedule
//                        .setWorkingStatus(HrConstants.StaffWorkScheduleWorkingStatus.FULL_ATTENDANCE.getValue());
//            }
//        } else {
//            staffWorkSchedule.setWorkingStatus(HrConstants.StaffWorkScheduleWorkingStatus.NOT_ATTENDANCE.getValue());
//        }

        // Số giờ công quy đổi được tính
        Double convertedWorkingHours = this.calculateConvertedWorkingHoursForMultipleEntry(staffWorkSchedule);
        staffWorkSchedule.setConvertedWorkingHours(convertedWorkingHours);

        // Tỷ lệ công được tính trong ca được phân
        // TH: Tính ngày công dựa trên tỉ lệ ngày công được định nghĩa trong từng giai đoạn làm việc
        Double totalPaidWork = calculateStaffWorkRatioByWorkIntervals(validWorkIntervals, shiftWork);
        // TH: Tính ngày công dựa trên tỉ lệ số giờ đã làm việc
//        Double totalPaidWork = calculateWorkRatioForOneEntryTimekeepingV2(totalHours, shiftWork);
        staffWorkSchedule.setTotalPaidWork(totalPaidWork);

        // Tỷ lệ công nghỉ phép được hưởng lương trong ca làm việc
        Double paidLeaveWorkRatio = calculatePaidLeaveWorkRatio(staffWorkSchedule);
        staffWorkSchedule.setPaidLeaveWorkRatio(paidLeaveWorkRatio);

        // Tỷ lệ công nghỉ phép KHÔNG được hưởng lương trong ca làm việc
        Double unpaidLeaveWorkRatio = calculateUnPaidLeaveWorkRatio(staffWorkSchedule);
        staffWorkSchedule.setUnpaidLeaveWorkRatio(unpaidLeaveWorkRatio);

        // Thời gian làm việc để tính đủ công của cả giai đoạn
        Double totalMinWorkTimeHour = this.getTotalMinWorkTimeHours(shiftWork);

        if (totalHours >= totalMinWorkTimeHour) {
            staffWorkSchedule.setWorkingStatus(HrConstants.StaffWorkScheduleWorkingStatus.FULL_ATTENDANCE.getValue());
        } else if (totalHours > 0) {
            staffWorkSchedule
                    .setWorkingStatus(HrConstants.StaffWorkScheduleWorkingStatus.PARTIAL_ATTENDANCE.getValue());
        } else {
            staffWorkSchedule.setWorkingStatus(HrConstants.StaffWorkScheduleWorkingStatus.NOT_ATTENDANCE.getValue());
        }


        staffWorkSchedule = staffWorkScheduleRepository.saveAndFlush(staffWorkSchedule);

        return staffWorkSchedule;
    }


    private void handleStatisticEarlyLateCriteriaForMultipleEntriesTimekeeping(StaffWorkSchedule staffWorkSchedule, List<Interval> validWorkIntervals) {
        if (staffWorkSchedule == null || staffWorkSchedule.getShiftWork() == null) return;

        ShiftWork shiftWork = staffWorkSchedule.getShiftWork();

        Integer lateArrivalCount = 0; // Số lần đi làm muộn
        Integer lateArrivalMinutes = 0; // Số phút đi muộn
        Integer earlyExitCount = 0; // Số lần về sớm
        Integer earlyExitMinutes = 0; // Số phút về sớm
        Integer earlyArrivalMinutes = 0; // Số phút đến sớm
        Integer lateExitMinutes = 0; // Số phút về muộn

        for (ShiftWorkTimePeriod shiftWorkTimePeriod : shiftWork.getTimePeriods()) {
            // Lấy thời gian check-in và check-out dựa vào loại tính toán đã chọn
            Date checkinTime = toDate(this.getFirstStartTimeFromIntervals(validWorkIntervals));
            Date checkoutTime = toDate(this.getLastCheckoutFromIntervals(validWorkIntervals));

            if (checkinTime == null) {
                continue;
            }
            // Lấy thời gian check-in đầu tiên trong toàn bộ timeSheetDetails
//            Date checkinTime = timeSheetDetails.stream().filter(detail -> detail.getStartTime() != null)
//                    .map(TimeSheetDetail::getStartTime).min(Date::compareTo).orElse(null);
//            // Lấy lần checkout cuối cùng trong toàn bộ timeSheetDetails
//            Date checkoutTime = timeSheetDetails.stream().filter(detail -> detail.getEndTime() != null)
//                    .map(TimeSheetDetail::getEndTime).max(Date::compareTo).orElse(null);

            Date periodStartTime = shiftWorkTimePeriod.getStartTime();

            Double allowedLateMinutes = shiftWorkTimePeriod.getAllowedLateMinutes();
            if (allowedLateMinutes == null) {
                allowedLateMinutes = 0D;
            }

            boolean isLate = this.isLateForShiftPeriod(checkinTime, allowedLateMinutes, periodStartTime);
            // Có đi làm muộn
            if (isLate) {
                lateArrivalCount++;
                int lateMinutes = (int) Math.ceil(this.getLateMinutes(checkinTime, periodStartTime));
                lateArrivalMinutes += lateMinutes;
            }
            // Không đi làm muộn/Đi làm sớm
            else {
                int earlyMinutes = (int) Math.ceil(this.getEarlyMinutes(checkinTime, periodStartTime));
                earlyArrivalMinutes += earlyMinutes;
            }

            // Chưa checkout
            if (checkoutTime != null) {
                Date periodEndTime = shiftWorkTimePeriod.getEndTime();

                boolean isLeaveEarly = this.isLeaveEarly(checkoutTime, periodEndTime);
                // Có về sớm
                if (isLeaveEarly) {
                    earlyExitCount++;
                    int earlyLeaveMinutes = (int) Math.ceil(this.getLeaveEarlyMinutes(checkoutTime, periodEndTime));
                    earlyExitMinutes += earlyLeaveMinutes;
                }
                // Về muộn
                else {
                    int lateLeaveMinutes = (int) Math.ceil(this.getEarlyMinutes(checkoutTime, periodEndTime));
                    lateExitMinutes += lateLeaveMinutes;
                }
            }
        }


        staffWorkSchedule.setLateArrivalCount(lateArrivalCount);
        staffWorkSchedule.setLateArrivalMinutes(lateArrivalMinutes);
        staffWorkSchedule.setEarlyExitCount(earlyExitCount);
        staffWorkSchedule.setEarlyExitMinutes(earlyExitMinutes);
        staffWorkSchedule.setEarlyArrivalMinutes(earlyArrivalMinutes);
        staffWorkSchedule.setLateExitMinutes(lateExitMinutes);
    }

//    private double calculateStaffWorkRatioWithMultipleEntries() {
//        for (ShiftWorkTimePeriod shiftWorkTimePeriod : shiftWork.getTimePeriods()) {
//            List<TimeSheetDetail> timeSheetDetails = timeSheetDetailRepository.findByStaffIdScheduleIdAndPeriodId(
//                    staffWorkSchedule.getStaff().getId(), staffWorkSchedule.getId(), shiftWorkTimePeriod.getId());
//            // Chưa có lần chấm công nào cho giai đoạn này
//            if (timeSheetDetails == null || timeSheetDetails.isEmpty()) {
//                continue;
//            }
//
//            // Lấy thời gian check-in và check-out dựa vào loại tính toán đã chọn
//            Date checkinTime = getCheckinTime(timeSheetDetails, timekeepingCalculationType);
//            Date checkoutTime = getCheckoutTime(timeSheetDetails, timekeepingCalculationType);
//            if (checkinTime == null) {
//                continue;
//            }
//            // Lấy thời gian check-in đầu tiên trong toàn bộ timeSheetDetails
////            Date checkinTime = timeSheetDetails.stream().filter(detail -> detail.getStartTime() != null)
////                    .map(TimeSheetDetail::getStartTime).min(Date::compareTo).orElse(null);
////            // Lấy lần checkout cuối cùng trong toàn bộ timeSheetDetails
////            Date checkoutTime = timeSheetDetails.stream().filter(detail -> detail.getEndTime() != null)
////                    .map(TimeSheetDetail::getEndTime).max(Date::compareTo).orElse(null);
//
//            Date periodStartTime = shiftWorkTimePeriod.getStartTime();
//
//            Double allowedLateMinutes = shiftWorkTimePeriod.getAllowedLateMinutes();
//            if (allowedLateMinutes == null) {
//                allowedLateMinutes = 0D;
//            }
//
//            boolean isLate = this.isLateForShiftPeriod(checkinTime, allowedLateMinutes, periodStartTime);
//            // Có đi làm muộn
//            if (isLate) {
//                lateArrivalCount++;
//                int lateMinutes = (int) Math.ceil(this.getLateMinutes(checkinTime, periodStartTime));
//                lateArrivalMinutes += lateMinutes;
//            }
//            // Không đi làm muộn/Đi làm sớm
//            else {
//                int earlyMinutes = (int) Math.ceil(this.getEarlyMinutes(checkinTime, periodStartTime));
//                earlyArrivalMinutes += earlyMinutes;
//            }
//
//            // Chưa checkout
//            if (checkoutTime != null) {
//                Date periodEndTime = shiftWorkTimePeriod.getEndTime();
//
//                boolean isLeaveEarly = this.isLeaveEarly(checkoutTime, periodEndTime);
//                // Có về sớm
//                if (isLeaveEarly) {
//                    earlyExitCount++;
//                    int earlyLeaveMinutes = (int) Math.ceil(this.getLeaveEarlyMinutes(checkoutTime, periodEndTime));
//                    earlyExitMinutes += earlyLeaveMinutes;
//                }
//                // Về muộn
//                else {
//                    int lateLeaveMinutes = (int) Math.ceil(this.getEarlyMinutes(checkoutTime, periodEndTime));
//                    lateExitMinutes += lateLeaveMinutes;
//                }
//            }
//
//            // Tính tổng số giờ đã chấm công của giai đoạn này dựa vào loại tính toán
//            double totalCheckedHoursByPeriod = this.calculateHoursByPeriod(shiftWorkTimePeriod, timeSheetDetails,
//                    timekeepingCalculationType);
////            double totalCheckedHoursByPeriod = this.totalCheckedHoursByPeriod(shiftWorkTimePeriod, timeSheetDetails);
//            totalHours += totalCheckedHoursByPeriod;
//
//            // LÀM ĐỦ THỜI GIAN TỐI THIỂU TRONG GIAI ĐOẠN VÀ KHÔNG ĐẾN MUỘN TRONG GIAI ĐOẠN
//            // NÀY THÌ MỚI ĐƯỢC TÍNH CÔNG
//            if (shiftWorkTimePeriod.getMinWorkTimeHour() == null) {
//                shiftWorkTimePeriod.setMinWorkTimeHour(0.0);
//            }
//
//            boolean isWorkEnough = totalCheckedHoursByPeriod >= shiftWorkTimePeriod.getMinWorkTimeHour();
////                    * HrConstants.WORK_ENOUGH_THRESHOLD;
//            if (isLate || !isWorkEnough) {
//                continue;
//            }
//            // Tính thêm số lần đi đủ
//            workEnoughCount++;
//
//            // Tính công
//            if (shiftWorkTimePeriod.getWorkRatio() == null) {
//                shiftWorkTimePeriod.setWorkRatio(0.0);
//            }
//            totalPaidWork += shiftWorkTimePeriod.getWorkRatio();
//        }
//    }

    /**
     * Lấy thời gian check-in dựa vào loại tính toán (FIFO, FILO, LILO)
     *
     * @param timeSheetDetails Danh sách các lần chấm công
     * @param calculationType  Loại tính toán
     * @return Thời gian check-in
     */
    private Date getCheckinTime(List<TimeSheetDetail> timeSheetDetails, Integer calculationType) {
        if (timeSheetDetails == null || timeSheetDetails.isEmpty()) {
            return null;
        }

        // Lọc các bản ghi có startTime
        List<TimeSheetDetail> validEntries = timeSheetDetails.stream().filter(detail -> detail.getStartTime() != null)
                .collect(Collectors.toList());

        if (validEntries.isEmpty()) {
            return null;
        }

        if (calculationType.equals(HrConstants.TimekeepingCalculationType.FIRST_IN_FIRST_OUT.getValue())
                || calculationType.equals(HrConstants.TimekeepingCalculationType.FIRST_IN_LAST_OUT.getValue())) {
            // FIFO, FILO: Lấy thời gian check-in đầu tiên
            return validEntries.stream().map(TimeSheetDetail::getStartTime).min(Date::compareTo).orElse(null);
        } else if (calculationType.equals(HrConstants.TimekeepingCalculationType.LAST_IN_LAST_OUT.getValue())) {
            // LILO: Lấy thời gian check-in cuối cùng
            return validEntries.stream().map(TimeSheetDetail::getStartTime).max(Date::compareTo).orElse(null);
        }

        // Mặc định: Lấy thời gian check-in đầu tiên (FILO)
        return validEntries.stream().map(TimeSheetDetail::getStartTime).min(Date::compareTo).orElse(null);
    }

    /**
     * Lấy thời gian check-out dựa vào loại tính toán (FIFO, FILO, LILO)
     *
     * @param timeSheetDetails Danh sách các lần chấm công
     * @param calculationType  Loại tính toán
     * @return Thời gian check-out
     */
    private Date getCheckoutTime(List<TimeSheetDetail> timeSheetDetails, Integer calculationType) {
        if (timeSheetDetails == null || timeSheetDetails.isEmpty()) {
            return null;
        }

        // Lọc các bản ghi có endTime
        List<TimeSheetDetail> validEntries = timeSheetDetails.stream().filter(detail -> detail.getEndTime() != null)
                .collect(Collectors.toList());

        if (validEntries.isEmpty()) {
            return null;
        }

        if (calculationType.equals(HrConstants.TimekeepingCalculationType.FIRST_IN_FIRST_OUT.getValue())) {
            // FIFO: Lấy thời gian check-out đầu tiên
            return validEntries.stream().map(TimeSheetDetail::getEndTime).min(Date::compareTo).orElse(null);
        } else if (calculationType.equals(HrConstants.TimekeepingCalculationType.FIRST_IN_LAST_OUT.getValue())
                || calculationType.equals(HrConstants.TimekeepingCalculationType.LAST_IN_LAST_OUT.getValue())) {
            // FILO, LILO: Lấy thời gian check-out cuối cùng
            return validEntries.stream().map(TimeSheetDetail::getEndTime).max(Date::compareTo).orElse(null);
        }

        // Mặc định: Lấy thời gian check-out cuối cùng (FILO)
        return validEntries.stream().map(TimeSheetDetail::getEndTime).max(Date::compareTo).orElse(null);
    }

    /**
     * Tính tổng số giờ đã chấm công của giai đoạn, chỉ dựa trên giờ không phụ thuộc
     * vào ngày, sử dụng checkinTime và checkoutTime, bỏ qua các khoảng trống (gap)
     * trong timeSheetDetails. Nếu checkoutTime lớn hơn periodEndHour, dùng
     * periodEndHour.
     *
     * @param shiftWorkTimePeriod Giai đoạn ca làm việc
     * @param timeSheetDetails    Danh sách các lần chấm công
     * @param calculationType     Loại tính toán (FIFO, FILO, LILO)
     * @return Tổng số giờ
     */
    private double calculateHoursByPeriod(ShiftWorkTimePeriod shiftWorkTimePeriod,
                                          List<TimeSheetDetail> timeSheetDetails, Integer calculationType) {
        // Lấy thời gian check-in và check-out dựa vào loại tính toán
        Date checkinTime = getCheckinTime(timeSheetDetails, calculationType);
        Date checkoutTime = getCheckoutTime(timeSheetDetails, calculationType);

        if (checkinTime == null || checkoutTime == null || CollectionUtils.isEmpty(timeSheetDetails)) {
            return 0.0;
        }

        // Lấy thời gian bắt đầu và kết thúc của giai đoạn
        Date periodStartTime = shiftWorkTimePeriod.getStartTime();
        Date periodEndTime = shiftWorkTimePeriod.getEndTime();
        if (periodStartTime == null || periodEndTime == null) {
            return 0.0;
        }

        // Chuyển đổi thời gian thành giờ trong ngày
        Calendar calCheckin = Calendar.getInstance();
        calCheckin.setTime(checkinTime);
        double checkinHour = calCheckin.get(Calendar.HOUR_OF_DAY) + calCheckin.get(Calendar.MINUTE) / 60.0
                + calCheckin.get(Calendar.SECOND) / 3600.0;

        Calendar calCheckout = Calendar.getInstance();
        calCheckout.setTime(checkoutTime);
        double checkoutHour = calCheckout.get(Calendar.HOUR_OF_DAY) + calCheckout.get(Calendar.MINUTE) / 60.0
                + calCheckout.get(Calendar.SECOND) / 3600.0;

        Calendar calPeriodStart = Calendar.getInstance();
        calPeriodStart.setTime(periodStartTime);
        double periodStartHour = calPeriodStart.get(Calendar.HOUR_OF_DAY) + calPeriodStart.get(Calendar.MINUTE) / 60.0
                + calPeriodStart.get(Calendar.SECOND) / 3600.0;

        Calendar calPeriodEnd = Calendar.getInstance();
        calPeriodEnd.setTime(periodEndTime);
        double periodEndHour = calPeriodEnd.get(Calendar.HOUR_OF_DAY) + calPeriodEnd.get(Calendar.MINUTE) / 60.0
                + calPeriodEnd.get(Calendar.SECOND) / 3600.0;

        // Xử lý ca qua đêm
        boolean isOvernightShift = periodEndHour < periodStartHour;
        if (isOvernightShift) {
            if (checkoutHour < checkinHour) {
                checkoutHour += 24.0;
            }
            if (periodEndHour < periodStartHour) {
                periodEndHour += 24.0;
            }
        }

        // Giới hạn thời gian trong ca
        double effectiveCheckinHour = Math.max(checkinHour, periodStartHour);
        double effectiveCheckoutHour = Math.min(checkoutHour, periodEndHour);

        if (effectiveCheckinHour >= effectiveCheckoutHour) {
            return 0.0;
        }

        // Sắp xếp danh sách chấm công
        timeSheetDetails.sort(Comparator.comparing(TimeSheetDetail::getStartTime));

        // Tính tổng thời gian làm việc, bỏ qua gap
        double totalWorkingHours = 0.0;
        double lastEndHour = effectiveCheckinHour;

        for (TimeSheetDetail detail : timeSheetDetails) {
            Date startTime = detail.getStartTime();
            Date endTime = detail.getEndTime();
            if (startTime == null || endTime == null) {
                continue;
            }

            // Chuyển đổi thời gian chấm công thành giờ trong ngày
            Calendar calStart = Calendar.getInstance();
            calStart.setTime(startTime);
            double startHour = calStart.get(Calendar.HOUR_OF_DAY) + calStart.get(Calendar.MINUTE) / 60.0
                    + calStart.get(Calendar.SECOND) / 3600.0;

            Calendar calEnd = Calendar.getInstance();
            calEnd.setTime(endTime);
            double endHour = calEnd.get(Calendar.HOUR_OF_DAY) + calEnd.get(Calendar.MINUTE) / 60.0
                    + calEnd.get(Calendar.SECOND) / 3600.0;

            if (isOvernightShift && endHour < startHour) {
                endHour += 24.0;
            }

            // Chỉ tính các khoảng nằm trong effectiveCheckinHour và effectiveCheckoutHour
            if (endHour <= effectiveCheckinHour || startHour >= effectiveCheckoutHour) {
                continue;
            }

            double effectiveStartHour = Math.max(startHour, effectiveCheckinHour);
            double effectiveEndHour = Math.min(endHour, effectiveCheckoutHour);

            // Nếu có gap từ lần trước, không tính gap đó
            if (effectiveStartHour > lastEndHour) {
                totalWorkingHours += effectiveEndHour - effectiveStartHour;
            } else if (effectiveEndHour > lastEndHour) {
                // Trùng lặp, chỉ tính phần không trùng
                totalWorkingHours += effectiveEndHour - lastEndHour;
            }

            lastEndHour = Math.max(lastEndHour, effectiveEndHour);
        }

        // Nếu không có khoảng làm việc nào hợp lệ, trả về toàn bộ thời gian từ
        // effectiveCheckinHour đến effectiveCheckoutHour
        return totalWorkingHours > 0 ? totalWorkingHours : (effectiveCheckoutHour - effectiveCheckinHour);
    }
}



