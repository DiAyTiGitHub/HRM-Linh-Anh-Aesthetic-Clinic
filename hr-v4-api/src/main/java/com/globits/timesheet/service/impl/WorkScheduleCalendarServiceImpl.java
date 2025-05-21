package com.globits.timesheet.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.globits.hr.domain.Position;
import com.globits.hr.dto.AllowanceDto;
import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.HrOrganizationDto;
import com.globits.hr.dto.PositionDto;
import com.globits.hr.dto.PositionTitleDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.UserExtRoleDto;
import com.globits.hr.service.HrRoleService;
import com.globits.hr.service.StaffService;
import com.globits.hr.service.UserExtService;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.hr.utils.ExportExcelUtil;
import com.globits.salary.dto.SalaryPeriodDto;
import com.globits.salary.service.SalaryPeriodService;
import com.globits.timesheet.domain.AbsenceRequest;
import com.globits.timesheet.domain.LeaveType;
import com.globits.timesheet.dto.IndexLeaveTypeDto;
import com.globits.timesheet.dto.LeaveTypeDto;
import com.globits.timesheet.dto.calendar.ScheduledShiftWorkDto;
import com.globits.timesheet.dto.calendar.ScheduledStaffItemDto;
import com.globits.timesheet.dto.calendar.ScheduledTimesheetDetailDto;
import com.globits.timesheet.dto.search.SearchWorkScheduleCalendarDto;
import com.globits.timesheet.repository.TimeSheetDetailRepository;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.globits.hr.HrConstants;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffWorkSchedule;
import com.globits.hr.repository.AbsenceRequestRepository;
import com.globits.hr.repository.PositionRepository;
import com.globits.hr.repository.ShiftWorkRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.repository.StaffWorkScheduleRepository;
import com.globits.timesheet.dto.calendar.ScheduledStaffCalendarDto;
import com.globits.timesheet.service.LeaveTypeService;
import com.globits.timesheet.service.WorkScheduleCalendarService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class WorkScheduleCalendarServiceImpl implements WorkScheduleCalendarService {
    @Autowired
    private StaffWorkScheduleRepository staffWorkScheduleRepository;

    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private StaffService staffService;
    @Autowired
    private LeaveTypeService leaveTypeService;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private ShiftWorkRepository shiftWorkRepository;
    @Autowired
    private TimeSheetDetailRepository timeSheetDetailRepository;
    @Autowired
    private UserExtService userExtService;
    @Autowired
    private EntityManager manager;

    @Autowired
    private HrRoleService hrRoleService;

    @Autowired
    private SalaryPeriodService salaryPeriodService;

    @Autowired
    AbsenceRequestRepository absenceRequestRepository;

    private Query getQueryStaffsByFilter(SearchWorkScheduleCalendarDto searchDto) {
        UserExtRoleDto roleDto = staffService.getCurrentRoleUser();
        boolean checkRole = false;
        if (roleDto != null && (roleDto.isRoleSuperAdmin() || roleDto.isRoleAdmin() || roleDto.isRoleHrManager())) {
            checkRole = true;
        }

        Date fromDateBegin = DateTimeUtil.getStartOfDay(searchDto.getFromDate());
        Date toDateEnd = DateTimeUtil.getEndOfDay(searchDto.getToDate());
        searchDto.setFromDate(fromDateBegin);
        searchDto.setToDate(toDateEnd);

//        String sql = "select distinct staff from Staff staff inner join StaffWorkSchedule sws on sws.staff.id = staff.id "
//                + "where date(sws.workingDate) >= date(:fromDate) and date(sws.workingDate) <= date(:toDate) ";

        String sql = "select staff from Staff staff " +
                " LEFT JOIN Position pos ON pos.staff.id = staff.id AND pos.isMain = true ";

        String whereClause = " where (1=1) and (staff.voided is null or staff.voided = false) ";
        String orderBy = " order by staff.displayName ";
        if (!checkRole) {

//            StaffDto staffDto = userExtService.getCurrentStaff();
//            if (staffDto != null) {
//                UUID staffId1 = staffDto.getId();
            whereClause += " AND (staff.id =: staffId )";
//            }
        }
        if (searchDto.getOrganizationId() != null) {
            whereClause += " and (pos.department.organization.id = :organizationId) ";
        }
        if (searchDto.getDepartmentId() != null) {
            whereClause += " and (pos.department.id = :departmentId) ";
        }
        if (searchDto.getPositionId() != null) {
            whereClause += " and (pos.id = :positionId) ";
        }
        if (searchDto.getStaffId() != null) {
            whereClause += " and (staff.id = :staffId) ";
        }

        sql += (whereClause + orderBy);

        Query q = manager.createQuery(sql, Staff.class);

//        q.setParameter("fromDate", searchDto.getFromDate());
//        q.setParameter("toDate", searchDto.getToDate());
        if (!checkRole) {
            StaffDto staffDto = userExtService.getCurrentStaff();
            if (staffDto != null) {
                UUID staffId1 = staffDto.getId();
                q.setParameter("staffId1", staffId1);
            } else {
                return null;
            }
        }
        if (searchDto.getOrganizationId() != null) {
            q.setParameter("organizationId", searchDto.getOrganizationId());
        }
        if (searchDto.getDepartmentId() != null) {
            q.setParameter("departmentId", searchDto.getDepartmentId());
        }
        if (searchDto.getPositionId() != null) {
            q.setParameter("positionId", searchDto.getPositionId());
        }
        if (searchDto.getStaffId() != null) {
            q.setParameter("staffId", searchDto.getStaffId());
        }

        return q;
    }

    @Override
    public Page<ScheduledStaffCalendarDto> getWorkingScheduleByFilter(SearchWorkScheduleCalendarDto searchDto) {
        if (searchDto == null || searchDto.getFromDate() == null || searchDto.getToDate() == null) {
            return Page.empty();
        }

        // Lấy danh sách nhân viên với phân trang
        Page<Staff> staffPage = staffService.searchByPageEntity(searchDto);
        List<UUID> staffIds = staffPage.getContent().stream()
                .map(Staff::getId)
                .collect(Collectors.toList());

        if (staffIds.isEmpty()) {
            return Page.empty();
        }

        // Lấy danh sách ngày làm việc
        List<Date> workingDates = searchDto.getListDatesInRange();
        if (workingDates.isEmpty()) {
            return Page.empty();
        }

        // Tải trước dữ liệu position và schedule
        Map<UUID, List<PositionDto>> positionCache = new HashMap<>();
        Map<UUID, Map<Date, List<StaffWorkSchedule>>> scheduleCache = new HashMap<>();

        for (UUID staffId : staffIds) {
            positionCache.put(staffId, positionRepository.findMainPositionDtoByStaffId(staffId));

            // Tải schedule cho staff theo tất cả các ngày trong phạm vi
            Map<Date, List<StaffWorkSchedule>> schedulesByDate = new HashMap<>();
            for (Date date : workingDates) {
                List<StaffWorkSchedule> schedules = staffWorkScheduleRepository.getByStaffAndWorkingDate(staffId, date);
                if (schedules != null && !schedules.isEmpty()) {
                    schedulesByDate.put(date, schedules);
                }
            }
            scheduleCache.put(staffId, schedulesByDate);
        }

        // Lấy danh sách tất cả ID của schedule
        List<UUID> scheduleIds = scheduleCache.values().stream()
                .flatMap(map -> map.values().stream().flatMap(List::stream))
                .map(StaffWorkSchedule::getId)
                .collect(Collectors.toList());

        // Lấy tất cả chi tiết bảng chấm công bằng phương thức có sẵn
        Map<UUID, List<ScheduledTimesheetDetailDto>> timesheetDetailsMap = new HashMap<>();
        if (!scheduleIds.isEmpty()) {
            for (UUID scheduleId : scheduleIds) {
                List<ScheduledTimesheetDetailDto> timeSheetDetails = timeSheetDetailRepository.getScheduleByScheduleId(scheduleId);
                timesheetDetailsMap.put(scheduleId, timeSheetDetails);
            }
        }

        List<ScheduledStaffCalendarDto> content = new ArrayList<>();

        for (Staff staff : staffPage.getContent()) {
            ScheduledStaffCalendarDto staffCalendar = new ScheduledStaffCalendarDto(staff);
            List<ScheduledStaffItemDto> workingSchedules = new ArrayList<>();

            for (Date date : workingDates) {
                List<StaffWorkSchedule> scheduledShiftWorks = scheduleCache
                        .getOrDefault(staff.getId(), Collections.emptyMap())
                        .getOrDefault(date, Collections.emptyList());

                workingSchedules.add(new ScheduledStaffItemDto(date, scheduledShiftWorks));
            }

            staffCalendar.setWorkingSchedules(workingSchedules);
            content.add(staffCalendar);
        }

        // Tạo Page mới từ danh sách content
        return new PageImpl<>(content, staffPage.getPageable(), staffPage.getTotalElements());
    }


    @Override
    public Workbook handleExcel(SearchWorkScheduleCalendarDto dto) {
        if (dto == null) {
            return null;
        }
        dto.setHasSocialIns(true);
        String templatePath = "Empty.xlsx";

        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(templatePath)) {
            if (fileInputStream == null) {
                throw new IOException("File '" + templatePath + "' không tìm thấy trong classpath");
            }

            Workbook workbook = new XSSFWorkbook(fileInputStream);
            ExportExcelUtil.configExcelStyle(workbook);

            // Tạo style cho header
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Căn giữa ngang
            headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Căn giữa dọc
            headerCellStyle.setBorderTop(BorderStyle.THIN);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);
            headerCellStyle.setBorderLeft(BorderStyle.THIN);
            headerCellStyle.setBorderRight(BorderStyle.THIN);
            headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // Font chữ đậm và đen
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.BLACK.getIndex()); // Chữ màu đen
            headerCellStyle.setFont(font);

            Sheet sheet = workbook.getSheetAt(0);

            // Danh sách ngày từ `fromDate` đến `toDate`
            List<String> listDateAndDateOfTheWeek = new ArrayList<>();
            List<Date> listDate = new ArrayList<>();
            if (dto.getFromDate() != null && dto.getToDate() != null && !dto.getFromDate().after(dto.getToDate())) {
                LocalDate startDate = convertToLocalDate(dto.getFromDate());
                LocalDate endDate = convertToLocalDate(dto.getToDate());
                LocalDate date = startDate;
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

                while (!date.isAfter(endDate)) {
                    String dayOfWeek = getDayOfWeekInVietnamese(date);
                    String formattedDate = dateFormatter.format(convertToDate(date));
                    listDateAndDateOfTheWeek.add(dayOfWeek + " - " + formattedDate);
                    listDate.add(convertToDate(date));
                    date = date.plusDays(1);
                }
            }

            this.createExcelHeaderTimeKeepingReport(sheet, dto, headerCellStyle, listDateAndDateOfTheWeek, listDate);

            // Render dữ liệu nhân viên
            int rowIndex = 2;
            Page<ScheduledStaffCalendarDto> scheduledStaffCalendarPage;
            do {
                scheduledStaffCalendarPage = this.getTimekeepingReportByFitler(dto);
                if (scheduledStaffCalendarPage != null && scheduledStaffCalendarPage.hasContent()) {
//                    for (ScheduledStaffCalendarDto scheduledStaffCalendarDto : scheduledStaffCalendarPage.getContent()) {
//                        if (scheduledStaffCalendarDto != null) {
//                            //this.writeRowToExcel(sheet, rowIndex++, scheduledStaffCalendarDto);
//                        }
//                    }
                    for (ScheduledStaffCalendarDto scheduledStaffCalendarDto : scheduledStaffCalendarPage.getContent()) {
                        if (scheduledStaffCalendarDto != null) {
                            Row dataRow = sheet.createRow(rowIndex);
                            int cellIndex = 0;
                            // stt
                            ExportExcelUtil.createCell(dataRow, cellIndex++, rowIndex - 1, null);
                            // ma nv
                            ExportExcelUtil.createCell(dataRow, cellIndex++, scheduledStaffCalendarDto.getStaffCode(), null);
                            // ten nv
                            ExportExcelUtil.createCell(dataRow, cellIndex++, scheduledStaffCalendarDto.getDisplayName(), null);
                            ExportExcelUtil.createCell(dataRow, cellIndex++, scheduledStaffCalendarDto.getCurrentOrganization(), null);
                            ExportExcelUtil.createCell(dataRow, cellIndex++, scheduledStaffCalendarDto.getCodeCurrentDepartment(), null);
                            ExportExcelUtil.createCell(dataRow, cellIndex++, scheduledStaffCalendarDto.getCurrentDepartment(), null);
                            ExportExcelUtil.createCell(dataRow, cellIndex++, scheduledStaffCalendarDto.getCodeCurrentPositionTitle(), null);
                            ExportExcelUtil.createCell(dataRow, cellIndex++, scheduledStaffCalendarDto.getCurrentPositionTitle(), null);
                            ExportExcelUtil.createCell(dataRow, cellIndex++, scheduledStaffCalendarDto.getCodeCurrentPosition(), null);
                            ExportExcelUtil.createCell(dataRow, cellIndex++, scheduledStaffCalendarDto.getCurrentPosition(), null);

                            // Dữ liệu từng ngày làm việc (nếu có)
                            for (Date date : listDate) {
                                if (scheduledStaffCalendarDto.getWorkingSchedules() != null && !scheduledStaffCalendarDto.getWorkingSchedules().isEmpty()) {
                                    boolean hasData = false;
                                    for (ScheduledStaffItemDto scheduledStaffItem : scheduledStaffCalendarDto.getWorkingSchedules()) {
                                        LocalDate workingDate = scheduledStaffItem.getWorkingDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                        LocalDate targetDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                        if (workingDate.isEqual(targetDate)) {
                                            if (scheduledStaffItem.getTotalAssignHours() != null && scheduledStaffItem.getTotalAssignHours() > 0.0) {
                                                StringBuilder sb = new StringBuilder();
                                                sb.append(scheduledStaffItem.getTotalWorkingHours() != null ? scheduledStaffItem.getTotalWorkingHours() : "0");
                                                sb.append(" / ");
                                                sb.append(scheduledStaffItem.getTotalAssignHours() != null ? scheduledStaffItem.getTotalAssignHours() : "0");

                                                ExportExcelUtil.createCell(dataRow, cellIndex++, sb, null);
                                                hasData = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (!hasData) {
                                        ExportExcelUtil.createCell(dataRow, cellIndex++, "", null);
                                    }
                                } else {
                                    ExportExcelUtil.createCell(dataRow, cellIndex++, "", null);
                                }
                            }
                            // Render các cột cuối bảng (check null trước)
                            //
                            dataRow.createCell(cellIndex++).setCellValue(
                                    scheduledStaffCalendarDto.getTotalAssignedHours() != null ? scheduledStaffCalendarDto.getTotalAssignedHours().toString() : "0"
                            );
                            dataRow.createCell(cellIndex++).setCellValue(
                                    scheduledStaffCalendarDto.getTotalWorkedHours() != null ? scheduledStaffCalendarDto.getTotalWorkedHours().toString() : "0"
                            );
                            dataRow.createCell(cellIndex++).setCellValue(
                                    scheduledStaffCalendarDto.getConfirmedOTHoursBeforeShift() != null ? scheduledStaffCalendarDto.getConfirmedOTHoursBeforeShift().toString() : "0"
                            );
                            dataRow.createCell(cellIndex++).setCellValue(
                                    scheduledStaffCalendarDto.getConfirmedOTHoursAfterShift() != null ? scheduledStaffCalendarDto.getConfirmedOTHoursAfterShift().toString() : "0"
                            );
                            dataRow.createCell(cellIndex++).setCellValue(
                                    scheduledStaffCalendarDto.getConvertedWorkingHours() != null ? scheduledStaffCalendarDto.getConvertedWorkingHours().toString() : "0"
                            );

                            //
                            dataRow.createCell(cellIndex++).setCellValue(
                                    scheduledStaffCalendarDto.getTotalAssignedShifts() != null ? scheduledStaffCalendarDto.getTotalAssignedShifts().toString() : "0"
                            );
                            dataRow.createCell(cellIndex++).setCellValue(
                                    scheduledStaffCalendarDto.getTotalFullAttendanceShifts() != null ? scheduledStaffCalendarDto.getTotalFullAttendanceShifts().toString() : "0"
                            );
                            dataRow.createCell(cellIndex++).setCellValue(
                                    scheduledStaffCalendarDto.getTotalPartialAttendanceShifts() != null ? scheduledStaffCalendarDto.getTotalPartialAttendanceShifts().toString() : "0"
                            );
                            dataRow.createCell(cellIndex++).setCellValue(
                                    scheduledStaffCalendarDto.getTotalNotAttendenceShifts() != null ? scheduledStaffCalendarDto.getTotalNotAttendenceShifts().toString() : "0"
                            );
//                            if (scheduledStaffCalendarDto.getShiftLeaveTypes() != null && scheduledStaffCalendarDto.getShiftLeaveTypes().size() > 0) {
//                                for (IndexLeaveTypeDto indexLeaveTypeDto : scheduledStaffCalendarDto.getShiftLeaveTypes()) {
//                                    dataRow.createCell(cellIndex++).setCellValue(
//                                            indexLeaveTypeDto.getTotalShifts() != null ? indexLeaveTypeDto.getTotalShifts().toString() : "0"
//                                    );
//                                }
//                            }
                            //
                            dataRow.createCell(cellIndex++).setCellValue(
                                    scheduledStaffCalendarDto.getLateArrivalCount() != null ? scheduledStaffCalendarDto.getLateArrivalCount().toString() : "0"
                            );
                            dataRow.createCell(cellIndex++).setCellValue(
                                    scheduledStaffCalendarDto.getEarlyExitCount() != null ? scheduledStaffCalendarDto.getEarlyExitCount().toString() : "0"
                            );

                            //
                            dataRow.createCell(cellIndex++).setCellValue(
                                    scheduledStaffCalendarDto.getLateArrivalMinutes() != null ? scheduledStaffCalendarDto.getLateArrivalMinutes().toString() : "0"
                            );
                            dataRow.createCell(cellIndex++).setCellValue(
                                    scheduledStaffCalendarDto.getEarlyExitMinutes() != null ? scheduledStaffCalendarDto.getEarlyExitMinutes().toString() : "0"
                            );
                            dataRow.createCell(cellIndex++).setCellValue(
                                    scheduledStaffCalendarDto.getEarlyArrivalMinutes() != null ? scheduledStaffCalendarDto.getEarlyArrivalMinutes().toString() : "0"
                            );
                            dataRow.createCell(cellIndex++).setCellValue(
                                    scheduledStaffCalendarDto.getLateExitMinutes() != null ? scheduledStaffCalendarDto.getLateExitMinutes().toString() : "0"
                            );

                            //
                            dataRow.createCell(cellIndex++).setCellValue(
                                    scheduledStaffCalendarDto.getTotalPaidWork() != null ? scheduledStaffCalendarDto.getTotalPaidWork().toString() : "0"
                            );

                            rowIndex++;
                        }
                    }

                }
                dto.setPageIndex(dto.getPageIndex() + 1); // Tăng trang để lấy tiếp dữ liệu
            } while (scheduledStaffCalendarPage != null && scheduledStaffCalendarPage.hasNext());

            return workbook;
        } catch (IOException e) {
            System.err.println("Error reading Excel template: " + e.getMessage());
            return null;
        }
    }

    public void createExcelHeaderTimeKeepingReport(Sheet sheet, SearchWorkScheduleCalendarDto dto, CellStyle headerCellStyle, List<String> listDateAndDateOfTheWeek,
                                                   List<Date> listDate) {
        // Danh sách tiêu đề cố định (phần đầu)
        List<String> headerBefore = Arrays.asList("STT", "Mã NV", "Họ và tên", "Đơn vị",
                "Mã phòng ban", "Phòng ban", "Mã chức danh", "Chức danh", "Mã vị trí", "Vị trí");

        // Tên nhóm
        List<String> headerAtTheEndRow1 = Arrays.asList("Số giờ", "Số ca", "Số lần", "Số phút");

        //
        int listLeaveTypeSize = 0;
        List<LeaveTypeDto> listLeaveTypeDto = leaveTypeService.getListLeaveTypeDto();
        List<String> leaveTypeNames = listLeaveTypeDto.stream()
                .map(LeaveTypeDto::getName)
                .collect(Collectors.toList());
        if (listLeaveTypeDto != null && listLeaveTypeDto.size() > 0) {
            listLeaveTypeSize = listLeaveTypeDto.size();
        }

        // Danh sách ban đầu 
        List<String> headerAtTheEndRow2 = new ArrayList<>(Arrays.asList(
                "Làm việc được phân", "Làm việc thực tế", "Làm thêm trước ca", "Làm thêm sau ca", "Công đã quy đổi",
                "Được phân", "Đi làm đủ", "Đi làm thiếu giờ", "Không đi làm",
                "Đi muộn", "Đi sớm",
                "Đi muộn", "Về sớm", "Đi sớm", "Về muộn"
        ));
        // Tìm vị trí của "Không đi làm"
        int insertIndex = headerAtTheEndRow2.indexOf("Không đi làm");
        if (insertIndex != -1) {
            // Chèn ngay sau "Không đi làm"
            headerAtTheEndRow2.addAll(insertIndex + 1, leaveTypeNames);
        } else {
            // Trường hợp không tìm thấy, thêm cuối danh sách
            headerAtTheEndRow2.addAll(leaveTypeNames);
        }
        // size tương ứng với tên nhóm
        List<Integer> mergeSizes = Arrays.asList(5, 4 + listLeaveTypeSize, 2, 4);

        List<String> headerAtTheEnd = Arrays.asList("Số công được tính");

        // Start header
        // Gộp tất cả tiêu đề vào danh sách duy nhất
        List<String> fullHeader = new ArrayList<>();
        fullHeader.addAll(headerBefore);
        fullHeader.addAll(listDateAndDateOfTheWeek);
        fullHeader.addAll(headerAtTheEndRow2);

//        // Render tiêu đề
//        for (int i = 0; i < fullHeader.size(); i++) {
//            Cell headerCell = headerRow.createCell(i);
//            headerCell.setCellValue(fullHeader.get(i));
//        }

        // Tạo 2 hàng tiêu đề
        Row headerRow1 = sheet.createRow(0); // Dòng tiêu đề 1 (Thứ, Header chính)
        Row headerRow2 = sheet.createRow(1); // Dòng tiêu đề 2 (Ngày, giữ cột cho ngày)

        // Chỉ mục cột bắt đầu
        int cellIndex = 0;

        // Hợp nhất các ô cho `headerBefore`
        for (String header : headerBefore) {
            Cell cell = headerRow1.createCell(cellIndex);
            cell.setCellValue(header);
            CellRangeAddress mergedRegion = new CellRangeAddress(0, 1, cellIndex, cellIndex);
            sheet.addMergedRegion(mergedRegion);
            this.setRegionBorder(mergedRegion, sheet);
            cell.setCellStyle(headerCellStyle);
            cellIndex++;
        }

        // Xử lý phần ngày tháng (không hợp nhất từng cột ngày)
        for (String dateStr : listDateAndDateOfTheWeek) {
            String[] parts = dateStr.split(" - "); // Tách thứ và ngày
            String dayOfWeek = parts[0];
            String date = parts[1];

            // cột thứ
            Cell cellDay = headerRow1.createCell(cellIndex);
            cellDay.setCellValue(dayOfWeek);
            cellDay.setCellStyle(headerCellStyle);
            // cột ngày
            Cell cellDate = headerRow2.createCell(cellIndex);
            cellDate.setCellValue(date);
            cellDate.setCellStyle(headerCellStyle);

            cellIndex++;
        }

        int startIndex = cellIndex;
        // Hợp nhất các ô trong `headerAtTheEndRow1`
        for (int i = 0; i < headerAtTheEndRow1.size(); i++) {
            Cell cell = headerRow1.createCell(cellIndex);
            cell.setCellValue(headerAtTheEndRow1.get(i));
            cell.setCellStyle(headerCellStyle);
            // Hợp nhất nhóm cột tương ứng
            int mergeSize = mergeSizes.get(i);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, cellIndex, cellIndex + mergeSize - 1));

            cellIndex += mergeSize; // Tăng index theo số cột đã hợp nhất
        }

        cellIndex = startIndex;

        // Đặt các tiêu đề chi tiết của `headerAtTheEndRow2`
        for (String header : headerAtTheEndRow2) {
            Cell cell = headerRow2.createCell(cellIndex);
            cell.setCellValue(header);
            cell.setCellStyle(headerCellStyle);
            cellIndex++; // Chỉ tăng từng ô một
        }

        // Đặt các tiêu đề chi tiết của `headerAtTheEnd` giống `headerBefore`
        for (String header : headerAtTheEnd) {
            Cell cell = headerRow1.createCell(cellIndex);
            cell.setCellValue(header);
            sheet.addMergedRegion(new CellRangeAddress(0, 1, cellIndex, cellIndex));
            CellRangeAddress mergedRegion = new CellRangeAddress(0, 1, cellIndex, cellIndex);
            this.setRegionBorder(mergedRegion, sheet);
            cell.setCellStyle(headerCellStyle);
            cellIndex++;
        }
        // End header

        // style hearder
        for (int i = 0; i < fullHeader.size(); i++) {
            sheet.autoSizeColumn(i);
        }
    }


    private static String getDayOfWeekInVietnamese(LocalDate date) {
        switch (date.getDayOfWeek()) {
            case MONDAY:
                return "T2";
            case TUESDAY:
                return "T3";
            case WEDNESDAY:
                return "T4";
            case THURSDAY:
                return "T5";
            case FRIDAY:
                return "T6";
            case SATURDAY:
                return "T7";
            case SUNDAY:
                return "CN";
            default:
                return "";
        }
    }

    private static LocalDate convertToLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static Date convertToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }


    @Override
    public SearchWorkScheduleCalendarDto getInitialTimekeepingReportFilter() {
        SearchWorkScheduleCalendarDto response = new SearchWorkScheduleCalendarDto();

        response.setPageIndex(1);
        response.setPageSize(10);

        List<SalaryPeriodDto> activePeriods = salaryPeriodService.getActivePeriodsByDate(new Date());
        if (activePeriods != null && !activePeriods.isEmpty()) {
            SalaryPeriodDto period = activePeriods.get(0);
            response.setSalaryPeriod(period);
            response.setSalaryPeriodId(period.getId());
            response.setFromDate(period.getFromDate());
            response.setToDate(period.getToDate());
        }

        Staff staff = userExtService.getCurrentStaffEntity();
        if (staff == null) {
            return response;
        }

        response.setStaff(new StaffDto());
        response.getStaff().setId(staff.getId());
        response.getStaff().setStaffCode(staff.getStaffCode());
        response.getStaff().setDisplayName(staff.getDisplayName());
        response.setStaffId(response.getStaff().getId());

        if (staff.getCurrentPositions() != null && !staff.getCurrentPositions().isEmpty()) {
            Position mainPosition = null;

            for (Position position : staff.getCurrentPositions()) {
                if (position != null && position.getIsMain() != null && position.getIsMain().equals(true)) {
                    mainPosition = position;
                    break;
                }
            }

            if (mainPosition != null) {

                if (mainPosition.getTitle() != null) {
                    PositionTitleDto positionTitle = new PositionTitleDto();

                    positionTitle.setId(mainPosition.getTitle().getId());
                    positionTitle.setCode(mainPosition.getTitle().getCode());
                    positionTitle.setName(mainPosition.getTitle().getName());

                    response.setPositionTitle(positionTitle);
                    response.setPositionTitleId(positionTitle.getId());
                }

                if (mainPosition.getDepartment() != null) {
                    HRDepartmentDto department = new HRDepartmentDto();

                    department.setId(mainPosition.getDepartment().getId());
                    department.setCode(mainPosition.getDepartment().getCode());
                    department.setName(mainPosition.getDepartment().getName());

                    response.setDepartment(department);
                    response.setDepartmentId(department.getId());
                }

                if (mainPosition.getDepartment() != null && mainPosition.getDepartment().getOrganization() != null) {
                    HrOrganizationDto organization = new HrOrganizationDto();

                    organization.setId(mainPosition.getDepartment().getOrganization().getId());
                    organization.setCode(mainPosition.getDepartment().getOrganization().getCode());
                    organization.setName(mainPosition.getDepartment().getOrganization().getName());

                    response.setOrganization(organization);
                    response.setOrganizationId(organization.getId());
                }
            }
        }

        return response;
    }

    public Page<ScheduledStaffCalendarDto> getTimekeepingReportByFitler(SearchWorkScheduleCalendarDto searchDto) {
        if (searchDto == null || searchDto.getFromDate() == null || searchDto.getToDate() == null) {
            return Page.empty();
        }
        // Lấy danh sách nhân viên với phân trang
        Page<Staff> staffPage = staffService.searchByPageEntity(searchDto);
        List<UUID> staffIds = staffPage.getContent().stream()
                .map(Staff::getId)
                .collect(Collectors.toList());

        // Lấy danh sách ngày một lần duy nhất
        List<Date> workingDates = searchDto.getListDatesInRange();
        // Tải trước dữ liệu position và schedule cho tất cả staff trong page
        Map<UUID, List<PositionDto>> positionCache = new HashMap<>();
        Map<UUID, Map<Date, List<StaffWorkSchedule>>> scheduleCache = new HashMap<>();

        Map<UUID, List<IndexLeaveTypeDto>> indexLeaveTypeCache = new HashMap<>();
        List<LeaveTypeDto> listLeaveTypeDto = leaveTypeService.getListLeaveTypeDto();
        List<IndexLeaveTypeDto> listIndexLeaveType = new ArrayList<>();
        for (LeaveTypeDto leaveType : listLeaveTypeDto) {
            IndexLeaveTypeDto indexLeaveType = new IndexLeaveTypeDto();
            indexLeaveType.setNameLeaveType(leaveType.getName());
            indexLeaveType.setCodeLeaveType(leaveType.getCode());
            indexLeaveType.setTotalShifts(0);
            listIndexLeaveType.add(indexLeaveType);
        }

        for (UUID staffId : staffIds) {
            positionCache.put(staffId, positionRepository.findMainPositionDtoByStaffId(staffId));

            // Tải schedule cho staff theo tất cả các ngày trong phạm vi
            Map<Date, List<StaffWorkSchedule>> schedulesByDate = new HashMap<>();
            for (Date date : workingDates) {
                //List<StaffWorkSchedule> schedules = staffWorkScheduleRepository.getByStaffAndWorkingDate(staffId, date);
                List<StaffWorkSchedule> schedules = staffWorkScheduleRepository.getByStaffAndWorkingDateWithNeedManagerApproval(staffId, date);
                if (schedules != null && !schedules.isEmpty()) {
                    schedulesByDate.put(date, schedules);
                }
            }
            scheduleCache.put(staffId, schedulesByDate);

            // khởi tạo các loại nghỉ cho từng nhân viên
            indexLeaveTypeCache.put(staffId, listIndexLeaveType);
        }

        int currentPage = staffPage.getNumber() + 1;
        int pageSize = staffPage.getSize();
        AtomicInteger displayIndex = new AtomicInteger((currentPage - 1) * pageSize + 1);

        // Chuyển đổi từ Page<Staff> sang Page<ScheduledStaffCalendarDto>
        return staffPage.map(staff -> {
            ScheduledStaffCalendarDto staffCalendar = new ScheduledStaffCalendarDto(staff);
            staffCalendar.setDisplayOrder(displayIndex.getAndIncrement());

            // Sử dụng dữ liệu từ positionCache
            List<PositionDto> mainPositions = positionCache.getOrDefault(staff.getId(), Collections.emptyList());
            if (!mainPositions.isEmpty()) {
                PositionDto mainPosition = mainPositions.get(0);
                staffCalendar.setCurrentPosition(mainPosition.getName());
                staffCalendar.setCodeCurrentPosition(mainPosition.getCode());
                if (mainPosition.getTitle() != null) {
                    PositionTitleDto positionTitle = mainPosition.getTitle();
                    staffCalendar.setCurrentPositionTitle(positionTitle.getName());
                    staffCalendar.setCodeCurrentPositionTitle(positionTitle.getCode());
                }
                if (mainPosition.getDepartment() != null) {
                    HRDepartmentDto department = mainPosition.getDepartment();
                    staffCalendar.setCurrentDepartment(department.getName());
                    staffCalendar.setCodeCurrentDepartment(department.getCode());
                    if (mainPosition.getDepartment().getOrganization() != null) {
                        HrOrganizationDto org = mainPosition.getDepartment().getOrganization();
                        staffCalendar.setCurrentOrganization(org.getName());
                        staffCalendar.setCodeCurrentOrganization(org.getName());
                    }
                }
            }

            // Khởi tạo danh sách lịch làm việc
            staffCalendar.setWorkingSchedules(new ArrayList<>(workingDates.size()));
            Map<Date, List<StaffWorkSchedule>> schedulesByDate = scheduleCache.getOrDefault(staff.getId(), Collections.emptyMap());

            // Khởi tạo biến thống kê
            double totalAssignedHours = 0.0, totalWorkedHours = 0.0, convertedWorkingHours = 0.0;
            int lateArrivalCount = 0, lateArrivalMinutes = 0, earlyExitCount = 0, earlyExitMinutes = 0;
            int earlyArrivalMinutes = 0, lateExitMinutes = 0;
            double totalPaidWork = 0.0, confirmedOTHoursBeforeShift = 0.0, confirmedOTHoursAfterShift = 0.0, totalPaidLeaveWorkRatio = 0.0, totalUnpaidLeaveWorkRatio = 0.0;
            int totalFullAttendanceShifts = 0, totalPartialAttendanceShifts = 0, totalNotAttendenceShifts = 0;
            List<IndexLeaveTypeDto> indexLeaveTypeOfStaff = indexLeaveTypeCache.get(staff.getId());

            // Xử lý lịch làm việc và tính toán thống kê
            for (Date workingDate : workingDates) {
                List<StaffWorkSchedule> scheduledShiftWorks = schedulesByDate.getOrDefault(workingDate, Collections.emptyList());
                if (!scheduledShiftWorks.isEmpty()) {
                    for (StaffWorkSchedule staffWorkSchedule : scheduledShiftWorks) {
                        totalAssignedHours += getValueOrDefault(staffWorkSchedule.getShiftWork().getTotalHours(), 0.0);
                        totalWorkedHours += getValueOrDefault(staffWorkSchedule.getTotalHours(), 0.0);
                        convertedWorkingHours += getValueOrDefault(staffWorkSchedule.getConvertedWorkingHours(), 0.0);
                        lateArrivalCount += getValueOrDefault(staffWorkSchedule.getLateArrivalCount(), 0);
                        lateArrivalMinutes += getValueOrDefault(staffWorkSchedule.getLateArrivalMinutes(), 0);
                        earlyExitCount += getValueOrDefault(staffWorkSchedule.getEarlyExitCount(), 0);
                        earlyExitMinutes += getValueOrDefault(staffWorkSchedule.getEarlyExitMinutes(), 0);
                        earlyArrivalMinutes += getValueOrDefault(staffWorkSchedule.getEarlyArrivalMinutes(), 0);
                        lateExitMinutes += getValueOrDefault(staffWorkSchedule.getLateExitMinutes(), 0);
                        totalPaidWork += getValueOrDefault(staffWorkSchedule.getTotalPaidWork(), 0.0);
                        confirmedOTHoursBeforeShift += getValueOrDefault(staffWorkSchedule.getConfirmedOTHoursBeforeShift(), 0.0);
                        confirmedOTHoursAfterShift += getValueOrDefault(staffWorkSchedule.getConfirmedOTHoursAfterShift(), 0.0);
                        totalPaidLeaveWorkRatio += getValueOrDefault(staffWorkSchedule.getPaidLeaveWorkRatio(), 0.0);
                        totalUnpaidLeaveWorkRatio += getValueOrDefault(staffWorkSchedule.getUnpaidLeaveWorkRatio(), 0.0);
                        Integer status = staffWorkSchedule.getWorkingStatus();
                        if (status != null) {
                            if (status.equals(HrConstants.StaffWorkScheduleWorkingStatus.FULL_ATTENDANCE.getValue())) {
                                totalFullAttendanceShifts++;
                            } else if (status.equals(HrConstants.StaffWorkScheduleWorkingStatus.PARTIAL_ATTENDANCE.getValue())) {
                                totalPartialAttendanceShifts++;
                            } else if (status.equals(HrConstants.StaffWorkScheduleWorkingStatus.NOT_ATTENDANCE.getValue())) {
                                totalNotAttendenceShifts++;
                            }
                        }
                        if (staffWorkSchedule.getLeaveType() != null) {
                            String codeLeaveType = staffWorkSchedule.getLeaveType().getCode();
                            indexLeaveTypeOfStaff.stream()
                                    .filter(item -> codeLeaveType != null && codeLeaveType.equals(item.getCodeLeaveType()))
                                    .findFirst()
                                    .ifPresent(item -> item.setTotalShifts(item.getTotalShifts() + 1));
                        }
                    }
                }
                staffCalendar.getWorkingSchedules().add(new ScheduledStaffItemDto(workingDate, scheduledShiftWorks, true));
            }

            // Gán giá trị thống kê
            staffCalendar.setTotalAssignedHours(totalAssignedHours);
            staffCalendar.setTotalWorkedHours(totalWorkedHours);
            staffCalendar.setConvertedWorkingHours(convertedWorkingHours);
            staffCalendar.setLateArrivalCount(lateArrivalCount);
            staffCalendar.setLateArrivalMinutes(lateArrivalMinutes);
            staffCalendar.setEarlyExitCount(earlyExitCount);
            staffCalendar.setEarlyExitMinutes(earlyExitMinutes);
            staffCalendar.setEarlyArrivalMinutes(earlyArrivalMinutes);
            staffCalendar.setLateExitMinutes(lateExitMinutes);
            staffCalendar.setTotalPaidWork(totalPaidWork);
            staffCalendar.setConfirmedOTHoursBeforeShift(confirmedOTHoursBeforeShift);
            staffCalendar.setConfirmedOTHoursAfterShift(confirmedOTHoursAfterShift);
            staffCalendar.setTotalFullAttendanceShifts(totalFullAttendanceShifts);
            staffCalendar.setTotalPartialAttendanceShifts(totalPartialAttendanceShifts);
            staffCalendar.setTotalNotAttendenceShifts(totalNotAttendenceShifts);
            staffCalendar.setShiftLeaveTypes(indexLeaveTypeOfStaff);
            staffCalendar.setTotalPaidLeaveWorkRatio(totalPaidLeaveWorkRatio);
            staffCalendar.setTotalUnpaidLeaveWorkRatio(totalUnpaidLeaveWorkRatio);

            this.calculateTotalWorkHoursAndShiftsist(staffCalendar);

            return staffCalendar;
        });
    }

    // Helper method for null-safe value retrieval
    private static <T extends Number> T getValueOrDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    // tính toán một số total
    private void calculateTotalWorkHoursAndShiftsist(ScheduledStaffCalendarDto staffCalendar) {

        // Tổng số ca làm việc được phân
        Integer totalAssignedShifts = 0;

//        //  Số ca làm việc nhân viên nghỉ có phép HƯỞNG LƯƠNG
//        Integer totalAuthorizedPaidLeaveShifts = 0;
//        //  Số ca làm việc nhân viên nghỉ có phép KHÔNG HƯỞNG LƯƠNG
//        Integer totalAuthorizedUnPaidLeaveShifts = 0;

        List<ScheduledStaffItemDto> listScheduledStaffItem = staffCalendar.getWorkingSchedules();
        if (listScheduledStaffItem != null && !listScheduledStaffItem.isEmpty()) {
            for (ScheduledStaffItemDto scheduledStaffItem : listScheduledStaffItem) {

                if (scheduledStaffItem.getShiftWorks() != null && !scheduledStaffItem.getShiftWorks().isEmpty()) {
                    totalAssignedShifts += scheduledStaffItem.getShiftWorks().size();

//                    for (ScheduledShiftWorkDto scheduledShiftWork : scheduledStaffItem.getShiftWorks()) {
//                        if (staffCalendar.getStaffId() != null && scheduledShiftWork.getId() != null) {
//                        	// Nghỉ có lương được phê duyệt
//                            List<AbsenceRequest> listAbsenceRequestTypePaidLeave = absenceRequestRepository.getListAbsenceRequest(staffCalendar.getStaffId(),
//                                    scheduledShiftWork.getId(), HrConstants.AbsenceRequestType.PAID_LEAVE.getValue(),
//                                    HrConstants.AbsenceRequestApprovalStatus.APPROVED.getValue());
//                            if (listAbsenceRequestTypePaidLeave != null && !listAbsenceRequestTypePaidLeave.isEmpty()) {
//                                totalAuthorizedPaidLeaveShifts += listAbsenceRequestTypePaidLeave.size();
//                            }
//                            // Nghỉ không lương được phê duyệt
//                            List<AbsenceRequest> listAbsenceRequestTypeUnPaidLeave = absenceRequestRepository.getListAbsenceRequest(staffCalendar.getStaffId(),
//                                    scheduledShiftWork.getId(), HrConstants.AbsenceRequestType.UNPAID_LEAVE.getValue(),
//                                    HrConstants.AbsenceRequestApprovalStatus.APPROVED.getValue());
//                            if (listAbsenceRequestTypeUnPaidLeave != null && !listAbsenceRequestTypeUnPaidLeave.isEmpty()) {
//                                totalAuthorizedUnPaidLeaveShifts += listAbsenceRequestTypeUnPaidLeave.size();
//                            }
//                        }
//                    }
                }
            }
        }
        staffCalendar.setTotalAssignedShifts(totalAssignedShifts);

//        staffCalendar.setTotalAuthorizedPaidLeaveShifts(totalAuthorizedPaidLeaveShifts);
//        staffCalendar.setTotalAuthorizedUnPaidLeaveShifts(totalAuthorizedUnPaidLeaveShifts);
    }


    @Override
    public ScheduledStaffCalendarDto getWorkCalendarOfStaff(SearchWorkScheduleCalendarDto
                                                                    searchDto) {
        if (searchDto.getChosenMonth() == null || searchDto.getChosenYear() == null) {
            System.out.println("Month and year must not be null.");
            return null;
        }

        // Create a Calendar instance and set it to the first day of the month
        Calendar calendar = Calendar.getInstance();
        calendar.set(searchDto.getChosenYear(), searchDto.getChosenMonth() - 1, 1); // Month is 0-based in Calendar

        // Get last date by setting the day to the last day of the month
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDate = calendar.getTime();

        // Reset calendar to the first day
        calendar.set(searchDto.getChosenYear(), searchDto.getChosenMonth() - 1, 1);

        ScheduledStaffCalendarDto response = new ScheduledStaffCalendarDto();

        Staff staff = staffRepository.findById(searchDto.getStaffId()).orElse(null);
        if (staff == null) return response;

        response.setStaffId(staff.getId());
        response.setStaffCode(staff.getStaffCode());
        response.setDisplayName(staff.getDisplayName());
        response.setBirthDate(staff.getBirthDate());

        if (staff.getStaffType() != null) {
            response.setStaffType(staff.getStaffType().getName());
        }

        if (staff.getCurrentPosition() != null) {
            response.setCurrentPosition(staff.getCurrentPosition().getName());
        }

        List<ScheduledStaffItemDto> scheduledItems = new ArrayList<>();
        response.setWorkingSchedules(scheduledItems);

        // Loop through all dates from first to last
        while (!calendar.getTime().after(lastDate)) {
            Date workingDate = calendar.getTime();

            List<StaffWorkSchedule> scheduledShiftWorks = staffWorkScheduleRepository
                    .getByStaffAndWorkingDate(searchDto.getStaffId(), workingDate);

            ScheduledStaffItemDto scheduledItem = new ScheduledStaffItemDto(workingDate, scheduledShiftWorks);

            if (scheduledItem.getShiftWorks() != null && !scheduledItem.getShiftWorks().isEmpty()) {
                for (ScheduledShiftWorkDto shiftWork : scheduledItem.getShiftWorks()) {
                    List<ScheduledTimesheetDetailDto> timeSheetDetails = timeSheetDetailRepository
                            .getScheduleByScheduleId(shiftWork.getStaffWorkScheduleId());
                    shiftWork.setTimeSheetDetails(timeSheetDetails);
                }
            }

            scheduledItems.add(scheduledItem);

            calendar.add(Calendar.DAY_OF_MONTH, 1); // Move to the next day
        }

        return response;
    }

    private void setRegionBorder(CellRangeAddress region, Sheet sheet) {
        RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
    }


}
