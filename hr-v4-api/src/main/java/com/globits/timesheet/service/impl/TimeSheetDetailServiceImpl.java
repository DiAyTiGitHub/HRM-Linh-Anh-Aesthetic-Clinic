package com.globits.timesheet.service.impl;

import com.globits.core.dto.BaseObjectDto;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.core.utils.CoreDateTimeUtil;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.hr.dto.*;
import com.globits.hr.dto.importExcel.StaffBankAccountImport;
import com.globits.hr.dto.importExcel.StaffFamilyRelationshipImport;
import com.globits.hr.repository.HrTaskHistoryRepository;
import com.globits.hr.repository.ShiftWorkRepository;
import com.globits.hr.repository.ShiftWorkTimePeriodRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.repository.StaffWorkScheduleRepository;
import com.globits.hr.repository.WorkingStatusRepository;
import com.globits.hr.service.*;
import com.globits.hr.utils.*;
import com.globits.salary.dto.SalaryPeriodDto;
import com.globits.salary.service.SalaryPeriodService;
import com.globits.salary.service.SalaryResultStaffItemService;
import com.globits.salary.service.impl.SalaryResultServiceImpl;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;
import com.globits.task.domain.*;
import com.globits.task.dto.HrTaskDto;
import com.globits.task.dto.KanbanDto;
import com.globits.task.repository.HrSubTaskItemRepository;
import com.globits.task.repository.HrTaskRepository;
import com.globits.task.service.HrTaskService;
import com.globits.timesheet.domain.*;
import com.globits.timesheet.dto.*;
import com.globits.timesheet.dto.TimeSheetStaffDto;
import com.globits.timesheet.dto.api.SearchTimeSheetApiDto;
import com.globits.timesheet.dto.api.TimeSheetRecordDto;
import com.globits.timesheet.dto.api.TimeSheetResponseDto;
import com.globits.timesheet.dto.importExcel.ImportTimesheetDetailDto;
import com.globits.timesheet.dto.search.LeaveRequestSearchDto;
import com.globits.timesheet.dto.search.SearchTimeSheetDto;
import com.globits.timesheet.repository.ProjectActivityRepository;
import com.globits.timesheet.repository.ProjectRepository;
import com.globits.timesheet.repository.TimeSheetDetailRepository;
import com.globits.timesheet.repository.TimeSheetRepository;
import com.globits.timesheet.service.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TimeSheetDetailServiceImpl extends GenericServiceImpl<TimeSheetDetail, UUID>
        implements TimeSheetDetailService {
    private static final Logger logger = LoggerFactory.getLogger(SalaryResultServiceImpl.class);
    private static final String TEMPLATE_PATH = "ImportExportTimesheetDetailSystemTemplate.xlsx";

    @Autowired
    TimeSheetDetailRepository timeSheetDetailRepository;
    @Autowired
    TimeSheetRepository timeSheetRepository;
    @Autowired
    HrDepartmentIpService hrDepartmentIpService;
    @Autowired
    StaffRepository staffRepository;
    @Autowired
    ProjectActivityRepository projectActivityRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    private UserExtService userExtService;
    @Autowired
    private WorkingStatusRepository workingStatusRepository;
    @Autowired
    private ProjectActivityService projectActivityService;
    @Autowired
    private HrTaskService taskService;

    @Autowired
    private WorkingStatusService workingStatusService;

    @Autowired
    private TimeSheetService timeSheetService;

    @Autowired
    private StaffService staffService;
    @Autowired
    private ProjectService projectService;

    @Autowired
    private TimeSheetShiftWorkPeriodService shiftWorkPeriodService;

    @Autowired
    private HrTaskRepository hrTaskRepository;

    @Autowired
    private HrTaskHistoryRepository taskHistoryRepository;

    @Autowired
    private HrTaskHistoryService taskHistoryService;

    @Autowired
    private HrSubTaskItemRepository hrSubTaskItemRepository;

    @Autowired
    private HrTaskService hrTaskService;

    @Autowired
    private TimeSheetShiftWorkPeriodService timeSheetShiftWorkPeriodService;

    @Autowired
    private StaffWorkScheduleRepository staffWorkScheduleRepository;

    @Autowired
    private ShiftWorkRepository shiftWorkRepository;

    @Autowired
    private StaffWorkScheduleService staffWorkScheduleService;

    @Autowired
    private ShiftWorkTimePeriodService shiftWorkTimePeriodService;

    @Autowired
    private ShiftWorkTimePeriodRepository shiftWorkTimePeriodRepository;

    @Autowired
    private SalaryResultStaffItemService salaryResultStaffItemService;

    @Autowired
    private CalculateStaffWorkTimeService calculateStaffWorkTimeService;

    @Autowired
    private CalculateStaffWorkTimeServiceV2 calculateStaffWorkTimeServiceV2;

    @Autowired
    private HrRoleService hrRoleService;

    @Autowired
    private PositionStaffService positionStaffService;

    @Autowired
    private StaffHierarchyService staffHierarchyService;
    @Autowired
    private SalaryPeriodService salaryPeriodService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private EntityManager entityManager;

    private void setTimeSheetDetailValue(TimeSheetDetailDto dto, TimeSheetDetail entity) {
        Date workingDate = new Date();
        int hourStart = DateTimeUtil.getHours(dto.getStartTime());
        int minuteStart = DateTimeUtil.getMinutes(dto.getStartTime());
        int hourEnd = DateTimeUtil.getHours(dto.getEndTime());
        int minuteEnd = DateTimeUtil.getMinutes(dto.getEndTime());

        Date startTime = DateTimeUtil.setHourAndMinute(workingDate, hourStart, minuteStart);
        Date endTime = DateTimeUtil.setHourAndMinute(workingDate, hourEnd, minuteEnd);

        double totalTime = (double) Math.round(DateTimeUtil.hoursDifference(startTime, endTime) * 10) / 10;

        boolean isAdd = true;
        if (dto.getTimeSheet() != null && dto.getTimeSheet().getId() != null) {
            TimeSheet timeSheet = timeSheetService.getEntityById(dto.getTimeSheet().getId());
            if (timeSheet != null) {
                isAdd = false;
                entity.setTimeSheet(timeSheet);
                workingDate = timeSheet.getWorkingDate();
            }

        }

        if (dto.getEmployee() != null && dto.getEmployee().getId() != null) {
            Staff staff = staffService.getEntityById(dto.getEmployee().getId());
            if (staff != null)
                entity.setEmployee(staff);

        }

        if (dto.getProject() != null && dto.getProject().getId() != null) {
            Project project = projectService.getEntityById(dto.getProject().getId());
            if (project != null)
                entity.setProject(project);

        }

        if (dto.getTimeSheetShiftWorkPeriodDto() != null && dto.getTimeSheetShiftWorkPeriodDto().getId() != null) {
            TimeSheetShiftWorkPeriod timeSheetShiftWorkPeriod = shiftWorkPeriodService
                    .getEntityById(dto.getTimeSheetShiftWorkPeriodDto().getId());
            if (timeSheetShiftWorkPeriod != null)
                entity.setTimeSheetShiftWorkPeriod(timeSheetShiftWorkPeriod);

        }

        if (dto.getHrTask() != null && dto.getHrTask().getId() != null) {
            HrTask hrTask = taskService.getEntityById(dto.getHrTask().getId());
            if (hrTask != null)
                entity.setTask(hrTask);
        }

        if (dto.getProjectActivity() != null && dto.getProjectActivity().getId() != null) {
            double oldValue = 0;
            ProjectActivity projectActivity = projectActivityService.getEntityById(dto.getProjectActivity().getId());

            if (projectActivity != null) {
                entity.setActivity(projectActivity);
                if (projectActivity.getDuration() != null)
                    oldValue = projectActivity.getDuration();
            }

            if (isAdd == true) {
                this.setProjectActivitySummary(projectActivity, totalTime);
            } else {
                this.setUpdateProjectActivitySummary(projectActivity, totalTime, oldValue);
            }

        }

        if (dto.getWorkingStatus() != null && dto.getWorkingStatus().getId() != null) {
            WorkingStatus workingStatus = workingStatusService.getEntityById(dto.getWorkingStatus().getId());
            if (workingStatus != null)
                entity.setWorkingStatus(workingStatus);
        }

        entity.setDescription(dto.getDescription());
        entity.setWorkingItemTitle(dto.getWorkingItemTitle());
        entity.setDuration(totalTime);
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setApproveStatus(dto.getApproveStatus());
        entity.setPriority(dto.getPriority());

    }

    public void setProjectActivitySummary(ProjectActivity projectActivity, Double duration) {

        // if (projectActivity.getStartTime() == null
        // || (projectActivity.getStartTime() != null &&
        // startTime.before(projectActivity.getStartTime()))) {
        // projectActivity.setStartTime(startTime);
        // }
        // if (projectActivity.getEndTime() == null
        // || (projectActivity.getEndTime() != null &&
        // endTime.after(projectActivity.getEndTime()))) {
        // projectActivity.setEndTime(endTime);
        // }
        if (projectActivity.getDuration() != null) {
            projectActivity.setDuration(projectActivity.getDuration() + duration);
        } else {
            projectActivity.setDuration(duration);
        }

        if (projectActivity.getParent() != null) {
            this.setProjectActivitySummary(projectActivity.getParent(), duration);
        } else {
            return;
        }

        projectActivityRepository.save(projectActivity);

    }

    public void setUpdateProjectActivitySummary(ProjectActivity projectActivity, Double duration, Double oldValue) {

        // if (projectActivity.getStartTime() == null
        // || (projectActivity.getStartTime() != null &&
        // startTime.before(projectActivity.getStartTime()))) {
        // projectActivity.setStartTime(startTime);
        // }
        // if (projectActivity.getEndTime() == null
        // || (projectActivity.getEndTime() != null &&
        // endTime.after(projectActivity.getEndTime()))) {
        // projectActivity.setEndTime(endTime);
        // }

        if (oldValue > duration) {
            if (projectActivity.getDuration() != null) {
                projectActivity.setDuration(projectActivity.getDuration() - (oldValue - duration));
            } else {
                projectActivity.setDuration(duration);
            }
        } else if (oldValue < duration) {
            if (projectActivity.getDuration() != null) {
                projectActivity.setDuration(projectActivity.getDuration() + (duration - oldValue));
            } else {
                projectActivity.setDuration(duration);
            }
        } else {

        }

        if (projectActivity.getParent() != null) {
            this.setUpdateProjectActivitySummary(projectActivity.getParent(), duration, oldValue);
        } else {
            return;
        }

        projectActivityRepository.save(projectActivity);

    }

    public void setDeleteProjectActivitySummary(ProjectActivity projectActivity, Date startTime, Date endTime,
                                                Double oldValue) {

        if (projectActivity == null)
            return;

        if (projectActivity.getStartTime() != null) {
            projectActivity.setStartTime(startTime);
            if (projectActivity.getEndTime() != null) {
                projectActivity.setEndTime(endTime);
                if (oldValue != null) {
                    if (projectActivity.getDuration() != null || projectActivity.getDuration() != 0) {
                        if (projectActivity.getDuration() - (oldValue) != 0) {
                            projectActivity.setDuration(projectActivity.getDuration() - (oldValue));

                        } else {
                            projectActivity.setDuration(null);
                            projectActivity.setStartTime(null);
                            projectActivity.setEndTime(null);
                        }

                    }
                    if (projectActivity.getParent() != null) {
                        this.setDeleteProjectActivitySummary(projectActivity.getParent(), startTime, endTime, oldValue);
                    } else {
                        return;
                    }
                }
            }
        }

        projectActivityRepository.save(projectActivity);

    }

    @Override
    public TimeSheetDetailDto saveTimeSheetDetail(TimeSheetDetailDto dto, UUID id) {

        Date workingDate = dto.getTimeSheet().getWorkingDate();
        int hourStart = DateTimeUtil.getHours(dto.getStartTime());
        int minuteStart = DateTimeUtil.getMinutes(dto.getStartTime());
        int hourEnd = DateTimeUtil.getHours(dto.getEndTime());
        int minuteEnd = DateTimeUtil.getMinutes(dto.getEndTime());
        Date startTime = DateTimeUtil.setHourAndMinute(workingDate, hourStart, minuteStart);
        Date endTime = DateTimeUtil.setHourAndMinute(workingDate, hourEnd, minuteEnd);

        if (endTime.before(startTime)) {
            return null;
        }

        TimeSheetDetail entity = null;
        if (id != null) {
            Optional<TimeSheetDetail> opEntity = timeSheetDetailRepository.findById(id);
            if (opEntity.isPresent())
                entity = opEntity.get();
        }

        if (entity == null) {
            entity = new TimeSheetDetail();
        }

        setTimeSheetDetailValue(dto, entity);
        entity = timeSheetDetailRepository.save(entity);
        return new TimeSheetDetailDto(entity);
    }

    @Override
    public Page<TimeSheetDetailDto> getPage(int pageSize, int pageIndex) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        return timeSheetDetailRepository.getListPage(pageable);
    }

    @Override
    public Boolean deleteTimeSheetDetails(List<TimeSheetDetailDto> list) {
        try {
            ArrayList<TimeSheetDetail> entities = new ArrayList<>();
            if (list != null) {
                for (TimeSheetDetailDto timeSheetDetailDto : list) {
                    if (timeSheetDetailDto != null && timeSheetDetailDto.getId() != null) {
                        TimeSheetDetail ts = timeSheetDetailRepository.getOne(timeSheetDetailDto.getId());
                        entities.add(ts);
                    }

                }
            }
            timeSheetDetailRepository.deleteInBatch(entities);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public TimeSheetDetailDto findTimeSheetDetailById(UUID id) {
        return timeSheetDetailRepository.findTimeSheetDetailById(id);
    }

    public List<TimeSheetDetailDto> getTimeSheetDetailByTimeSheetId(UUID id) {
        List<TimeSheetDetailDto> timeSheetDetailDtos = timeSheetDetailRepository.findTimeSheetDetailByTimeSheetId(id);
        return timeSheetDetailDtos;
    }

    public List<TimeSheetDetailDto> getListTimeSheetDetailByProjectActivityId(UUID id) {
        if (id != null) {

            return timeSheetDetailRepository.getListTimeSheetDetailByProjectActivityId(id);
        }
        return null;
    }

    @Override
    public List<TimeSheetDetailDto> getListTimeSheetDetailByTaskId(UUID id) {
        if (id != null) {
            return timeSheetDetailRepository.getListTimeSheetDetailByTaskId(id);
        }
        return null;
    }

    @Override
    public List<TimeSheetDetail> getListTimeSheetDetailByTaskIdNew(UUID id) {
        if (id != null) {
            return timeSheetDetailRepository.getListTimeSheetDetailByTaskIdNew(id);
        }
        return null;
    }

    @Override
    public List<TimeSheetDetailDto> getListTimeSheetDetailByTaskIdAndStaffId(UUID taskId, UUID staffId) {
        if (taskId != null && staffId != null) {
            String sql = "SELECT new com.globits.timesheet.dto.TimeSheetDetailDto(ts) FROM TimeSheetDetail ts "
                    + "where ts.task.id = :taskId and ts.employee.id = :staffId ";
            Query query = manager.createQuery(sql, TimeSheetDetailDto.class);
            query.setParameter("taskId", taskId);
            query.setParameter("staffId", staffId);
            List<TimeSheetDetailDto> entities = query.getResultList();
            return entities;
        }
        return null;
    }

    @Override
    public List<TimeSheetDetailDto> getListTimeSheetDetailByTaskIdAndStaffIdAndTimeSheetId(UUID taskId, UUID staffId,
                                                                                           UUID timeSheetId) {
        if (taskId != null && staffId != null) {
            String sql = "SELECT new com.globits.timesheet.dto.TimeSheetDetailDto(ts) FROM TimeSheetDetail ts "
                    + "where ts.task.id = :taskId and ts.employee.id = :staffId and ts.timeSheet.id = :timeSheetId";
            Query query = manager.createQuery(sql, TimeSheetDetailDto.class);
            query.setParameter("taskId", taskId);
            query.setParameter("staffId", staffId);
            query.setParameter("timeSheetId", timeSheetId);
            List<TimeSheetDetailDto> entities = query.getResultList();
            return entities;
        }
        return null;
    }

    public List<TimeSheetDetailDto> getListTimeSheetDetailByTaskIdAndStaffIdAndWorkingDate(UUID taskId, UUID staffId,
                                                                                           Date workingDate) {
        if (taskId != null && staffId != null && workingDate != null) {
            String sql = "SELECT new com.globits.timesheet.dto.TimeSheetDetailDto(ts) FROM TimeSheetDetail ts "
                    + "where ts.task.id = :taskId and ts.employee.id = :staffId  "
                    + "And ts.timeSheet.day = :day and ts.timeSheet.month = :month and ts.timeSheet.year = :year";
            Query query = manager.createQuery(sql, TimeSheetDetailDto.class);
            query.setParameter("taskId", taskId);
            query.setParameter("staffId", staffId);
            Integer year = DateTimeUtil.getYear(workingDate);
            Integer month = DateTimeUtil.getMonth(workingDate);
            Integer day = DateTimeUtil.getDay(workingDate);
            query.setParameter("year", year);
            query.setParameter("month", month);
            query.setParameter("day", day);
            List<TimeSheetDetailDto> entities = query.getResultList();
            return entities;
        }
        return null;
    }

    @Override
    public List<TimeSheetCalendarItemDto> getListTimesheetDetail(SearchTimeSheetDto dto) {
        if (dto == null) {
            return null;
        }

        String whereClause = "";
        String whereDate = "";
        String orderBy = " order by ts.startTime, ts.endTime ";

        String sql = "SELECT new com.globits.timesheet.dto.TimeSheetCalendarItemDto(ts) FROM TimeSheetDetail ts where (1=1)";
        if (dto.getStaffId() != null) {
            whereClause += " AND ts.employee.id= :staffId";
        }
        if (dto.getMonthReport() != null && dto.getYearReport() != null) {
            whereDate = " AND ts.timeSheet.id IN (SELECT t.id FROM TimeSheet t where (t.month +1) = :monthTmp AND t.year = :yearTmp)";
        }
        if (dto.getWorkingDate() != null) {
            whereDate = " AND ts.timeSheet.id IN (SELECT t.id FROM TimeSheet t where Date(t.workingDate) = :workingDateTmp)";
        }
        if (dto.getFromDate() != null && dto.getToDate() != null) {
            whereDate = " AND ts.timeSheet.id IN (SELECT t.id FROM TimeSheet t where Date(t.workingDate) >= :fromDate AND Date(t.workingDate) <= :toDate)";
        }

        sql += whereClause + whereDate + orderBy;
        Query query = manager.createQuery(sql, TimeSheetCalendarItemDto.class);

        if (dto.getStaffId() != null) {
            query.setParameter("staffId", dto.getStaffId());
        }
        if (dto.getMonthReport() != null && dto.getYearReport() != null) {
            query.setParameter("monthTmp", dto.getMonthReport());
            query.setParameter("yearTmp", dto.getYearReport());
        }
        if (dto.getWorkingDate() != null) {
            query.setParameter("workingDateTmp", dto.getWorkingDate());
        }
        if (dto.getFromDate() != null && dto.getToDate() != null) {
            query.setParameter("fromDate", dto.getFromDate());
            query.setParameter("toDate", dto.getToDate());
        }

        List<TimeSheetCalendarItemDto> entities = query.getResultList();
        return entities;
    }

    @Override
    public List<TimeSheetCalendarItemDto> getListTimesheetDetailOfAllStaff(SearchTimeSheetDto dto) {
        if (dto == null) {
            return null;
        }
        String whereClause = "";
        String sql = "SELECT new com.globits.timesheet.dto.TimeSheetCalendarItemDto(ts) FROM TimeSheetDetail ts where (1=1)";
        if (dto.getMonthReport() != null && dto.getYearReport() != null) {
            whereClause += " AND ts.timeSheet.id IN (SELECT t.id FROM TimeSheet t where (t.month +1) = :monthTmp AND t.year = :yearTmp)";
        }
        sql += whereClause;
        Query query = manager.createQuery(sql, TimeSheetCalendarItemDto.class);
        if (dto.getMonthReport() != null && dto.getYearReport() != null) {
            query.setParameter("monthTmp", dto.getMonthReport());
            query.setParameter("yearTmp", dto.getYearReport());
        }
        List<TimeSheetCalendarItemDto> entities = query.getResultList();
        return entities;
    }

    List<UUID> dataCheckPermission(UserDto user, Staff staff) {
        if (hrRoleService.hasRoleAdmin()) {
            return null;
        }
        if (hrRoleService.hasRoleManager()) {
            List<StaffDto> list = positionStaffService.getListStaffUnderManager(user.getId());
            if (!CollectionUtils.isEmpty(list)) {
                list.add(new StaffDto(staff));
                return list.stream().map(BaseObjectDto::getId).collect(Collectors.toList());
            }
        }
        return Collections.singletonList(staff.getId());
    }

    String queryCheckPermission(UserDto user) {
        String whereClausePermission = "";
        if (hrRoleService.hasRoleAdmin()) {
            return whereClausePermission;
        }
        if (hrRoleService.hasRoleManager()) {
            List<StaffDto> list = positionStaffService.getListStaffUnderManager(user.getId());
            if (!CollectionUtils.isEmpty(list)) {
                whereClausePermission = " AND entity.employee.id in :employee";
            }
        }
        return " AND (entity.employee.id in :employee) ";
    }

    void setValueCheckPermissions(List<UUID> list, Query q) {
        if (!CollectionUtils.isEmpty(list)) {
            q.setParameter("employee", list);
        }
    }

    public Page<TimeSheetDetailDto> searchByPage(SearchTimeSheetDto dto) {
        if (dto == null) {
            return null;
        }
        UserDto userDto = userExtService.getCurrentUser();
        Staff staff = userExtService.getCurrentStaffEntity();

        boolean isAdmin = RoleUtils.hasRoleAdmin(userDto);
        boolean isManager = RoleUtils.hasRoleHrManager(userDto);
        boolean isStaffView = RoleUtils.hasRoleHrStaffView(userDto);
        boolean isAssignment = RoleUtils.hasRoleHrAssignment(userDto);
        boolean isShiftAssignment = RoleUtils.hasShiftAssignmentPermission(staff);

        // Phân quyền lọc theo staff
        List<UUID> managedStaff = new ArrayList<>();
        if (!(isAdmin || isManager || isStaffView)) {
            if (isAssignment && isShiftAssignment && staff != null) {
                managedStaff = staffHierarchyService.getAllManagedStaff(staff.getId(), List.of(staff.getId()));
            } else {
                if (staff == null) return null;
                dto.setStaffId(staff.getId());
            }
        }

        int pageIndex = dto.getPageIndex() <= 1 ? 0 : dto.getPageIndex() - 1;
        int pageSize = dto.getPageSize();

        String sqlCount = "select count(entity.id) from TimeSheetDetail as entity ";
        String sql = "select new com.globits.timesheet.dto.TimeSheetDetailDto(entity) from TimeSheetDetail entity ";
        String leftJoin = " LEFT JOIN entity.timeSheet timeSheet ";

        String whereClause = " where (1=1) ";
        if (managedStaff != null && !managedStaff.isEmpty()) {
            whereClause += " AND entity.employee.id IN :staffIdList";
        }
        if (dto.getStaffId() != null) {
            whereClause += " AND ( entity.employee.id = :staffId ) ";
        }

        if (dto.getProjectId() != null && StringUtils.hasText(dto.getProjectId().toString())) {
            whereClause += " AND ( entity.project.id  =: projectId ) ";
        }
        if (dto.getWorkingStatusId() != null && StringUtils.hasText(dto.getWorkingStatusId().toString())) {
            whereClause += " AND ( entity.workingStatus.id  =: workingStatusId ) ";
        }
        if (dto.getAddressIPCheckIn() != null && !dto.getAddressIPCheckIn().isEmpty()) {
            whereClause += " AND (entity.addressIPCheckIn LIKE :addressIPCheckIn) ";
        }
        if (dto.getAddressIPCheckOut() != null && !dto.getAddressIPCheckOut().isEmpty()) {
            whereClause += " AND (entity.addressIPCheckOut LIKE :addressIPCheckOut) ";
        }

        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            whereClause += " AND (entity.employee.displayName LIKE :keyword) ";
        }

        if (dto.getShiftWorkId() != null && StringUtils.hasText(dto.getShiftWorkId().toString())) {
            whereClause += " AND ( entity.staffWorkSchedule.shiftWork.id = :shiftWorkId ) ";
        }

        if (dto.getTimeReport() != null) {
            if (dto.getTimeReport() == 2) {
                whereClause = "AND MONTH(entity.timeSheet.workingDate) =: monthReport AND YEAR(entity.timeSheet.workingDate) = :year ";
            }
            if (dto.getTimeReport() == 3) {
                whereClause = "AND YEAR(entity.timeSheet.workingDate) = :year ";
            }
        }

        if (dto.getProjectActivityId() != null) {
            whereClause += " AND ( entity.activity.id =: projectActivityId )";
        }

        if (dto.getFromDate() != null) {
            whereClause += " AND ( DATE(timeSheet.workingDate) >= DATE(:fromDate) ) ";
        }
        if (dto.getToDate() != null) {
            whereClause += " AND ( DATE(timeSheet.workingDate) <= DATE(:toDate) ) ";
        }
        if (dto.getPriority() != null) {
            whereClause += " AND ( entity.priority = :priority ) ";
        }
        if (dto.getNotScheduled() != null && dto.getNotScheduled()) {
            whereClause += " AND ( entity.staffWorkSchedule IS NULL ) ";
        }


//        if (dto.getOrganizationId() != null && StringUtils.hasText(dto.getOrganizationId().toString())) {
//            whereClause += " AND ( entity.employee.organization.id  =: organizationId ) ";
//        }
//        if (dto.getDepartmentId() != null && StringUtils.hasText(dto.getDepartmentId().toString())) {
//            whereClause += " AND ( entity.employee.department.id  =: departmentId ) ";
//        }
//
//        if (dto.getPositionId() != null && StringUtils.hasText(dto.getPositionId().toString())) {
//            whereClause += " AND ( pos.id  =: positionId ) ";
//        }
        if (dto.getPositionTitleId() != null || dto.getOrganizationId() != null || dto.getPositionId() != null || dto.getDepartmentId() != null) {
            whereClause += " AND EXISTS ";
            String subQueryPosition = "SELECT 1 FROM Position pos " +
                    "where pos.staff.id = entity.employee.id ";

            if (dto.getPositionId() != null) {
                subQueryPosition += " and pos.id = :positionId ";
            }
            if (dto.getPositionTitleId() != null) {
                subQueryPosition += " and pos.title.id = :positionTitleId ";
            }
            if (dto.getDepartmentId() != null) {
                subQueryPosition += " and pos.department.id = :departmentId ";
            }
            if (dto.getOrganizationId() != null) {
                subQueryPosition += " and pos.department.organization.id = :organizationId ";
            }

            whereClause += "(" + subQueryPosition + ")";
        }

        String orderBy = " ORDER BY timeSheet.workingDate DESC, entity.startTime DESC";
        sql += leftJoin + whereClause + orderBy;
        sqlCount += leftJoin + whereClause;

        Query query = manager.createQuery(sql, TimeSheetDetailDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getFromDate() != null) {
            query.setParameter("fromDate", dto.getFromDate());
            qCount.setParameter("fromDate", dto.getFromDate());
        }
        if (dto.getToDate() != null) {
            query.setParameter("toDate", dto.getToDate());
            qCount.setParameter("toDate", dto.getToDate());
        }

        if (dto.getAddressIPCheckIn() != null && !dto.getAddressIPCheckIn().isEmpty()) {
            query.setParameter("addressIPCheckIn", "%" + dto.getAddressIPCheckIn() + "%");
            qCount.setParameter("addressIPCheckIn", "%" + dto.getAddressIPCheckIn() + "%");
        }
        if (dto.getAddressIPCheckOut() != null && !dto.getAddressIPCheckOut().isEmpty()) {
            query.setParameter("addressIPCheckOut", "%" + dto.getAddressIPCheckOut() + "%");
            qCount.setParameter("addressIPCheckOut", "%" + dto.getAddressIPCheckOut() + "%");
        }
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            query.setParameter("keyword", "%" + dto.getKeyword() + "%");
            qCount.setParameter("keyword", "%" + dto.getKeyword() + "%");
        }

        if (dto.getTimeReport() != null) {
            if (dto.getTimeReport() == 2 && dto.getMonthReport() != null && dto.getYearReport() != null) {
                if (dto.getYearReport() != null) {
                    query.setParameter("year", dto.getYearReport());
                    qCount.setParameter("year", dto.getYearReport());
                } else {
                    query.setParameter("year", LocalDate.now().getYear());
                    qCount.setParameter("year", LocalDate.now().getYear());
                }
                query.setParameter("monthReport", dto.getMonthReport());
                qCount.setParameter("monthReport", dto.getMonthReport());
            }
            if (dto.getTimeReport() == 3) {
                if (dto.getYearReport() != null) {
                    query.setParameter("year", dto.getYearReport());
                    qCount.setParameter("year", dto.getYearReport());
                } else {
                    query.setParameter("year", LocalDate.now().getYear());
                    qCount.setParameter("year", LocalDate.now().getYear());
                }
            }
            if (dto.getTimeReport() == 1 && dto.getWeekReport() == null) {
                query.setParameter("weekReport", LocalDate.now().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
                qCount.setParameter("weekReport", LocalDate.now().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
                if (dto.getYearReport() != null) {
                    query.setParameter("year", dto.getYearReport());
                    qCount.setParameter("year", dto.getYearReport());
                } else {
                    query.setParameter("year", LocalDate.now().getYear());
                    qCount.setParameter("year", LocalDate.now().getYear());
                }
            }
            if (dto.getTimeReport() == 2 && dto.getMonthReport() == null) {
                query.setParameter("monthReport", Calendar.getInstance().get(Calendar.MONTH) + 1);
                qCount.setParameter("monthReport", Calendar.getInstance().get(Calendar.MONTH) + 1);
                if (dto.getYearReport() != null) {
                    query.setParameter("year", dto.getYearReport());
                    qCount.setParameter("year", dto.getYearReport());
                } else {
                    query.setParameter("year", LocalDate.now().getYear());
                    qCount.setParameter("year", LocalDate.now().getYear());
                }
            }
        }

        if (dto.getProjectId() != null && StringUtils.hasText(dto.getProjectId().toString())) {
            query.setParameter("projectId", dto.getProjectId());
            qCount.setParameter("projectId", dto.getProjectId());
        }
        if (dto.getProjectActivityId() != null && StringUtils.hasText(dto.getProjectActivityId().toString())) {
            query.setParameter("projectActivityId", dto.getProjectActivityId());
            qCount.setParameter("projectActivityId", dto.getProjectActivityId());
        }
        if (dto.getWorkingStatusId() != null && StringUtils.hasText(dto.getWorkingStatusId().toString())) {
            query.setParameter("workingStatusId", dto.getWorkingStatusId());
            qCount.setParameter("workingStatusId", dto.getWorkingStatusId());
        }
        if (dto.getShiftWorkId() != null && StringUtils.hasText(dto.getShiftWorkId().toString())) {
            query.setParameter("shiftWorkId", dto.getShiftWorkId());
            qCount.setParameter("shiftWorkId", dto.getShiftWorkId());
        }
        if (dto.getPriority() != null) {
            query.setParameter("priority", dto.getPriority());
            qCount.setParameter("priority", dto.getPriority());
        }

        if (dto.getStaffId() != null) {
            query.setParameter("staffId", dto.getStaffId());
            qCount.setParameter("staffId", dto.getStaffId());
        }
        if (managedStaff != null && !managedStaff.isEmpty()) {
            query.setParameter("staffIdList", managedStaff);
            qCount.setParameter("staffIdList", managedStaff);
        }

        if (dto.getPositionTitleId() != null || dto.getOrganizationId() != null || dto.getPositionId() != null || dto.getDepartmentId() != null) {
            if (dto.getPositionId() != null) {
                query.setParameter("positionId", dto.getPositionId());
                qCount.setParameter("positionId", dto.getPositionId());
            }
            if (dto.getPositionTitleId() != null) {
                query.setParameter("positionTitleId", dto.getPositionTitleId());
                qCount.setParameter("positionTitleId", dto.getPositionTitleId());
            }
            if (dto.getDepartmentId() != null) {
                query.setParameter("departmentId", dto.getDepartmentId());
                qCount.setParameter("departmentId", dto.getDepartmentId());
            }
            if (dto.getOrganizationId() != null) {
                query.setParameter("organizationId", dto.getOrganizationId());
                qCount.setParameter("organizationId", dto.getOrganizationId());
            }
        }

        if (dto.getIsExportExcel()) {
            List<TimeSheetDetailDto> listExportExcel = query.getResultList();
            return new PageImpl<>(listExportExcel);
        }
        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        List<TimeSheetDetailDto> entities = query.getResultList();
        long count = (long) qCount.getSingleResult();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    public List<TimeSheetDetailDto> getListTimeSheetDetailByProjectId(UUID id) {
        return timeSheetDetailRepository.getListTimeSheetDetailByProjectId(id);
    }

    @Override
    public List<TimeSheetDetailDto> getListTimesheetByShift(UUID shiftId, UUID staffId) {
        return timeSheetDetailRepository.getListTimeSheetDetailByShiftId(shiftId, staffId);
    }

    @Override
    public List<TimeSheetDetailDto> getListTimeSheetDetailByTask(UUID staffId, UUID shiftId, UUID taskId) {
        return timeSheetDetailRepository.getListTimeSheetDetailByTask(staffId, shiftId, taskId);
    }

    @Override
    public List<TimeSheetDetailDto> getListTimeSheetDetailBySubtaskItem(UUID taskId) {
        return timeSheetDetailRepository.getListTimeSheetDetailByTaskOfItem(taskId);
    }

    @Override
    public String updateStatus(UUID id, UUID workingStatusId) {
        if (id != null) {
            Optional<TimeSheetDetail> optional = timeSheetDetailRepository.findById(id);
            TimeSheetDetail entity = null;
            if (optional.isPresent()) {
                entity = optional.get();
            }
            if (entity == null) {
                return null;
            }
            if (workingStatusId != null) {
                WorkingStatus workingStatus = null;
                Optional<WorkingStatus> projectOptional = workingStatusRepository.findById(workingStatusId);
                if (projectOptional.isPresent()) {
                    workingStatus = projectOptional.get();
                }
                if (workingStatus == null) {
                    return null;
                }
                entity.setWorkingStatus(workingStatus);
                timeSheetDetailRepository.save(entity);
                return "success";
            }
        }
        return null;
    }

    @Override
    public Page<StaffDto> findPageByName(String textSearch, int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        String keyword = '%' + textSearch + '%';
        String sql = "select new com.globits.hr.dto.StaffDto(s) from Staff s where s.displayName like :keyword";
        String sqlCount = "select count (s.id) from Staff s where s.displayName like :keyword";
        Query q = manager.createQuery(sql);
        Query qCount = manager.createQuery(sqlCount);
        q.setParameter("keyword", keyword);
        qCount.setParameter("keyword", keyword);
        List<StaffDto> content = new ArrayList<>();
        int startPosition = (pageIndex - 1) * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<Staff> list = q.getResultList();

        for (Staff staff : list) {
            StaffDto dto = new StaffDto(staff);
            content.add(dto);
        }
        long total = (long) qCount.getSingleResult();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Boolean deleteMultiple(List<UUID> ids) {
        if (ids == null || ids.isEmpty())
            return false;
        Set<UUID> parentIds = new HashSet<>();
        for (UUID id : ids) {
            if (id == null)
                return false;

            try {
                TimeSheetDetail entity = timeSheetDetailRepository.findById(id).orElse(null);
                if (entity == null)
                    return false;

                Date startTime = new Date();
                Date endTime = new Date();
                ProjectActivity projectActivity = null;

                double oldValue = entity.getDuration();
                if (entity.getActivity() != null) {
                    Optional<ProjectActivity> optionalP = projectActivityRepository
                            .findById(entity.getActivity().getId());
                    if (optionalP.isPresent()) {
                        projectActivity = optionalP.get();
                        Date workingDate = entity.getTimeSheet().getWorkingDate();

                        if (projectActivity.getStartTime() != null) {
                            int hourStart = DateTimeUtil.getHours(projectActivity.getStartTime());
                            int minuteStart = DateTimeUtil.getMinutes(projectActivity.getStartTime());
                            startTime = DateTimeUtil.setHourAndMinute(workingDate, hourStart, minuteStart);
                        }

                        if (projectActivity.getEndTime() != null) {
                            int hourEnd = DateTimeUtil.getHours(projectActivity.getEndTime());
                            int minuteEnd = DateTimeUtil.getMinutes(projectActivity.getEndTime());
                            endTime = DateTimeUtil.setHourAndMinute(workingDate, hourEnd, minuteEnd);
                        }

                    }
                }

                UUID timesheetId = null;
                Date workingDate = null;
                if (entity.getTimeSheet() != null) {
                    timesheetId = entity.getTimeSheet().getId();
                    workingDate = entity.getTimeSheet().getWorkingDate();
                }
                UUID staffId = null;
                if (entity.getEmployee() != null) {
                    staffId = entity.getEmployee().getId();
                }
                UUID staffWorkScheduleId = null;
                if (entity.getStaffWorkSchedule() != null) {
                    StaffWorkSchedule staffWorkSchedule = entity.getStaffWorkSchedule();

                    staffWorkScheduleId = staffWorkSchedule.getId();

                    if (staffWorkSchedule.getIsLocked() != null && staffWorkSchedule.getIsLocked().equals(true)) {
                        return null;
                    }
                }

                timeSheetDetailRepository.deleteById(entity.getId());
                timeSheetRepository.flush();
                this.setDeleteProjectActivitySummary(projectActivity, startTime, endTime, oldValue);
                parentIds.add(timesheetId);

                // Thống kê giờ làm việc của nhân viên
                // calculateStaffWorkTimeService.calculateStaffWorkTime(staffWorkScheduleId);
                calculateStaffWorkTimeServiceV2.calculateStaffWorkTimeAndSave(staffWorkScheduleId);


                // Cập nhật phiếu lương của nhân viên khi chấm công: số giờ làm việc, số ca làm
                // việc
                Staff staff = staffRepository.findById(staffId).orElse(null);
                if (staff != null) {
                    salaryResultStaffItemService.updateTimekeepingDataForPayslips(staff, workingDate);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        for (UUID timesheetId : parentIds) {
            if (timesheetId != null) {
                TimeSheet timeSheet = timeSheetRepository.findById(timesheetId).orElse(null);
                Hibernate.initialize(timeSheet.getDetails());
                if (timeSheet != null && timeSheet.getDetails().isEmpty()) {
                    timeSheetRepository.deleteById(timesheetId);
                    timeSheetRepository.flush(); // Ensure immediate deletion
                }
            }
        }
        return true;
    }

    @Override
    public Boolean deleteTimeSheetDetailById(UUID id) {
        if (id == null)
            return false;

        try {
            TimeSheetDetail entity = timeSheetDetailRepository.findById(id).orElse(null);
            if (entity == null)
                return false;

            Date startTime = new Date();
            Date endTime = new Date();
            ProjectActivity projectActivity = null;

            double oldValue = entity.getDuration();
            if (entity.getActivity() != null) {
                Optional<ProjectActivity> optionalP = projectActivityRepository.findById(entity.getActivity().getId());
                if (optionalP.isPresent()) {
                    projectActivity = optionalP.get();
                    Date workingDate = entity.getTimeSheet().getWorkingDate();

                    if (projectActivity.getStartTime() != null) {
                        int hourStart = DateTimeUtil.getHours(projectActivity.getStartTime());
                        int minuteStart = DateTimeUtil.getMinutes(projectActivity.getStartTime());
                        startTime = DateTimeUtil.setHourAndMinute(workingDate, hourStart, minuteStart);
                    }

                    if (projectActivity.getEndTime() != null) {
                        int hourEnd = DateTimeUtil.getHours(projectActivity.getEndTime());
                        int minuteEnd = DateTimeUtil.getMinutes(projectActivity.getEndTime());
                        endTime = DateTimeUtil.setHourAndMinute(workingDate, hourEnd, minuteEnd);
                    }

                }
            }

            UUID timesheetId = null;
            Date workingDate = null;
            if (entity.getTimeSheet() != null) {
                timesheetId = entity.getTimeSheet().getId();
                workingDate = entity.getTimeSheet().getWorkingDate();
            }
            UUID staffId = null;
            if (entity.getEmployee() != null) {
                staffId = entity.getEmployee().getId();
            }
            UUID staffWorkScheduleId = null;
            if (entity.getStaffWorkSchedule() != null) {
                staffWorkScheduleId = entity.getStaffWorkSchedule().getId();
            }

            timeSheetDetailRepository.deleteById(entity.getId());
            this.setDeleteProjectActivitySummary(projectActivity, startTime, endTime, oldValue);
            if (timesheetId != null) {
                TimeSheet timeSheet = timeSheetRepository.findById(timesheetId).orElse(null);
                repository.flush();
                manager.refresh(timeSheet); // Force Hibernate to reload from DB
                Hibernate.initialize(timeSheet.getDetails());
                if (timeSheet != null && timeSheet.getDetails().isEmpty()) {
                    timeSheetRepository.deleteById(timesheetId);
                    timeSheetRepository.flush(); // Ensure immediate deletion
                }
            }

            // Thống kê giờ làm việc
//            calculateStaffWorkTimeService.calculateStaffWorkTime(staffWorkScheduleId);
            calculateStaffWorkTimeServiceV2.calculateStaffWorkTimeAndSave(staffWorkScheduleId);

            // Cập nhật phiếu lương của nhân viên khi chấm công: số giờ làm việc, số ca làm
            // việc
            Staff staff = staffRepository.findById(staffId).orElse(null);
            if (staff != null) {
                salaryResultStaffItemService.updateTimekeepingDataForPayslips(staff, workingDate);
            }

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public boolean updateTimesheetDetail() {
        List<TimeSheetDetail> listTimeSheetDetail = timeSheetDetailRepository.findAll();
        if (listTimeSheetDetail != null) {
            for (TimeSheetDetail timeSheetDetail : listTimeSheetDetail) {
                if (timeSheetDetail.getTimeSheet() != null && timeSheetDetail.getTimeSheet().getWorkingDate() != null) {
                    if (timeSheetDetail.getEndTime() != null && timeSheetDetail.getStartTime() != null) {
                        int hourStart = DateTimeUtil.getHours(timeSheetDetail.getStartTime());
                        int minuteStart = DateTimeUtil.getMinutes(timeSheetDetail.getStartTime());
                        int hourEnd = DateTimeUtil.getHours(timeSheetDetail.getEndTime());
                        int minuteEnd = DateTimeUtil.getMinutes(timeSheetDetail.getEndTime());
                        Date durationStart = DateTimeUtil.setHourAndMinute(
                                timeSheetDetail.getTimeSheet().getWorkingDate(), hourStart, minuteStart);
                        Date durationEnd = DateTimeUtil
                                .setHourAndMinute(timeSheetDetail.getTimeSheet().getWorkingDate(), hourEnd, minuteEnd);
                        timeSheetDetail.setDuration(
                                (double) Math.round(DateTimeUtil.hoursDifference(durationStart, durationEnd) * 10)
                                        / 10);
                        timeSheetDetailRepository.save(timeSheetDetail);
                    }

                }
            }
        } else {
            return false;
        }
        return true;

    }

    // CODE FOR AUTOGENERATE TIMESHEET DETAIL
//    @Override
//    public List<TimeSheetDetailDto> autogenerateTimesheetDetailV2(UUID taskId) {
//        HrTask hrTask = hrTaskRepository.findById(taskId).orElse(null);
//        if (hrTask == null) return null;
//        boolean hasCreatedInSubTaskItem = false;
//
//        //list successfully created timesheet detail
//        List<TimeSheetDetailDto> successfullyCreatedTSD = new ArrayList<>();
//
//        //generate timesheet detail for subTaskItem first
//        if (hrTask.getSubTasks() != null && hrTask.getSubTasks().size() > 0) {
//            for (HrSubTask subTask : hrTask.getSubTasks()) {
//                //each subtask has many subtask items
//                if (subTask.getSubTaskItems() != null && subTask.getSubTaskItems().size() > 0) {
//                    //looping in each subtask item,
//                    for (HrSubTaskItem subTaskItem : subTask.getSubTaskItems()) {
//                        //checking and create timesheet detail for each subTaskItem
//                        List<TimeSheetDetailDto> tsd = this.autogenerateTimesheetDetailBySubTaskItem(subTaskItem.getId());
//                        if (tsd != null && tsd.size() > 0) {
//                            hasCreatedInSubTaskItem = true;
//                            successfullyCreatedTSD.addAll(tsd);
//                        }
//                    }
//                }
//            }
//        }
//
//        //if timesheet detail has NOT been created in subTaskItem,
//        // create timesheet detail by assignee, startTime, endTime of TASK
//        if (!hasCreatedInSubTaskItem && hrTask.getStaffs() != null && hrTask.getStaffs().size() > 0
//                && hrTask.getStartTime() != null && hrTask.getEndTime() != null) {
//            List<TimeSheetDetailDto> tsd = this.autogenerateTimesheetDetailByTask(hrTask.getId());
//            if (tsd != null && tsd.size() > 0) {
//                successfullyCreatedTSD.addAll(tsd);
//            }
//        }
//
//        return successfullyCreatedTSD;
//    }
//
//    @Override
//    public List<TimeSheetDetailDto> autogenerateTimesheetDetailBySubTaskItem(UUID subTaskItemId) {
//        HrSubTaskItem subTaskItem = hrSubTaskItemRepository.findById(subTaskItemId).orElse(null);
//        if (subTaskItem == null) return null;
//
//        //list successfully created timesheet detail
//        List<TimeSheetDetailDto> successfullyCreatedTSD = new ArrayList<>();
//
//        HrTask hrTask = hrTaskRepository.findById(subTaskItem.getSubTask().getTask().getId()).orElse(null);
//        if (hrTask == null) return null;
//        HrTaskDto hrTaskDto = new HrTaskDto(hrTask);
//
//        // if item has assignee + startTime + endTime, then generate timesheet detail for this assignee
//        if (subTaskItem.getStaffs() != null && subTaskItem.getStaffs().size() > 0
//                && subTaskItem.getStartTime() != null && subTaskItem.getEndTime() != null) {
//            Staff assignee = null;
//            for (HrSubTaskItemStaff itemStaff : subTaskItem.getStaffs()) {
//                assignee = itemStaff.getStaff();
//                break;
//            }
//
//            if (assignee == null) return null;
//
//            //all timesheet in range [startTime, endTime]
//            List<TimeSheetDto> timesheetInRange = timeSheetService
//                    .getTimeSheetByTime(assignee.getId(), subTaskItem.getStartTime(), subTaskItem.getEndTime());
//            if (timesheetInRange == null || timesheetInRange.size() == 0) return null;
//
//            for (TimeSheetDto timeSheetDto : timesheetInRange) {
//                //get all work shift of staff in this day (timesheet)
//                if (timeSheetDto.getTimeSheetShiftWorkPeriod() != null
//                        && timeSheetDto.getTimeSheetShiftWorkPeriod().size() > 0) {
//                    //subTaskItem can be in status DOING in multiple shifts, in many days
//                    for (TimeSheetShiftWorkPeriodDto timeSheetShiftWorkPeriodDto : timeSheetDto.getTimeSheetShiftWorkPeriod()) {
//                        //checking for all work shift of the day
//                        ShiftWorkTimePeriodDto shiftWork = timeSheetShiftWorkPeriodDto.getShiftWorkTimePeriod();
//                        Date shiftStartTime = DateTimeUtil.setTimeToDate(
//                                shiftWork.getStartTime(),
//                                timeSheetShiftWorkPeriodDto.getTimeSheet().getWorkingDate());
//                        Date shiftEndTime = DateTimeUtil.setTimeToDate(shiftWork.getEndTime(),
//                                timeSheetShiftWorkPeriodDto.getTimeSheet().getWorkingDate());
//
//                        //get range of start time and end time of timesheet detail
//                        Date tsdStartTime = new Date(Math.max(shiftStartTime.getTime(), subTaskItem.getStartTime().getTime()));
//                        Date tsdEndTime = new Date(Math.min(shiftEndTime.getTime(), subTaskItem.getEndTime().getTime()));
//
//                        //check whether this range time is valid in this shift work period
//                        if (shiftEndTime.before(tsdStartTime) || shiftStartTime.after(tsdEndTime)) {
//                            continue;
//                        }
//
//                        //reach this section, the start and end time of timesheet detail is valid,
//
//                        //check whether any timesheet detail of this staff has been created in this time before
//                        boolean hasExisted = false;
//                        if (timeSheetDto.getDetails() != null && timeSheetDto.getDetails().size() > 0) {
//                            for (TimeSheetDetailDto existedTSD : timeSheetDto.getDetails()) {
//                                if (existedTSD.getStartTime() != null && existedTSD.getEndTime() != null &&
//                                        existedTSD.getStartTime().getTime() == tsdStartTime.getTime() &&
//                                        existedTSD.getEndTime().getTime() == tsdEndTime.getTime() &&
//                                        existedTSD.getHrTask() != null &&
//                                        existedTSD.getHrTask().getId().equals(hrTask.getId())) {
//                                    //this timesheet detail has been created before, skip it
//                                    hasExisted = true;
//                                    break;
//                                }
//                            }
//                        }
//
//                        if (!hasExisted) {
//                            TimeSheetDetailDto newTimesheetDetail = new TimeSheetDetailDto();
//                            newTimesheetDetail.setProjectActivity(hrTaskDto.getActivity());
//                            newTimesheetDetail.setPriority(hrTaskDto.getPriority());
//                            newTimesheetDetail.setProject(hrTaskDto.getProject());
//                            newTimesheetDetail.setWorkingStatus(hrTaskDto.getStatus());
//                            newTimesheetDetail.setEmployee(new StaffDto());
//                            newTimesheetDetail.getEmployee().setId(assignee.getId());
//                            newTimesheetDetail.setTimeSheetShiftWorkPeriodDto(timeSheetShiftWorkPeriodDto);
//                            newTimesheetDetail.setHrTask(hrTaskDto);
//                            newTimesheetDetail.setTimeSheet(timeSheetDto);
//                            newTimesheetDetail.setStartTime(tsdStartTime);
//                            newTimesheetDetail.setEndTime(tsdEndTime);
//
//                            newTimesheetDetail.setDescription(hrTaskDto.getName()
//                                    + ": " + subTaskItem.getSubTask().getName() + ": " + subTaskItem.getName());
//
//                            TimeSheetDetailDto createdTSD = this
//                                    .saveTimeSheetDetail(newTimesheetDetail, null);
//                            successfullyCreatedTSD.add(createdTSD);
//                        }
//                    }
//                }
//            }
//        }
//
//        return successfullyCreatedTSD;
//    }
//
//    @Override
//    public List<TimeSheetDetailDto> autogenerateTimesheetDetailByTask(UUID taskId) {
//        HrTask hrTask = hrTaskRepository.findById(taskId).orElse(null);
//        if (hrTask == null) return null;
//        HrTaskDto hrTaskDto = new HrTaskDto(hrTask);
//
//        //list successfully created timesheet detail
//        List<TimeSheetDetailDto> successfullyCreatedTSD = new ArrayList<>();
//
//        // if item has assignee + startTime + endTime, then generate timesheet detail for this assignee
//        if (hrTask.getStaffs() != null && hrTask.getStaffs().size() > 0
//                && hrTask.getStartTime() != null && hrTask.getEndTime() != null) {
//            Staff assignee = null;
//            for (HrTaskStaff itemStaff : hrTask.getStaffs()) {
//                assignee = itemStaff.getStaff();
//                break;
//            }
//            if (assignee == null) return null;
//
//            //all timesheet in range [startTime, endTime]
//            List<TimeSheetDto> timesheetInRange = timeSheetService
//                    .getTimeSheetByTime(assignee.getId(), hrTask.getStartTime(), hrTask.getEndTime());
//            if (timesheetInRange == null || timesheetInRange.size() == 0) return null;
//
//            for (TimeSheetDto timeSheetDto : timesheetInRange) {
//                //get all work shift of staff in this day (timesheet)
//                if (timeSheetDto.getTimeSheetShiftWorkPeriod() != null
//                        && timeSheetDto.getTimeSheetShiftWorkPeriod().size() > 0) {
//                    //subTaskItem can be in status DOING in multiple shifts, in many days
//                    for (TimeSheetShiftWorkPeriodDto timeSheetShiftWorkPeriodDto : timeSheetDto.getTimeSheetShiftWorkPeriod()) {
//                        //checking for all work shift of the day
//                        ShiftWorkTimePeriodDto shiftWork = timeSheetShiftWorkPeriodDto.getShiftWorkTimePeriod();
//                        Date shiftStartTime = DateTimeUtil.setTimeToDate(
//                                shiftWork.getStartTime(),
//                                timeSheetShiftWorkPeriodDto.getTimeSheet().getWorkingDate());
//                        Date shiftEndTime = DateTimeUtil.setTimeToDate(shiftWork.getEndTime(),
//                                timeSheetShiftWorkPeriodDto.getTimeSheet().getWorkingDate());
//
//                        //get range of start time and end time of timesheet detail
//                        Date tsdStartTime = new Date(Math.max(shiftStartTime.getTime(), hrTask.getStartTime().getTime()));
//                        Date tsdEndTime = new Date(Math.min(shiftEndTime.getTime(), hrTask.getEndTime().getTime()));
//
//                        //check whether this range time is valid in this shift work period
//                        if (shiftEndTime.before(tsdStartTime) || shiftStartTime.after(tsdEndTime)) {
//                            continue;
//                        }
//
//                        //reach this section, the start and end time of timesheet detail is valid,
//
//                        //check whether any timesheet detail of this staff has been created in this time before
//                        boolean hasExisted = false;
//                        if (timeSheetDto.getDetails() != null && timeSheetDto.getDetails().size() > 0) {
//                            for (TimeSheetDetailDto existedTSD : timeSheetDto.getDetails()) {
//                                if (existedTSD.getStartTime() != null && existedTSD.getEndTime() != null &&
//                                        existedTSD.getStartTime().getTime() == tsdStartTime.getTime() &&
//                                        existedTSD.getEndTime().getTime() == tsdEndTime.getTime() &&
//                                        existedTSD.getHrTask() != null &&
//                                        existedTSD.getHrTask().getId().equals(hrTask.getId())) {
//                                    //this timesheet detail has been created before, skip it
//                                    hasExisted = true;
//                                    break;
//                                }
//                            }
//                        }
//
//                        if (!hasExisted) {
//                            TimeSheetDetailDto newTimesheetDetail = new TimeSheetDetailDto();
//                            newTimesheetDetail.setProjectActivity(hrTaskDto.getActivity());
//                            newTimesheetDetail.setPriority(hrTaskDto.getPriority());
//                            newTimesheetDetail.setProject(hrTaskDto.getProject());
//                            newTimesheetDetail.setWorkingStatus(hrTaskDto.getStatus());
//                            newTimesheetDetail.setEmployee(new StaffDto());
//                            newTimesheetDetail.getEmployee().setId(assignee.getId());
//                            newTimesheetDetail.setTimeSheetShiftWorkPeriodDto(timeSheetShiftWorkPeriodDto);
//                            newTimesheetDetail.setHrTask(hrTaskDto);
//                            newTimesheetDetail.setTimeSheet(timeSheetDto);
//                            newTimesheetDetail.setStartTime(tsdStartTime);
//                            newTimesheetDetail.setEndTime(tsdEndTime);
//                            newTimesheetDetail.setDescription(hrTaskDto.getName());
//
//                            TimeSheetDetailDto createdTSD = this
//                                    .saveTimeSheetDetail(newTimesheetDetail, null);
//                            successfullyCreatedTSD.add(createdTSD);
//                        }
//                    }
//                }
//            }
//        }
//
//        return successfullyCreatedTSD;
//    }

    // handle for auto generate timesheet detail button
    // 1 timesheet/working shift can have many timesheet details
//    @Override
//    public List<TimeSheetDetailDto> autoGenerateTimeSheetDetailInRangeTime(SearchTimeSheetDto searchTimeSheetDto) {
//        if (searchTimeSheetDto == null || searchTimeSheetDto.getFromDate() == null
//                || searchTimeSheetDto.getToDate() == null) return null;
//
//        //handle for case users creating their own timesheet
//        Staff needGenerateStaff = userExtService.getCurrentStaffEntity();
//        //handle for case admin/manager create timesheet for their staffs
//        if (searchTimeSheetDto.getStaffId() != null)
//            needGenerateStaff = staffRepository.findById(searchTimeSheetDto.getStaffId()).orElse(null);
//
//        if (needGenerateStaff == null) return null;
//
//        //list timeSheetDetail successfully created in this function
//        List<TimeSheetDetailDto> listGenerated = new ArrayList<>();
//
//        //first get all timesheet of need creating timesheet detail user
//        List<TimeSheetDto> availableTimeSheets =
//                timeSheetService.getTimeSheetByTime(needGenerateStaff.getId(),
//                        searchTimeSheetDto.getFromDate(), searchTimeSheetDto.getToDate());
//
//        //get all task has history which own createDate in range [fromDate, toDate]
//        Date fromDateBegin = DateTimeUtil.getStartOfDay(searchTimeSheetDto.getFromDate());
//        Date toDateEnd = DateTimeUtil.getEndOfDay(searchTimeSheetDto.getToDate());
//        searchTimeSheetDto.setFromDate(fromDateBegin);
//        searchTimeSheetDto.setToDate(toDateEnd);
//
//        searchTimeSheetDto.setStaffId(needGenerateStaff.getId());
//        List<KanbanDto> willGenerateTasks = hrTaskService.getAllTaskCreatedUpdatedInRangeFromToDate(searchTimeSheetDto);
//        if (willGenerateTasks != null && willGenerateTasks.size() > 0) {
//            for (KanbanDto task : willGenerateTasks) {
//                List<TimeSheetDetailDto> createdTimesheetDetail = this.autogenerateTimesheetDetailV2(task.getId());
//                if (createdTimesheetDetail != null && createdTimesheetDetail.size() > 0) {
//                    listGenerated.addAll(createdTimesheetDetail);
//                }
//            }
//        }
//
//        return listGenerated;
//    }

    // 1 shift working only have 1 timesheet detail,
    // this detail has description field containing all tasks that user do in that
    // shift working
    @Override
    public List<TimeSheetDetailDto> autoGenerateTimeSheetDetailInRangeTimeV2(SearchTimeSheetDto searchTimeSheetDto) {
        if (searchTimeSheetDto == null || searchTimeSheetDto.getFromDate() == null
                || searchTimeSheetDto.getToDate() == null)
            return null;

        // handle for case users creating their own timesheet
        Staff needGenerateStaff = userExtService.getCurrentStaffEntity();
        // handle for case admin/manager create timesheet for their staffs
        if (searchTimeSheetDto.getStaffId() != null)
            needGenerateStaff = staffRepository.findById(searchTimeSheetDto.getStaffId()).orElse(null);

        if (needGenerateStaff == null)
            return null;

        // list timeSheetDetail successfully created in this function
        List<TimeSheetDetailDto> listGenerated = new ArrayList<>();
        TimesheetDetailDescriptor detailDescriptor = new TimesheetDetailDescriptor();

        // get all task has history which own createDate in range [fromDate, toDate]
        Date fromDateBegin = DateTimeUtil.getStartOfDay(searchTimeSheetDto.getFromDate());
        Date toDateEnd = DateTimeUtil.getEndOfDay(searchTimeSheetDto.getToDate());
        searchTimeSheetDto.setFromDate(fromDateBegin);
        searchTimeSheetDto.setToDate(toDateEnd);

        // TODO: delete all old timesheet details which have existed yet first
        timeSheetDetailRepository.deleteTimeSheetDetailOfStaffInRangeTime(needGenerateStaff.getId(), fromDateBegin,
                toDateEnd);

        searchTimeSheetDto.setStaffId(needGenerateStaff.getId());
        List<KanbanDto> willGenerateTasks = hrTaskService.getAllTaskCreatedUpdatedInRangeFromToDate(searchTimeSheetDto);
        if (willGenerateTasks != null && willGenerateTasks.size() > 0) {
            for (KanbanDto task : willGenerateTasks) {
                this.autogenerateTimesheetDetailV2(task.getId(), detailDescriptor, searchTimeSheetDto);
            }
        }

        // NEW TODO: ONLY NEED A SINGLE TASK HISTORY RECORD TO MARK THAT TASK IS AN ITEM
        // OF TIMESHEET DETAIL
        // name of the task will be filled into detailDescriptor
        autoGenerateTimesheetDetailByOnlyUpdateHistory(detailDescriptor, searchTimeSheetDto);

        // THIS SECTION CODE WILL FINALLY GENERATE TIMESHEET DETAIL
        // set content of description field in timesheet detail, content is all
        // task/subtaskItem that users have done in specific time
        for (TimeSheetDetail needCreateUpdateTSD : detailDescriptor.getNeedCreateUpdateTimesheetDetails()) {
            if (needCreateUpdateTSD == null || needCreateUpdateTSD.getId() == null)
                continue;
            needCreateUpdateTSD.setDescription(detailDescriptor.getBuiltContent(needCreateUpdateTSD));
            TimeSheetDetail savedTSD = timeSheetDetailRepository.save(needCreateUpdateTSD);

            // add to return result is list generated timesheet detail
            listGenerated.add(new TimeSheetDetailDto(savedTSD));
        }

        // NEW TODO: create timeSheet detail for shiftWork periods which do not have any
        // timeSheet detail => timesheet detail's content is EMPTY
        List<TimeSheetDetailDto> emptyTSDGenerated = generateTSDForShiftWorkDoNotHaveTSDBefore(
                needGenerateStaff.getId(), fromDateBegin, toDateEnd);
        if (emptyTSDGenerated != null && emptyTSDGenerated.size() > 0)
            listGenerated.addAll(emptyTSDGenerated);

        return listGenerated;
    }

    // this function will get content of tasks which are modified in range time to
    // add to content of timesheet detail
    private void autoGenerateTimesheetDetailByOnlyUpdateHistory(TimesheetDetailDescriptor detailDescriptor,
                                                                SearchTimeSheetDto searchDto) {
        if (detailDescriptor == null || searchDto == null || searchDto.getStaffId() == null
                || searchDto.getFromDate() == null || searchDto.getToDate() == null) {
            return;
        }

        // get all task has history which own createDate in range [fromDate, toDate]
        Date fromDateBegin = DateTimeUtil.getStartOfDay(searchDto.getFromDate());
        Date toDateEnd = DateTimeUtil.getEndOfDay(searchDto.getToDate());
        searchDto.setFromDate(fromDateBegin);
        searchDto.setToDate(toDateEnd);

        List<HrTaskHistory> historyOfStaff = taskHistoryService.findHistoryOfStaffInRangeTime(searchDto.getStaffId(),
                searchDto.getFromDate(), searchDto.getToDate());

        // in this case, query is empty or there's no activity of this user in input
        // rangeTime => return;
        if (historyOfStaff == null || historyOfStaff.size() == 0)
            return;

        // time sheet detail will receive this history as an item of its content
        for (HrTaskHistory history : historyOfStaff) {
            // convert local date time to date type
            Date createdHistoryTime = CoreDateTimeUtil.convertToDateViaInstant(history.getCreateDate());

            // at first, add history as content of timesheet detail for existed timesheet
            // detail,
            // if that received timesheet detail hasn't been existed yet, create NEW in step
            // SECOND
            List<TimeSheetDetail> onReceiveTSDs = timeSheetDetailRepository
                    .findTSDReceiveHistoryAsContent(searchDto.getStaffId(), createdHistoryTime);

            boolean hasCreated = false;
            if (onReceiveTSDs != null && onReceiveTSDs.size() > 0) {
                for (TimeSheetDetail receiveTSD : onReceiveTSDs) {
                    detailDescriptor.addDetailContent(receiveTSD, getDetailContentOfTask(history.getTask()));
                    hasCreated = true;
                }
            }

            if (hasCreated)
                continue;

            // SECOND, in this section, there's no tsd has been created for logging this
            // history event => CREATE NEW
            // find timeSheetShiftWorkPeriod for new TimeSheetDetail
            TimeSheetShiftWorkPeriod onReceiveShiftPeriod = timeSheetShiftWorkPeriodService
                    .findTSShiftWorkPeriodWrapTimeOfStaff(createdHistoryTime, searchDto.getStaffId());

            // if shiftPeriod is null => USER HAS NOT TAKEN ATTENDANCE IN THIS SHIFT WORK =>
            // skip
            if (onReceiveShiftPeriod == null)
                continue;
            // else, just create new timesheet detail and fill all necessary field
            TimeSheetShiftWorkPeriodDto onReceiveShiftPeriodDto = new TimeSheetShiftWorkPeriodDto(onReceiveShiftPeriod);
            ShiftWorkTimePeriodDto shiftWork = onReceiveShiftPeriodDto.getShiftWorkTimePeriod();
            Date shiftStartTime = DateTimeUtil.setTimeToDate(shiftWork.getStartTime(),
                    onReceiveShiftPeriodDto.getTimeSheet().getWorkingDate());
            Date shiftEndTime = DateTimeUtil.setTimeToDate(shiftWork.getEndTime(),
                    onReceiveShiftPeriodDto.getTimeSheet().getWorkingDate());

            TimeSheetDetailDto newTimesheetDetail = new TimeSheetDetailDto();
//            newTimesheetDetail.setProjectActivity(hrTaskDto.getActivity());
//            newTimesheetDetail.setPriority(hrTaskDto.getPriority());
//            newTimesheetDetail.setProject(hrTaskDto.getProject());
//            newTimesheetDetail.setWorkingStatus(hrTaskDto.getStatus());
            newTimesheetDetail.setEmployee(new StaffDto());
            newTimesheetDetail.getEmployee().setId(searchDto.getStaffId());
            newTimesheetDetail.setTimeSheetShiftWorkPeriodDto(onReceiveShiftPeriodDto);
//            newTimesheetDetail.setHrTask(hrTaskDto);
            newTimesheetDetail.setTimeSheet(onReceiveShiftPeriodDto.getTimeSheet());
            newTimesheetDetail.setStartTime(shiftStartTime);
            newTimesheetDetail.setEndTime(shiftEndTime);
            newTimesheetDetail.setApproveStatus(1);
//            newTimesheetDetail.setDescription("on updating timesheet detail content...");

            TimeSheetDetailDto createdTSD = this.saveTimeSheetDetail(newTimesheetDetail, null);
            TimeSheetDetail receiveTSD = timeSheetDetailRepository.findById(createdTSD.getId()).orElse(null);

            // add content to receive timesheet detail
            detailDescriptor.addDetailContent(receiveTSD, getDetailContentOfTask(history.getTask()));
        }

    }

    private void autogenerateTimesheetDetailV2(UUID taskId, TimesheetDetailDescriptor detailDescriptor,
                                               SearchTimeSheetDto searchTimeSheetDto) {
        HrTask hrTask = hrTaskRepository.findById(taskId).orElse(null);
        if (hrTask == null)
            return;
        boolean hasCreatedInSubTaskItem = false;

        // generate timesheet detail for subTaskItem first
        if (hrTask.getSubTasks() != null && hrTask.getSubTasks().size() > 0) {
            for (HrSubTask subTask : hrTask.getSubTasks()) {
                // each subtask has many subtask items
                if (subTask.getSubTaskItems() != null && subTask.getSubTaskItems().size() > 0) {
                    // looping in each subtask item,
                    for (HrSubTaskItem subTaskItem : subTask.getSubTaskItems()) {
                        // checking and create timesheet detail for each subTaskItem
                        hasCreatedInSubTaskItem = this.autogenerateTimesheetDetailBySubTaskItemV2(subTaskItem.getId(),
                                detailDescriptor, searchTimeSheetDto);
                    }
                }
            }
        }

        // if timesheet detail has NOT been created in subTaskItem,
        // create timesheet detail by assignee, startTime, endTime of TASK
        if (!hasCreatedInSubTaskItem && hrTask.getStaffs() != null && hrTask.getStaffs().size() > 0
                && hrTask.getStartTime() != null && hrTask.getEndTime() != null) {
            this.autogenerateTimesheetDetailByTaskV2(hrTask.getId(), detailDescriptor, searchTimeSheetDto);
        }
    }

    private boolean autogenerateTimesheetDetailBySubTaskItemV2(UUID subTaskItemId,
                                                               TimesheetDetailDescriptor detailDescriptor, SearchTimeSheetDto searchTimeSheetDto) {
        Staff currentStaff = userExtService.getCurrentStaffEntity();
        if (currentStaff == null)
            return false;

        HrSubTaskItem subTaskItem = hrSubTaskItemRepository.findById(subTaskItemId).orElse(null);
        if (subTaskItem == null)
            return false;

        HrTask hrTask = hrTaskRepository.findById(subTaskItem.getSubTask().getTask().getId()).orElse(null);
        if (hrTask == null)
            return false;
        boolean hasCreateOrUpdated = false;

        // if item has assignee + startTime + endTime, then generate timesheet detail
        // for this assignee
        if (subTaskItem.getStaffs() != null && subTaskItem.getStaffs().size() > 0 && subTaskItem.getStartTime() != null
                && subTaskItem.getEndTime() != null) {
            Staff assignee = null;
            for (HrSubTaskItemStaff itemStaff : subTaskItem.getStaffs()) {
                assignee = itemStaff.getStaff();
                break;
            }

            if (assignee == null || !assignee.getId().equals(currentStaff.getId()))
                return false;

            // all timesheet in range [startTime, endTime]
            List<TimeSheetDto> timesheetInRange = timeSheetService.getTimeSheetByTime(assignee.getId(),
                    subTaskItem.getStartTime(), subTaskItem.getEndTime());

            if (timesheetInRange == null || timesheetInRange.size() == 0)
                return false;

            for (TimeSheetDto timeSheetDto : timesheetInRange) {
                // TODO: DO NOT CREATE TIMESHEETDETAILS WHICH ARE NOT IN RANGE FROMDATE TO
                // TODATE IN INPUT
                if (!(timeSheetDto.getWorkingDate().getTime() <= searchTimeSheetDto.getToDate().getTime()
                        && timeSheetDto.getWorkingDate().getTime() >= searchTimeSheetDto.getFromDate().getTime()))
                    continue;

                // get all work shift of staff in this day (timesheet)
                if (timeSheetDto.getTimeSheetShiftWorkPeriod() != null
                        && timeSheetDto.getTimeSheetShiftWorkPeriod().size() > 0) {
                    // subTaskItem can be in status DOING in multiple shifts, in many days
                    for (TimeSheetShiftWorkPeriodDto timeSheetShiftWorkPeriodDto : timeSheetDto
                            .getTimeSheetShiftWorkPeriod()) {
                        // checking for work shift of the day
                        ShiftWorkTimePeriodDto shiftWork = timeSheetShiftWorkPeriodDto.getShiftWorkTimePeriod();
                        Date shiftStartTime = DateTimeUtil.setTimeToDate(shiftWork.getStartTime(),
                                timeSheetShiftWorkPeriodDto.getTimeSheet().getWorkingDate());
                        Date shiftEndTime = DateTimeUtil.setTimeToDate(shiftWork.getEndTime(),
                                timeSheetShiftWorkPeriodDto.getTimeSheet().getWorkingDate());

                        // get range of start time and end time of timesheet detail
                        Date tsdStartTime = new Date(
                                Math.max(shiftStartTime.getTime(), subTaskItem.getStartTime().getTime()));
                        Date tsdEndTime = new Date(
                                Math.min(shiftEndTime.getTime(), subTaskItem.getEndTime().getTime()));

                        // check whether this range time is valid in this shift work period
                        if (shiftEndTime.before(tsdStartTime) || shiftStartTime.after(tsdEndTime)) {
                            continue;
                        }

                        // reach this section, the start and end time of timesheet detail is valid,

                        // check whether any timesheet detail of this staff has been created in this
                        // time before
                        TimeSheetDetail onCreateTSD = null;
                        hasCreateOrUpdated = true;

                        // first, check whether the timesheet detail is created in detail descriptor or
                        // not yet
                        // if it was existed => this is onCreateTSD
                        Set<TimeSheetDetail> choosenToGenerateTSD = detailDescriptor
                                .getNeedCreateUpdateTimesheetDetails();
                        for (TimeSheetDetail detail : choosenToGenerateTSD) {
                            if (detail.getTimeSheet().getId().equals(timeSheetDto.getId())
                                    && shiftStartTime.equals(detail.getStartTime())
                                    && shiftEndTime.equals(detail.getEndTime())
                                    && detail.getEmployee().getId().equals(assignee.getId())) {
                                onCreateTSD = detail;
                            }
                        }

                        if (onCreateTSD == null && timeSheetDto.getDetails() != null
                                && timeSheetDto.getDetails().size() > 0) {
                            for (TimeSheetDetailDto existedTSD : timeSheetDto.getDetails()) {
                                if (existedTSD.getStartTime() != null && existedTSD.getEndTime() != null
                                        && existedTSD.getStartTime().getTime() == shiftStartTime.getTime()
                                        && existedTSD.getEndTime().getTime() == shiftEndTime.getTime()) {
                                    // this timesheet detail has been created before, skip it
                                    onCreateTSD = timeSheetDetailRepository.findById(existedTSD.getId()).orElse(null);
                                    break;
                                }
                            }
                        }

                        // timesheet detail is not existed yet, then create new
                        if (onCreateTSD == null) {
                            TimeSheetDetailDto newTimesheetDetail = new TimeSheetDetailDto();
//                            newTimesheetDetail.setProjectActivity(hrTaskDto.getActivity());
//                            newTimesheetDetail.setPriority(hrTaskDto.getPriority());
//                            newTimesheetDetail.setProject(hrTaskDto.getProject());
//                            newTimesheetDetail.setWorkingStatus(hrTaskDto.getStatus());
                            newTimesheetDetail.setEmployee(new StaffDto());
                            newTimesheetDetail.getEmployee().setId(assignee.getId());
                            newTimesheetDetail.setTimeSheetShiftWorkPeriodDto(timeSheetShiftWorkPeriodDto);
//                            newTimesheetDetail.setHrTask(hrTaskDto);
                            newTimesheetDetail.setTimeSheet(timeSheetDto);
                            newTimesheetDetail.setStartTime(shiftStartTime);
                            newTimesheetDetail.setEndTime(shiftEndTime);
                            newTimesheetDetail.setApproveStatus(1);
//                            newTimesheetDetail.setDescription("on updating timesheet detail content...");

                            TimeSheetDetailDto createdTSD = this.saveTimeSheetDetail(newTimesheetDetail, null);
                            onCreateTSD = timeSheetDetailRepository.findById(createdTSD.getId()).orElse(null);
                        }

                        detailDescriptor.addDetailContent(onCreateTSD,
                                getDetailContentOfSubTaskItem(subTaskItem, hrTask));
                    }
                }
            }
        }

        return hasCreateOrUpdated;
    }

    private void autogenerateTimesheetDetailByTaskV2(UUID taskId, TimesheetDetailDescriptor detailDescriptor,
                                                     SearchTimeSheetDto searchTimeSheetDto) {
        Staff currentStaff = userExtService.getCurrentStaffEntity();
        if (currentStaff == null)
            return;

        HrTask hrTask = hrTaskRepository.findById(taskId).orElse(null);
        if (hrTask == null)
            return;
        HrTaskDto hrTaskDto = new HrTaskDto(hrTask);

        // if item has assignee + startTime + endTime, then generate timesheet detail
        // for this assignee
        if (hrTask.getStaffs() != null && hrTask.getStaffs().size() > 0 && hrTask.getStartTime() != null
                && hrTask.getEndTime() != null) {
            Staff assignee = null;
            for (HrTaskStaff itemStaff : hrTask.getStaffs()) {
                assignee = itemStaff.getStaff();
                break;
            }
            if (assignee == null || !assignee.getId().equals(currentStaff.getId()))
                return;

            // all timesheet in range [startTime, endTime]
            List<TimeSheetDto> timesheetInRange = timeSheetService.getTimeSheetByTime(assignee.getId(),
                    hrTask.getStartTime(), hrTask.getEndTime());
            if (timesheetInRange == null || timesheetInRange.size() == 0)
                return;

            for (TimeSheetDto timeSheetDto : timesheetInRange) {
                // TODO: DO NOT CREATE TIMESHEETDETAILS WHICH ARE NOT IN RANGE FROMDATE TO
                // TODATE IN INPUT
                if (!(timeSheetDto.getWorkingDate().getTime() <= searchTimeSheetDto.getToDate().getTime()
                        && timeSheetDto.getWorkingDate().getTime() >= searchTimeSheetDto.getFromDate().getTime()))
                    continue;

                // get all work shift of staff in this day (timesheet)
                if (timeSheetDto.getTimeSheetShiftWorkPeriod() != null
                        && timeSheetDto.getTimeSheetShiftWorkPeriod().size() > 0) {
                    // subTaskItem can be in status DOING in multiple shifts, in many days
                    for (TimeSheetShiftWorkPeriodDto timeSheetShiftWorkPeriodDto : timeSheetDto
                            .getTimeSheetShiftWorkPeriod()) {
                        // checking for all work shift of the day
                        ShiftWorkTimePeriodDto shiftWork = timeSheetShiftWorkPeriodDto.getShiftWorkTimePeriod();
                        Date shiftStartTime = DateTimeUtil.setTimeToDate(shiftWork.getStartTime(),
                                timeSheetShiftWorkPeriodDto.getTimeSheet().getWorkingDate());
                        Date shiftEndTime = DateTimeUtil.setTimeToDate(shiftWork.getEndTime(),
                                timeSheetShiftWorkPeriodDto.getTimeSheet().getWorkingDate());

                        // get range of start time and end time of timesheet detail
                        Date tsdStartTime = new Date(
                                Math.max(shiftStartTime.getTime(), hrTask.getStartTime().getTime()));
                        Date tsdEndTime = new Date(Math.min(shiftEndTime.getTime(), hrTask.getEndTime().getTime()));

                        // check whether this range time is valid in this shift work period
                        if (shiftEndTime.before(tsdStartTime) || shiftStartTime.after(tsdEndTime)) {
                            continue;
                        }

                        // reach this section, the start and end time of timesheet detail is valid,
                        TimeSheetDetail onCreateTSD = null;

                        // first, check whether the timesheet detail is created in detail descriptor or
                        // not yet
                        // if it was existed => this is onCreateTSD
                        Set<TimeSheetDetail> choosenToGenerateTSD = detailDescriptor
                                .getNeedCreateUpdateTimesheetDetails();
                        for (TimeSheetDetail detail : choosenToGenerateTSD) {
                            if (detail.getTimeSheet().getId().equals(timeSheetDto.getId())
                                    && shiftStartTime.equals(detail.getStartTime())
                                    && shiftEndTime.equals(detail.getEndTime())
                                    && detail.getEmployee().getId().equals(assignee.getId())) {
                                onCreateTSD = detail;
                            }
                        }

                        // check whether any timesheet detail of this staff has been created in this
                        // time before
                        if (onCreateTSD == null && timeSheetDto.getDetails() != null
                                && timeSheetDto.getDetails().size() > 0) {
                            for (TimeSheetDetailDto existedTSD : timeSheetDto.getDetails()) {
                                if (existedTSD.getStartTime() != null && existedTSD.getEndTime() != null
                                        && existedTSD.getStartTime().getTime() == shiftStartTime.getTime()
                                        && existedTSD.getEndTime().getTime() == shiftEndTime.getTime()) {
                                    // this timesheet detail has been created before, skip it
                                    onCreateTSD = timeSheetDetailRepository.findById(existedTSD.getId()).orElse(null);
                                    break;
                                }
                            }
                        }

                        if (onCreateTSD == null) {
                            TimeSheetDetailDto newTimesheetDetail = new TimeSheetDetailDto();
//                            newTimesheetDetail.setProjectActivity(hrTaskDto.getActivity());
//                            newTimesheetDetail.setPriority(hrTaskDto.getPriority());
//                            newTimesheetDetail.setProject(hrTaskDto.getProject());
//                            newTimesheetDetail.setWorkingStatus(hrTaskDto.getStatus());
                            newTimesheetDetail.setEmployee(new StaffDto());
                            newTimesheetDetail.getEmployee().setId(assignee.getId());
                            newTimesheetDetail.setTimeSheetShiftWorkPeriodDto(timeSheetShiftWorkPeriodDto);
//                            newTimesheetDetail.setHrTask(hrTaskDto);
                            newTimesheetDetail.setTimeSheet(timeSheetDto);
                            newTimesheetDetail.setStartTime(shiftStartTime);
                            newTimesheetDetail.setEndTime(shiftEndTime);
                            newTimesheetDetail.setApproveStatus(1);
//                            newTimesheetDetail.setDescription(hrTaskDto.getName());

                            TimeSheetDetailDto createdTSD = this.saveTimeSheetDetail(newTimesheetDetail, null);

                            onCreateTSD = timeSheetDetailRepository.findById(createdTSD.getId()).orElse(null);
                        }

                        detailDescriptor.addDetailContent(onCreateTSD, getDetailContentOfTask(hrTask));
                    }
                }
            }
        }
    }

    private String getDetailContentOfSubTaskItem(HrSubTaskItem subTaskItem, HrTask currentTask) {
        String moreDetailContent = "";
        if (currentTask.getProject() != null) {
            moreDetailContent += " (" + currentTask.getProject().getCode() + "#" + currentTask.getCode() + ": "
                    + currentTask.getName() + " - " + subTaskItem.getSubTask().getName() + ")";
        }

        return subTaskItem.getName() + moreDetailContent;
    }

    private String getDetailContentOfTask(HrTask currentTask) {
        String moreDetailContent = "";
        if (currentTask.getProject() != null) {
            moreDetailContent += " (" + currentTask.getProject().getCode() + "#" + currentTask.getCode() + ")";
        }

        return currentTask.getName() + moreDetailContent;
    }

    private List<TimeSheetDetailDto> generateTSDForShiftWorkDoNotHaveTSDBefore(UUID staffId, Date fromDate,
                                                                               Date toDate) {
        List<TimeSheetDetailDto> listGenerated = new ArrayList<>();
        List<TimeSheetShiftWorkPeriodDto> ignoredShiftWorks = timeSheetShiftWorkPeriodService
                .getListTSShiftWorkPeriodsDoNotHaveAnyTimeSheetDetail(staffId, fromDate, toDate);

        if (ignoredShiftWorks == null || ignoredShiftWorks.isEmpty())
            return listGenerated;

        for (TimeSheetShiftWorkPeriodDto timeSheetShiftWorkPeriodDto : ignoredShiftWorks) {
            // checking for work shift of the day
            ShiftWorkTimePeriodDto shiftWork = timeSheetShiftWorkPeriodDto.getShiftWorkTimePeriod();
            Date shiftStartTime = DateTimeUtil.setTimeToDate(shiftWork.getStartTime(),
                    timeSheetShiftWorkPeriodDto.getTimeSheet().getWorkingDate());
            Date shiftEndTime = DateTimeUtil.setTimeToDate(shiftWork.getEndTime(),
                    timeSheetShiftWorkPeriodDto.getTimeSheet().getWorkingDate());

            // get list existed timesheet detail in shift work
            List<TimeSheetDetailDto> existedTSDInShiftWorkPeriod = timeSheetDetailRepository
                    .findExistedTSDInShiftWorkPeriod(staffId, timeSheetShiftWorkPeriodDto.getId());
            if (existedTSDInShiftWorkPeriod != null && existedTSDInShiftWorkPeriod.size() > 0) {
                // if current timesheet work period has timesheet detail => skip it
                // BECAUSE ONLY CREATE EMPTY TSD FOR SHIFT WORK PERIOD
                continue;
            }

            // timesheet detail is not existed yet, then create new
            TimeSheetDetailDto newTimesheetDetail = new TimeSheetDetailDto();
//                            newTimesheetDetail.setProjectActivity(hrTaskDto.getActivity());
//                            newTimesheetDetail.setPriority(hrTaskDto.getPriority());
//                            newTimesheetDetail.setProject(hrTaskDto.getProject());
//                            newTimesheetDetail.setWorkingStatus(hrTaskDto.getStatus());
            newTimesheetDetail.setEmployee(new StaffDto());
            newTimesheetDetail.getEmployee().setId(staffId);
            newTimesheetDetail.setTimeSheetShiftWorkPeriodDto(timeSheetShiftWorkPeriodDto);
//                            newTimesheetDetail.setHrTask(hrTaskDto);
            newTimesheetDetail.setTimeSheet(timeSheetShiftWorkPeriodDto.getTimeSheet());
            newTimesheetDetail.setStartTime(shiftStartTime);
            newTimesheetDetail.setEndTime(shiftEndTime);
            newTimesheetDetail.setApproveStatus(1);
            newTimesheetDetail.setDescription("<p>Không có hoạt động được ghi nhận</p>");

            TimeSheetDetailDto createdTSD = this.saveTimeSheetDetail(newTimesheetDetail, null);

            // add to result
            listGenerated.add(createdTSD);
        }

        return listGenerated;
    }

    // Kiểm tra xem lần chấm công mới có trùng lặp với các lần chấm công hiện có của
    // ca làm việc hay không
    @Override
    public boolean isValidNewTimesheetDetail(TimeSheetDetailDto dto) {
        if (dto == null || dto.getStaffWorkSchedule() == null || dto.getStaffWorkSchedule().getId() == null
                || dto.getStartTime() == null || dto.getEndTime() == null) {
            return false; // Không đủ thông tin
        }

        StaffWorkSchedule schedule = staffWorkScheduleRepository.findById(dto.getStaffWorkSchedule().getId())
                .orElse(null);

        if (schedule == null) {
            return false;
        }

        if (schedule.getAllowOneEntryOnly() != null && schedule.getAllowOneEntryOnly().equals(true)
                && schedule.getTimesheetDetails() != null && !schedule.getTimesheetDetails().isEmpty()) {
            return false;
        }

        Date newStart = dto.getStartTime();
        Date newEnd = dto.getEndTime();

        for (TimeSheetDetail existingDetail : schedule.getTimesheetDetails()) {
            Date existingStart = existingDetail.getStartTime();
            Date existingEnd = existingDetail.getEndTime();

            // Nếu timesheet cũ thiếu thời gian => không thể xác định => không hợp lệ
            if (existingStart == null || existingEnd == null) {
                continue;
            }

            if ((existingStart.before(newStart) && existingEnd.after(newStart))
                    || (existingStart.before(newEnd) && existingEnd.after(newEnd)))
                return false;
        }

        return true;
    }


    @Override
    public TimeSheetDetailDto saveOrUpdate(TimeSheetDetailDto dto, HttpServletRequest request) {
        if (dto == null) return null;

        TimeSheetDetail entity = null;

        // 1. Tìm entity theo ID hoặc theo điều kiện sync
        if (dto.getId() != null) {
            entity = timeSheetDetailRepository.findById(dto.getId()).orElse(null);
        } else if (dto.getIsSync() != null && Boolean.TRUE.equals(dto.getIsSync()) && dto.getEmployee() != null) {
            List<TimeSheetDetail> list = timeSheetDetailRepository.findTimeSheetDetailsStaffBySync(
                    dto.getEmployee().getId(), dto.getStartTime());
            if (list != null && !list.isEmpty()) {
                entity = list.get(0);
            }
        }

        // 2. Nếu chưa có entity, tìm theo staffWorkSchedule
        if (entity == null && dto.getStaffWorkSchedule() != null && dto.getStaffWorkSchedule().getId() != null) {
            StaffWorkSchedule staffWorkSchedule = staffWorkScheduleRepository.findById(dto.getStaffWorkSchedule().getId()).orElse(null);
            if (staffWorkSchedule != null && Boolean.TRUE.equals(staffWorkSchedule.getAllowOneEntryOnly())
                    && staffWorkSchedule.getTimesheetDetails() != null && !staffWorkSchedule.getTimesheetDetails().isEmpty()) {
                entity = staffWorkSchedule.getTimesheetDetails().iterator().next();
            }
        }

        if (entity == null) {
            entity = new TimeSheetDetail();
            entity.setCreateDate(LocalDateTime.now());
            String username = (request != null && request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "system";
            entity.setCreatedBy(username);
        }

        // 3. Gán nhân viên
        if (dto.getEmployee() != null && dto.getEmployee().getId() != null) {
            Staff staff = staffRepository.findById(dto.getEmployee().getId()).orElse(null);
            entity.setEmployee(staff);
        } else if (dto.getStaffCode() != null) {
            List<Staff> staffs = staffRepository.findByCode(dto.getStaffCode());
            if (staffs != null && !staffs.isEmpty()) {
                entity.setEmployee(staffs.get(0));
            }
        } else {
            entity.setEmployee(null);
        }

        // 4. Gán staffWorkSchedule
        StaffWorkSchedule staffWorkSchedule = null;
        if (dto.getStaffWorkSchedule() != null) {
            if (dto.getStaffWorkSchedule().getId() != null) {
                staffWorkSchedule = staffWorkScheduleRepository.findById(dto.getStaffWorkSchedule().getId()).orElse(null);
            }
            if (staffWorkSchedule == null && dto.getStaffWorkSchedule().getShiftWork() != null) {
                staffWorkSchedule = staffWorkScheduleService.generateScheduleFromTimesheetDetailDto(dto, entity);
            }
        }
        entity.setStaffWorkSchedule(staffWorkSchedule);

        // Nếu lịch làm việc bị khóa thì không cho lưu
        if (staffWorkSchedule != null && Boolean.TRUE.equals(staffWorkSchedule.getIsLocked())) {
            return null;
        }

        // 5. Gán TimeSheet
        if (dto.getTimeSheet() != null && dto.getTimeSheet().getId() != null) {
            TimeSheet timeSheet = timeSheetRepository.findById(dto.getTimeSheet().getId()).orElse(null);
            entity.setTimeSheet(timeSheet);
        } else {
            entity.setTimeSheet(null);
        }

        // 6. Gán ShiftWorkTimePeriod
        if (dto.getShiftWorkTimePeriod() != null && dto.getShiftWorkTimePeriod().getId() != null) {
            ShiftWorkTimePeriod shiftWorkTimePeriod = shiftWorkTimePeriodRepository.findById(dto.getShiftWorkTimePeriod().getId()).orElse(null);
            entity.setShiftWorkTimePeriod(shiftWorkTimePeriod);
        } else if (dto.getShiftWorkTimePeriodCode() != null) {
            ShiftWorkTimePeriod shiftWorkTimePeriod = shiftWorkTimePeriodRepository.findByCode(dto.getShiftWorkTimePeriodCode());
            entity.setShiftWorkTimePeriod(shiftWorkTimePeriod);
        }

        // 7. Cập nhật startTime và endTime + IP nếu chỉnh sửa
        if (request == null) {
            if (entity.getStartTime() != null && dto.getStartTime() != null
                    && !entity.getStartTime().equals(dto.getStartTime())) {
                dto.setAddressIPCheckIn(null);
            }
            if (entity.getEndTime() != null && dto.getEndTime() != null
                    && !entity.getEndTime().equals(dto.getEndTime())) {
                dto.setAddressIPCheckOut(null);
            }
        }
        entity.setStartTime(dto.getStartTime());
        entity.setAddressIPCheckIn(dto.getAddressIPCheckIn());
        entity.setEndTime(dto.getEndTime());
        entity.setAddressIPCheckOut(dto.getAddressIPCheckOut());
        if (dto.getIsSync() != null) {
            entity.setIsSync(dto.getIsSync());
        }
        if (dto.getTimekeepingCode() != null) {
            entity.setTimekeepingCode(dto.getTimekeepingCode());
        }
        // 8. Save entity
        entity = timeSheetDetailRepository.saveAndFlush(entity);

        // 9. Nếu chấm công thủ công chưa gán TimeSheet thì tạo mới
        if (entity.getTimeSheet() == null && dto.getWorkingDate() != null && entity.getEmployee() != null) {
            List<TimeSheet> timeSheets = timeSheetRepository.getTimeSheetByWorkingDate(
                    entity.getEmployee().getId(), dto.getWorkingDate());
            TimeSheet timeSheet = (timeSheets != null && !timeSheets.isEmpty()) ? timeSheets.get(0) : null;

            if (timeSheet == null) {
                timeSheet = new TimeSheet();
                timeSheet.setWorkingDate(dto.getWorkingDate());
                timeSheet.setSchedule(staffWorkSchedule);
                timeSheet.setStaff(entity.getEmployee());
            }
            if (timeSheet.getDetails() == null) {
                timeSheet.setDetails(new HashSet<>());
            }
            timeSheet.addDetail(entity);
            timeSheetRepository.saveAndFlush(timeSheet);
        }

        TimeSheetDetailDto response = new TimeSheetDetailDto(entity, true);

        entityManager.flush();
        entityManager.clear();


        // 10. Cập nhật giờ làm và bảng lương
        if (response.getStaffWorkSchedule() != null) {
//            if ((entity.getTimeSheet() != null && entity.getTimeSheet().getDetails() != null && !entity.getTimeSheet().getDetails().isEmpty())
//                    || (entity.getStaffWorkSchedule().getTimesheetDetails() != null && !entity.getStaffWorkSchedule().getTimesheetDetails().isEmpty())) {
            // Thống kê lại chấm công
            calculateStaffWorkTimeServiceV2.calculateStaffWorkTimeAndSave(response.getStaffWorkSchedule().getId());
//            } else {
//                System.out.println("Các lần chấm công của ca làm việc đang trống");
//            }

            entityManager.flush();
            entityManager.clear();

            // Tính lại phiếu lương
            salaryResultStaffItemService.updateTimekeepingDataForPayslips(
                    response.getEmployee().getId(), response.getStaffWorkSchedule().getWorkingDate());
        }

        return response;
    }

    @Override
    public List<TimeSheetStaffDto> importFromInputStream(InputStream is) throws IOException {
        List<TimeSheetStaffDto> listTimeSheetStaff = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Calendar calendar = Calendar.getInstance();

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null)
                    continue;
                // int index = 0;
                Cell firstCell = row.getCell(0);
                if (firstCell == null || firstCell.getCellType() == Cell.CELL_TYPE_BLANK) {
                    continue; // Nếu cột đầu tiên (workingDate) không có dữ liệu, bỏ qua dòng
                }
                // indexRow
                String rowIndexStr = String.valueOf(rowIndex + 1);
                // workingDate
                SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");
                Date workingDate = ExcelUtils.parseDateCellValue(firstCell, rowIndex, row.getRowNum(), dateFormat);
                // staffCode
                String staffCode = this.parseStringCellValue(row.getCell(1));

                // shiftWorkCode
                String shiftWorkCode = this.parseStringCellValue(row.getCell(2));

                // shiftWorkTimePeriodCode
                String shiftWorkTimePeriodCode = this.parseStringCellValue(row.getCell(3));

                LocalDate workingLocalDate = workingDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                // startTime
                LocalDateTime startDateTime = null;
                Cell startTimeCell = row.getCell(4);
                if (startTimeCell != null) {
                    if (startTimeCell.getCellType() == Cell.CELL_TYPE_NUMERIC
                            && DateUtil.isCellDateFormatted(startTimeCell)) {
                        calendar.setTime(startTimeCell.getDateCellValue());
                        LocalTime startTime = LocalTime.of(calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
                        startDateTime = LocalDateTime.of(workingLocalDate, startTime);
                    } else if (startTimeCell.getCellType() == Cell.CELL_TYPE_STRING) {
                        String timeText = startTimeCell.getStringCellValue().trim();
                        String[] parts = timeText.split(":");
                        if (parts.length == 2) {
                            LocalTime startTime = LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                            startDateTime = LocalDateTime.of(workingLocalDate, startTime);
                        }
                    }
                }

                // endTime
                LocalDateTime endDateTime = null;
                Cell endTimeCell = row.getCell(5);
                if (endTimeCell != null) {
                    if (endTimeCell.getCellType() == Cell.CELL_TYPE_NUMERIC
                            && DateUtil.isCellDateFormatted(endTimeCell)) {
                        calendar.setTime(endTimeCell.getDateCellValue());
                        LocalTime endTime = LocalTime.of(calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
                        endDateTime = LocalDateTime.of(workingLocalDate, endTime);
                    } else if (endTimeCell.getCellType() == Cell.CELL_TYPE_STRING) {
                        String timeText = endTimeCell.getStringCellValue().trim();
                        String[] parts = timeText.split(":");
                        if (parts.length == 2) {
                            LocalTime endTime = LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                            endDateTime = LocalDateTime.of(workingLocalDate, endTime);
                        }
                    }
                }

                // Chuyển LocalDateTime thành Date nếu cần
                Date startDate = startDateTime != null
                        ? Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant())
                        : null;
                Date endDate = endDateTime != null ? Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant())
                        : null;

                // IP
                Cell addressIP = row.getCell(6);
                String ip = this.parseStringCellValue(addressIP);

                TimeSheetStaffDto dto = new TimeSheetStaffDto();
                dto.setWorkingDate(workingDate);
                Staff staff = null;
                List<Staff> listStaff = staffRepository.getByCode(staffCode);
                if (listStaff != null && !listStaff.isEmpty()) {
                    staff = listStaff.get(0);
                    if (staff != null) {
                        dto.setStaffId(staff.getId());
                    }
                }

                // Ca làm việc = shiftWorkTimePeriod
                ShiftWorkTimePeriodDto shiftWorkTimePeriodDto = null;
                ShiftWorkTimePeriod shiftWorkTimePeriod = shiftWorkTimePeriodRepository
                        .findByCode(shiftWorkTimePeriodCode);
                if (shiftWorkTimePeriod != null) {
                    shiftWorkTimePeriodDto = new ShiftWorkTimePeriodDto(shiftWorkTimePeriod);
                }
                dto.setShiftWorkTimePeriod(shiftWorkTimePeriodDto);

                // Phân ca làm việc = staffWorkSchedule
                StaffWorkScheduleDto staffWorkScheduleDto = null;
                if (staff != null && staff.getId() != null && workingDate != null && shiftWorkCode != null) {
                    List<StaffWorkSchedule> listStaffWorkSchedule = staffWorkScheduleRepository
                            .getByStaffAndDateAndShiftWorkCode(staff.getId(), workingDate, shiftWorkCode);
                    if (!CollectionUtils.isEmpty(listStaffWorkSchedule)) {
                        StaffWorkSchedule staffWorkSchedule = listStaffWorkSchedule.get(0);
                        staffWorkScheduleDto = new StaffWorkScheduleDto(staffWorkSchedule);
                    }
                }
                dto.setStaffWorkSchedule(staffWorkScheduleDto);

                // them vao listTimeSheetStaff
                // if (startTime != null) {
                TimeSheetStaffDto dtoStart = new TimeSheetStaffDto();
                dtoStart.setIndexRowExcel(rowIndexStr);
                dtoStart.setWorkingDate(workingDate);
                dtoStart.setStaffId(dto.getStaffId());
                dtoStart.setShiftWorkTimePeriod(dto.getShiftWorkTimePeriod());
                dtoStart.setStaffWorkSchedule(dto.getStaffWorkSchedule());

                dtoStart.setCurrentTime(startDate);
                dtoStart.setTypeTimeSheetDetail(HrConstants.TypeTimeSheetDetail.START.getValue());
                dtoStart.setIpCheckIn(ip);
                listTimeSheetStaff.add(dtoStart);
                // }
                // them vao listTimeSheetStaff
                // if (endTime != null) {
                TimeSheetStaffDto dtoEnd = new TimeSheetStaffDto();
                dtoEnd.setIndexRowExcel(rowIndexStr);
                dtoEnd.setWorkingDate(workingDate);
                dtoEnd.setStaffId(dto.getStaffId());
                dtoEnd.setShiftWorkTimePeriod(dto.getShiftWorkTimePeriod());
                dtoEnd.setStaffWorkSchedule(dto.getStaffWorkSchedule());

                dtoEnd.setCurrentTime(endDate);
                dtoEnd.setTypeTimeSheetDetail(HrConstants.TypeTimeSheetDetail.END.getValue());
                dtoEnd.setIpCheckOut(ip);
                listTimeSheetStaff.add(dtoEnd);
                // }
            }
        }
        return listTimeSheetStaff;
    }

    private static boolean isDateCell(Cell cell) {
        return cell != null && cell.getCellType() == Cell.CELL_TYPE_NUMERIC && HSSFDateUtil.isCellDateFormatted(cell);
    }

    private String parseStringCellValue(Cell cell) {
        if (cell == null)
            return null;
        return switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING -> cell.getStringCellValue().trim();
            case Cell.CELL_TYPE_NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> "";
        };
    }


    private static final String DATE_FORMAT = "dd/MM/yyyy";

    @Override
    public Workbook exportDataWithSystemTemplate(SearchTimeSheetDto dto) {
        if (dto == null) {
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(TEMPLATE_PATH)) {
            if (fileInputStream == null) {
                throw new IOException("File '" + TEMPLATE_PATH + "' không tìm thấy trong classpath");
            }

            dto.setPageIndex(1);
            dto.setPageSize(999999999);

            Page<TimeSheetDetailDto> exportDataPage = this.searchByPage(dto);
            if (exportDataPage == null)
                return null;

            List<TimeSheetDetailDto> exportData = exportDataPage.getContent();

            Workbook workbook = new XSSFWorkbook(fileInputStream);

            Sheet importSheet = workbook.getSheetAt(0);
            CellStyle dataCellStyle = ExportExcelUtil.createDataCellStyle(workbook);

            int rowIndex = 1;
            int orderNumber = 1;
            long startTime = System.nanoTime();

            for (TimeSheetDetailDto exportItem : exportData) {
                if (exportItem == null)
                    continue;

                Row dataRow = importSheet.createRow(rowIndex);
                int cellIndex = 0;

                // 0. STT
                ExportExcelUtil.createCell(dataRow, 0, orderNumber, dataCellStyle);
                orderNumber++;

                // 1. Mã nhân viên
                String staffCode = "";
                if (exportItem.getEmployee() != null) {
                    staffCode = exportItem.getEmployee().getStaffCode();
                }
                ExportExcelUtil.createCell(dataRow, 1, staffCode, dataCellStyle);

                // 2. Tên nhân viên
                String staffName = "";
                if (exportItem.getEmployee() != null) {
                    staffName = exportItem.getEmployee().getDisplayName();
                }
                ExportExcelUtil.createCell(dataRow, 2, staffName, dataCellStyle);

                // 3. Ngày làm việc
                String workingDate = "";
                if (exportItem.getWorkingDate() != null) {
                    workingDate = formatDate(exportItem.getWorkingDate());
                }
                ExportExcelUtil.createCell(dataRow, 3, workingDate, dataCellStyle);

                // 4. Mã ca làm việc
                String shiftWorkCode = "";
                if (exportItem.getStaffWorkSchedule() != null
                        && exportItem.getStaffWorkSchedule().getShiftWork() != null) {
                    shiftWorkCode = exportItem.getStaffWorkSchedule().getShiftWork().getCode();
                }
                ExportExcelUtil.createCell(dataRow, 4, shiftWorkCode, dataCellStyle);

                // 5. Ca làm việc
                String shiftWorkName = "";
                if (exportItem.getStaffWorkSchedule() != null
                        && exportItem.getStaffWorkSchedule().getShiftWork() != null) {
                    shiftWorkName = exportItem.getStaffWorkSchedule().getShiftWork().getName();
                }
                ExportExcelUtil.createCell(dataRow, 5, shiftWorkName, dataCellStyle);

                // 6. Mã giai đoạn làm việc
                String shiftWorkTimeCode = "";
                if (exportItem.getShiftWorkTimePeriod() != null) {
                    shiftWorkTimeCode = exportItem.getShiftWorkTimePeriod().getCode();
                }
                ExportExcelUtil.createCell(dataRow, 6, shiftWorkTimeCode, dataCellStyle);

                // 7. Thời gian vào
                String startTimeFormatted = "";
                if (exportItem.getStartTime() != null) {
                    startTimeFormatted = timeFormat.format(exportItem.getStartTime());
                }
                ExportExcelUtil.createCell(dataRow, 7, startTimeFormatted, dataCellStyle);

                // 8. IP checkin
                String ipCheckIn = exportItem.getAddressIPCheckIn() != null ? exportItem.getAddressIPCheckIn() : "";
                ExportExcelUtil.createCell(dataRow, 8, ipCheckIn, dataCellStyle);

                // 9. Thời gian ra
                String endTimeFormatted = "";
                if (exportItem.getEndTime() != null) {
                    endTimeFormatted = timeFormat.format(exportItem.getEndTime());
                }
                ExportExcelUtil.createCell(dataRow, 9, endTimeFormatted, dataCellStyle);

                // 10. IP checkout
                String ipCheckOut = exportItem.getAddressIPCheckOut() != null ? exportItem.getAddressIPCheckOut() : "";
                ExportExcelUtil.createCell(dataRow, 10, ipCheckOut, dataCellStyle);

                // thêm dòng tiếp theo
                rowIndex++;
            }

            long endTime = System.nanoTime();
            long elapsedTimeMs = (endTime - startTime) / 1_000_000;

            logger.info("Xuất mẫu dữ chấm công - Xử lý mất {} ms ", elapsedTimeMs);
            return workbook;

        } catch (IOException e) {
            System.err.println("Error reading Excel template: " + e.getMessage());
            return null;
        }
    }

    private String formatDate(Date date) {
        if (date == null)
            return "";
        return new SimpleDateFormat(DATE_FORMAT).format(date);
    }

    @Override
    public List<ImportTimesheetDetailDto> importDataWithSystemTemplate(InputStream inputStream,
                                                                       List<ImportTimesheetDetailDto> importResults) {
        return List.of();
    }

    @Override
    public ByteArrayOutputStream exportImportResultTimeSheet(MultipartFile file) throws IOException {
        // Đọc workbook gốc từ file đầu vào
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());

        // Lấy danh sách dữ liệu từ file Excel
        ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
        List<TimeSheetStaffDto> listData = importFromInputStream(bis);

        // Tạo sheet mới để ghi kết quả xử lý
        XSSFSheet resultSheet = this.createUniqueSheet(workbook, "Kết quả xử lý");

        // Ghi header
        Row header = resultSheet.createRow(0);
        header.createCell(0).setCellValue("Dòng");
        header.createCell(1).setCellValue("Ngày chấm công");
        header.createCell(2).setCellValue("Loại chấm công");
        header.createCell(3).setCellValue("Kết quả");

        if (listData != null && !listData.isEmpty()) {
            for (int i = 0; i < listData.size(); i++) {
                TimeSheetStaffDto dto = listData.get(i);
                String result = timeSheetService.saveTimekeeping(dto, null);

                Row row = resultSheet.createRow(i + 1);
                row.createCell(0).setCellValue(dto.getIndexRowExcel());
                String workingDateStr = null;
                if (dto.getWorkingDate() != null) {
                    workingDateStr = this.formatDate(dto.getWorkingDate());
                }
                row.createCell(1).setCellValue(workingDateStr != null ? workingDateStr : "N/A");
                String typeTimeSheetDetailStr = null;
                if (dto.getTypeTimeSheetDetail() != null) {
                    if (dto.getTypeTimeSheetDetail().equals(HrConstants.TypeTimeSheetDetail.START.getValue())) {
                        typeTimeSheetDetailStr = "CHECK IN";
                    } else if (dto.getTypeTimeSheetDetail().equals(HrConstants.TypeTimeSheetDetail.END.getValue())) {
                        typeTimeSheetDetailStr = "CHECK OUT";
                    }
                }
                row.createCell(2).setCellValue(typeTimeSheetDetailStr != null ? typeTimeSheetDetailStr : "N/A");
                row.createCell(3).setCellValue(result);
            }
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        return bos;
    }

    private XSSFSheet createUniqueSheet(XSSFWorkbook workbook, String baseName) {
        String sheetName = baseName;
        int index = 1;

        // Kiểm tra sheet đã tồn tại chưa
        while (workbook.getSheet(sheetName) != null) {
            sheetName = baseName + " (" + index + ")";
            index++;
        }

        return workbook.createSheet(sheetName);
    }

    /*
     * Đọc file data theo mẫu template ImportExportTimesheetDetailSystem resutl
     * List<TimeSheetDetailDto>
     */
    @Override
    public List<TimeSheetDetailDto> readImportExportTimesheetDetailSystem(InputStream is) throws IOException {
        List<TimeSheetDetailDto> listTimeSheetDetail = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Calendar calendar = Calendar.getInstance();

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null)
                    continue;
                // int index = 0;
                Cell staffCodeCell = row.getCell(1);
                Cell workingDateCell = row.getCell(3);
                if (staffCodeCell == null || staffCodeCell.getCellType() == Cell.CELL_TYPE_BLANK) {
                    continue;
                }
                if (workingDateCell == null || workingDateCell.getCellType() == Cell.CELL_TYPE_BLANK) {
                    continue;
                }
                // indexRow
                String rowIndexStr = String.valueOf(rowIndex + 1);
                // staffCode
                String staffCode = this.parseStringCellValue(staffCodeCell);
                // workingDate
                SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");
                Date workingDate = ExcelUtils.parseDateCellValue(workingDateCell, rowIndex, row.getRowNum(), dateFormat);
                // shiftWorkCode
                Cell shiftWorkCodeCell = row.getCell(4);
                String shiftWorkCode = this.parseStringCellValue(shiftWorkCodeCell);
                // shiftWorkTimePeriodCode
                Cell shiftWorkTimePeriodCodeCell = row.getCell(6);
                String shiftWorkTimePeriodCode = this.parseStringCellValue(shiftWorkTimePeriodCodeCell);

                LocalDate workingLocalDate = workingDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                // startTime
                LocalDateTime startDateTime = null;
                Cell startTimeCell = row.getCell(7);
                if (startTimeCell != null) {
                    if (startTimeCell.getCellType() == Cell.CELL_TYPE_NUMERIC
                            && DateUtil.isCellDateFormatted(startTimeCell)) {
                        calendar.setTime(startTimeCell.getDateCellValue());
                        LocalTime startTime = LocalTime.of(calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
                        startDateTime = LocalDateTime.of(workingLocalDate, startTime);
                    } else if (startTimeCell.getCellType() == Cell.CELL_TYPE_STRING) {
                        String timeText = startTimeCell.getStringCellValue().trim();
                        String[] parts = timeText.split(":");
                        if (parts.length == 2) {
                            LocalTime startTime = LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                            startDateTime = LocalDateTime.of(workingLocalDate, startTime);
                        }
                    }
                }
                // IP checkin
                Cell addressIPCheckIn = row.getCell(10);
                String ipCheckIn = this.parseStringCellValue(addressIPCheckIn);

                // endTime
                LocalDateTime endDateTime = null;
                Cell endTimeCell = row.getCell(9);
                if (endTimeCell != null) {
                    if (endTimeCell.getCellType() == Cell.CELL_TYPE_NUMERIC
                            && DateUtil.isCellDateFormatted(endTimeCell)) {
                        calendar.setTime(endTimeCell.getDateCellValue());
                        LocalTime endTime = LocalTime.of(calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
                        endDateTime = LocalDateTime.of(workingLocalDate, endTime);
                    } else if (endTimeCell.getCellType() == Cell.CELL_TYPE_STRING) {
                        String timeText = endTimeCell.getStringCellValue().trim();
                        String[] parts = timeText.split(":");
                        if (parts.length == 2) {
                            LocalTime endTime = LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                            endDateTime = LocalDateTime.of(workingLocalDate, endTime);
                        }
                    }
                }
                // IP
                Cell addressIPCheckOut = row.getCell(10);
                String ipCheckOut = this.parseStringCellValue(addressIPCheckOut);

                // Chuyển LocalDateTime thành Date nếu cần
                Date startDate = startDateTime != null
                        ? Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant())
                        : null;
                Date endDate = endDateTime != null ? Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant())
                        : null;

                TimeSheetDetailDto dto = new TimeSheetDetailDto();
                dto.setIndexRowExcel(rowIndexStr);
                dto.setStaffCode(staffCode);
                dto.setWorkingDate(workingDate);
                dto.setShiftWorkCode(shiftWorkCode);
                dto.setShiftWorkTimePeriodCode(shiftWorkTimePeriodCode);
                dto.setStartTime(startDate);
                dto.setAddressIPCheckIn(ipCheckIn);
                dto.setEndTime(endDate);
                dto.setAddressIPCheckOut(ipCheckOut);

                listTimeSheetDetail.add(dto);

            }
        }
        return listTimeSheetDetail;
    }

    /*
     * Read file excel Save data excel Return file excel with result save
     */
    @Override
    public ByteArrayOutputStream exportImportResultTimeSheetDetailSystem(MultipartFile file) throws IOException {
        // Đọc workbook gốc từ file đầu vào
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());

        // Lấy danh sách dữ liệu từ file Excel
        ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
        List<TimeSheetDetailDto> listData = readImportExportTimesheetDetailSystem(bis);

        // Tạo sheet mới để ghi kết quả xử lý
        XSSFSheet resultSheet = this.createUniqueSheet(workbook, "Kết quả xử lý");

        // Ghi header
        Row header = resultSheet.createRow(0);
        header.createCell(0).setCellValue("Dòng");
        header.createCell(1).setCellValue("Ngày chấm công");
        header.createCell(2).setCellValue("Kết quả");

        if (listData != null && !listData.isEmpty()) {
            for (int i = 0; i < listData.size(); i++) {
                TimeSheetDetailDto dto = listData.get(i);
                timeSheetService.saveTimekeeping(null, null);
                String result = this.saveTimeSheetDetailFromImport(dto);

                Row row = resultSheet.createRow(i + 1);
                row.createCell(0).setCellValue(dto.getIndexRowExcel());
                String workingDateStr = null;
                if (dto.getWorkingDate() != null) {
                    workingDateStr = this.formatDate(dto.getWorkingDate());
                }
                row.createCell(1).setCellValue(workingDateStr != null ? workingDateStr : "N/A");
                row.createCell(2).setCellValue(result);
            }
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        return bos;
    }

    private String saveTimeSheetDetailFromImport(TimeSheetDetailDto dto) {
        if (dto == null || dto.getWorkingDate() == null)
            return "Không có dữ liệu";

        if (dto.getStaffCode() == null) {
            return "Không xác định được nhân viên chấm công";
        }
        if (dto.getWorkingDate() == null) {
            return "Không xác định được ngày chấm công";
        }
        if (dto.getShiftWorkCode() == null) {
            return "Không xác định được ca làm việc";
        }
        if (dto.getStartTime() == null && dto.getEndTime() == null) {
            return "Chưa có thời gian checkout/checkin";
        }

        Staff staff = null;
        List<Staff> staffList = staffRepository.findByCode(dto.getStaffCode());
        if (staffList != null && staffList.size() > 0) {
            staff = staffList.get(0);
        } else {
            return "Không xác định được nhân viên chấm công";
        }
        StaffWorkSchedule staffWorkSchedule = null;
        List<StaffWorkSchedule> staffWorkScheduleList = staffWorkScheduleRepository
                .getByStaffAndDateAndShiftWorkCode(staff.getId(), dto.getWorkingDate(), dto.getShiftWorkCode());
        if (staffWorkScheduleList != null && staffWorkScheduleList.size() > 0) {
            staffWorkSchedule = staffWorkScheduleList.get(0);
        }
        if (staffWorkSchedule == null) {
            return "Nhân viên chưa được phân ca vào ngày chấm công";
        }
        ShiftWorkTimePeriod shiftWorkTimePeriod = null;
        // Kiểm tra ca làm việc nếu ca cho phép chấm công ra vào nhiều lần
        if (staffWorkSchedule.getAllowOneEntryOnly() == null || (staffWorkSchedule != null && staffWorkSchedule.getAllowOneEntryOnly() == false)) {
            if (dto.getShiftWorkTimePeriodCode() == null) {
                return "Ca làm việc cho phép chấm công ra vào nhiều lần trong toàn ca, cần bổ sung mã giao đoạn ca làm việc";
            } else {
                shiftWorkTimePeriod = shiftWorkTimePeriodRepository.findByCode(dto.getShiftWorkTimePeriodCode());
            }
            if (shiftWorkTimePeriod == null) {
                return "Không xác định được giao đoạn làm việc";
            }
        }
        // Địa chỉ IP có hợp lệ hay không, hợp lệ mới được chấm công
        Boolean isValidTimekeepingIPCheckIn = false, isValidTimekeepingIPCheckOut = false;

        // Có những nhân viên được phép chấm công với IP ngoài => Khi đó luôn hợp lệ
        if (staff.getAllowExternalIpTimekeeping() != null && staff.getAllowExternalIpTimekeeping().equals(true)) {
            isValidTimekeepingIPCheckIn = true;
            isValidTimekeepingIPCheckOut = true;
        } else {
            isValidTimekeepingIPCheckIn = hrDepartmentIpService.isValidTimekeepingIP(staff.getId(),
                    dto.getAddressIPCheckIn());
            isValidTimekeepingIPCheckOut = hrDepartmentIpService.isValidTimekeepingIP(staff.getId(),
                    dto.getAddressIPCheckOut());
        }
        if (isValidTimekeepingIPCheckIn == null || isValidTimekeepingIPCheckIn.equals(false)) {
            return "Địa chỉ IP check in chấm công không hợp lệ";
        }
        if (isValidTimekeepingIPCheckOut == null || isValidTimekeepingIPCheckOut.equals(false)) {
            return "Địa chỉ IP check out chấm công không hợp lệ";
        }
        if (staffWorkSchedule.getTimekeepingCalculationType() == null) {
            staffWorkSchedule
                    .setTimekeepingCalculationType(HrConstants.TimekeepingCalculationType.FIRST_IN_LAST_OUT.getValue());
        }

        TimeSheetDetailDto timeSheetDetailDto = this.saveOrUpdateForImport(dto, staff, staffWorkSchedule, shiftWorkTimePeriod);

        entityManager.flush();
        entityManager.clear();

        if (timeSheetDetailDto != null) {
            return "Chấm công thành công";
        } else {
            return "Chấm công thất bại";
        }
    }

    public TimeSheetDetailDto saveOrUpdateForImport(TimeSheetDetailDto dto, Staff staff,
                                                    StaffWorkSchedule staffWorkSchedule, ShiftWorkTimePeriod shiftWorkTimePeriod) {
        if (dto == null || staff == null || staffWorkSchedule == null)
            return null;

        TimeSheetDetail entity = null;

        // Tìm bản ghi chấm công cũ nếu có
        // Nếu là chấm công ra vào 1 lần
        if (staffWorkSchedule.getAllowOneEntryOnly() != null && staffWorkSchedule.getAllowOneEntryOnly() == true) {
            if (staff.getId() != null && staffWorkSchedule.getId() != null && dto.getWorkingDate() != null) {
                List<TimeSheetDetail> entityList = timeSheetDetailRepository.getEntityListByStaffAndDateAndSchedule(
                        staff.getId(), dto.getWorkingDate(), staffWorkSchedule.getId());
                if (entityList != null && !entityList.isEmpty()) {
                    entity = entityList.get(0);
                }
            }
        } else {
            if (staff.getId() != null && staffWorkSchedule.getId() != null && dto.getWorkingDate() != null && shiftWorkTimePeriod.getId() != null) {
                List<TimeSheetDetail> entityList = timeSheetDetailRepository.getEntityListByShiftWorkTimePeriod(
                        staff.getId(), dto.getWorkingDate(), staffWorkSchedule.getId(), shiftWorkTimePeriod.getId());
                if (entityList != null && !entityList.isEmpty()) {
                    entity = entityList.get(0);
                }
            }
        }

        if (entity == null) {
            entity = new TimeSheetDetail();
            entity.setEmployee(staff);
            entity.setStaffWorkSchedule(staffWorkSchedule);
            entity.setShiftWorkTimePeriod(shiftWorkTimePeriod);
            entity.setStartTime(dto.getStartTime());
            entity.setAddressIPCheckIn(dto.getAddressIPCheckIn());
            entity.setEndTime(dto.getEndTime());
            entity.setAddressIPCheckOut(dto.getAddressIPCheckOut());

            // Save ngay để lấy ID, tránh duplicate session objects
            entity = timeSheetDetailRepository.save(entity);

            // Sau khi save (đã có ID), mới gán vào staffWorkSchedule.details nếu cần
            if (staffWorkSchedule.getTimesheetDetails() == null) {
                staffWorkSchedule.setTimesheetDetails(new HashSet<>());
            }
            staffWorkSchedule.getTimesheetDetails().add(entity);
        } else {
            // Đang update entity cũ
            entity.setStartTime(dto.getStartTime());
            entity.setAddressIPCheckIn(dto.getAddressIPCheckIn());
            entity.setEndTime(dto.getEndTime());
            entity.setAddressIPCheckOut(dto.getAddressIPCheckOut());
            entity = timeSheetDetailRepository.save(entity);
        }

        // Tạo hoặc gán TimeSheet
        TimeSheet timeSheet = entity.getTimeSheet();
        // Nếu TimeSheet null -> tạo mới hoặc tìm theo ngày và staff
        if (timeSheet == null && dto.getWorkingDate() != null) {
            List<TimeSheet> timeSheets = timeSheetRepository.getTimeSheetByWorkingDate(staff.getId(),
                    dto.getWorkingDate());

            if (timeSheets != null && !timeSheets.isEmpty()) {
                timeSheet = timeSheets.get(0);
            } else {
                timeSheet = new TimeSheet();
                timeSheet.setWorkingDate(dto.getWorkingDate());
                timeSheet.setSchedule(staffWorkSchedule);
                timeSheet.setStaff(staff);
            }

            // Đảm bảo details không null
            if (timeSheet.getDetails() == null) {
                timeSheet.setDetails(new HashSet<>());
            }
            if (staffWorkSchedule.getAllowOneEntryOnly() != null && staffWorkSchedule.getAllowOneEntryOnly() == true) {
                // Gán TimeSheet vào entity nếu chưa gán
                timeSheet.addDetail(entity);
            } else {
                entity.setTimeSheet(timeSheet);
                timeSheet.getDetails().add(entity);
            }

            timeSheet = timeSheetRepository.save(timeSheet);
        }

        // Re-sync entity sau khi gán TimeSheet
        entity = timeSheetDetailRepository.save(entity);

        // Tính lại thời gian làm việc
        if (entity.getStaffWorkSchedule() != null && entity.getTimeSheet() != null) {
            // Cập nhật dữ liệu lương
            calculateStaffWorkTimeServiceV2
                    .calculateStaffWorkTimeAndSave(entity.getStaffWorkSchedule().getId());
        } else {
            System.out.println("Các lần chấm công của ca làm việc đang trống");
        }

        // Cập nhật dữ liệu lương
        salaryResultStaffItemService.updateTimekeepingDataForPayslips(entity.getEmployee(), dto.getWorkingDate());

        entityManager.flush();
        entityManager.clear();

        return new TimeSheetDetailDto(entity);
    }

    // Hàm convert từ list chấm công lấy từ API convert về list chấm công PM
    @Override
    public List<TimeSheetDetailDto> getListByApiTimeSheet(TimeSheetResponseDto result, String fromDate, String
            toDate, Boolean isOneTimeLock) {
        if (result == null || result.getTable1() == null || result.getTable1().isEmpty()) {
            return Collections.emptyList();
        }
        // 1. Preload all nhân viên
        Map<String, StaffDto> staffMap = staffRepository.findAllDtos().stream()
                .filter(dto -> dto.getStaffCode() != null) // tránh null key
                .collect(Collectors.toMap(
                        StaffDto::getStaffCode,
                        Function.identity(),
                        (existing, replacement) -> existing // giữ cái đầu tiên
                ));

        List<TimeSheetDetailDto> ret = new ArrayList<>();

        // 2. Preload all lịch làm việc
        List<StaffWorkSchedule> allSchedules = this.findAllByWorkingDateRange(fromDate, toDate);
        Map<String, List<StaffWorkSchedule>> scheduleMap = allSchedules.stream()
                .collect(Collectors.groupingBy(s ->
                        s.getStaff().getId() + "_" + formatDate(s.getWorkingDate())
                ));

        // Group dữ liệu từ máy chấm công
        Map<String, List<TimeSheetRecordDto>> grouped = result.getTable1().stream()
                .collect(Collectors.groupingBy(record ->
                        record.getMaChamCong() + "_" + record.getMaNhanVien() + "_" + record.getNgayCham()
                ));

        for (List<TimeSheetRecordDto> group : grouped.values()) {
            group.sort(Comparator.comparing(record -> {
                try {
                    return record.getNgayGioChamCongDate();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }));
            if (isOneTimeLock != null && isOneTimeLock) {
                // Lấy bản ghi đầu tiên (sớm nhất) và cuối cùng (muộn nhất)
                TimeSheetRecordDto start = group.get(0);
                TimeSheetRecordDto end = group.size() > 1 ? group.get(group.size() - 1) : null;

                TimeSheetDetailDto dto = createTimeSheetDetail(start, end, staffMap, scheduleMap, isOneTimeLock);
                if (dto != null) {
                    ret.add(dto);
                }
            } else {
                // Xử lý theo từng cặp start - end
                for (int i = 0; i + 1 < group.size(); i += 2) {
                    TimeSheetRecordDto start = group.get(i);
                    TimeSheetRecordDto end = group.get(i + 1);

                    TimeSheetDetailDto dto = createTimeSheetDetail(start, end, staffMap, scheduleMap, isOneTimeLock);
                    if (dto != null) {
                        ret.add(dto);
                    }
                }

                // Xử lý nếu còn dư 1 bản ghi
                if (group.size() % 2 != 0) {
                    TimeSheetRecordDto start = group.get(group.size() - 1);

                    TimeSheetDetailDto dto = createTimeSheetDetail(start, null, staffMap, scheduleMap, isOneTimeLock);
                    if (dto != null) {
                        ret.add(dto);
                    }
                }
            }


        }

        return ret;
    }

    // Tách hàm để tạo TimeSheetDetailDto cho gọn
    private TimeSheetDetailDto createTimeSheetDetail(TimeSheetRecordDto start, TimeSheetRecordDto end,
                                                     Map<String, StaffDto> staffMap,
                                                     Map<String, List<StaffWorkSchedule>> scheduleMap, Boolean isOneTimeLock) {
        TimeSheetDetailDto dto = new TimeSheetDetailDto();
        try {
            dto.setStartTime(start.getNgayGioChamCongDate());
            if (end != null) {
                dto.setEndTime(end.getNgayGioChamCongDate());
            }
            dto.setWorkingDate(start.getNgayGioChamCongDate());
            dto.setTimekeepingCode(String.valueOf(start.getMaChamCong()));
        } catch (ParseException e) {
            return null; // skip nếu lỗi parse
        }
        if (start.getMaNhanVien() != null && StringUtils.hasText(start.getMaNhanVien())) {
            String staffCode = start.getMaNhanVien().trim();

            dto.setStaffCode(staffCode);
            StaffDto employee = staffMap.get(staffCode);
            if (employee != null) {
                dto.setEmployee(employee);
            }
        }

        if (dto.getEmployee() != null) {
            String scheduleKey = dto.getEmployee().getId() + "_" + formatDate(dto.getWorkingDate());
            List<StaffWorkSchedule> schedules = scheduleMap.get(scheduleKey);
            if (schedules != null && !schedules.isEmpty()) {
                for (StaffWorkSchedule schedule : schedules) {
                    if (schedule.getShiftWork() == null) continue;
                    if (isOneTimeLock != null && isOneTimeLock) { // Không cần kiểm tra có khoảng thời gian giao với ca làm việc nữa vì mặc định 1 ngày chỉ có 1 ca làm việc => gán ca làm việc luôn vào lần chấm công
                        dto.setStaffWorkSchedule(new StaffWorkScheduleDto());
                        dto.getStaffWorkSchedule().setId(schedule.getId());
                        break;
                    } else if (isInShift(dto.getStartTime(), dto.getEndTime(), schedule.getShiftWork())) {//TH ma van chấm nhiều thì như hiện tại lẻ vào chẵn ra
                        dto.setStaffWorkSchedule(new StaffWorkScheduleDto());
                        dto.getStaffWorkSchedule().setId(schedule.getId());
                        break;
                    }

//                    if (isInShift(dto.getStartTime(), dto.getEndTime(), schedule)) {
//                        dto.setStaffWorkSchedule(new StaffWorkScheduleDto());
//                        dto.getStaffWorkSchedule().setId(schedule.getId());
//                        break;
//                    }
                }
            }

            dto.setIsSync(true); // đánh dấu là dữ liệu đồng bộ từ máy chấm công
            return dto;
        } else {
            return null; // nếu không map được nhân viên
        }
    }

    //ham lay danh sach phan ca lam viec tu ngay den ngay
    public List<StaffWorkSchedule> findAllByWorkingDateRange(String fromDateStr, String toDateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate;
        Date toDate;

        try {
            fromDate = sdf.parse(fromDateStr);
            toDate = sdf.parse(toDateStr);

            // Set toDate thành cuối ngày (23:59:59.999)
            Calendar cal = Calendar.getInstance();
            cal.setTime(toDate);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);

            toDate = cal.getTime();
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format, must be yyyy-MM-dd", e);
        }

        return staffWorkScheduleRepository.getWorkScheduleInRangeTime(fromDate, toDate);
    }

    // Hàm phụ: check xem thời gian chấm công có nằm trong ca làm việc hay không
    private boolean isInShift(Date startTime, Date endTime, StaffWorkSchedule schedule) {
        if (schedule.getWorkingDate() == null) {
            return false;
        }

        // Giả sử startWorkingTime và endWorkingTime là kiểu Date
        return (startTime.before(schedule.getWorkingDate()) || startTime.equals(schedule.getWorkingDate()))
                && (endTime.after(schedule.getWorkingDate()) || endTime.equals(schedule.getWorkingDate()));
    }

    // Hàm phụ: check xem thời gian chấm công có nằm trong ca làm việc hay không
    private boolean isInShift(Date startTime, Date endTime, ShiftWork shiftWork) {
        if (startTime == null || endTime == null || shiftWork == null || shiftWork.getTimePeriods() == null) {
            return false;
        }

        // Convert Date thành số phút trong ngày (0 - 1440 phút)
        int startMinutes = toMinutesOfDay(startTime);
        int endMinutes = toMinutesOfDay(endTime);

        for (ShiftWorkTimePeriod period : shiftWork.getTimePeriods()) {
            int periodStartMinutes = toMinutesOfDay(period.getStartTime());
            int periodEndMinutes = toMinutesOfDay(period.getEndTime());

            // Kiểm tra giao nhau theo phút
            if (startMinutes < periodEndMinutes && endMinutes > periodStartMinutes) {
                return true;
            }
        }

        return false;
    }

    private int toMinutesOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
    }

    @Override
    public List<TimeSheetDetailDto> convertTimeSheetDetailByApiTimeSheet(SearchTimeSheetApiDto dto) {
        if (dto == null || dto.getFromdate() == null || dto.getTodate() == null || dto.getUrl() == null)
            return null;

        try {
            TimeSheetResponseDto result = RestApiUtils.postApi(dto.getUrl(), dto);
            if (result != null) {
                List<TimeSheetDetailDto> ret = getListByApiTimeSheet(result, dto.getFromdate(), dto.getTodate(), dto.getIsOneTimeLock());
                return ret;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Workbook exportExcelLATimekeepingData(SearchTimeSheetApiDto dto) {
        if (dto == null || dto.getFromdate() == null || dto.getTodate() == null)
            return null;

        String templatePath = "Excel/DU_LIEU_CHAM_CONG_LA.xlsx";

        long startTime = System.nanoTime();

        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(templatePath)) {
            if (fileInputStream == null) {
                throw new IOException("File '" + templatePath + "' không tìm thấy trong classpath");
            }

            SystemConfigDto urlConfig = systemConfigService.getByKeyCode(HrConstants.HR_URL_TIMESHEET);
            if (urlConfig == null || !StringUtils.hasText(urlConfig.getConfigValue())) return null;

            String urlAPITimeSheet = urlConfig.getConfigValue();
            dto.setUrl(urlAPITimeSheet);

            TimeSheetResponseDto result = RestApiUtils.postApi(dto.getUrl(), dto);
            if (result == null || result.getTable1() == null) return null;


            Workbook workbook = new XSSFWorkbook(fileInputStream);


            Sheet timekeepingData = workbook.getSheetAt(0);
            CellStyle dataCellStyle = ExportExcelUtil.createDataCellStyle(workbook);

            int rowIndex = 1;
            int orderNumber = 1;

            for (TimeSheetRecordDto timekeepingItem : result.getTable1()) {
                if (timekeepingItem == null)
                    continue;

                Row dataRow = timekeepingData.createRow(rowIndex);
                int cellIndex = 0;

                // 1. STT
                ExportExcelUtil.createCell(dataRow, cellIndex++, orderNumber, dataCellStyle);
                orderNumber++;

                // 2. Mã chấm công
                ExportExcelUtil.createCell(dataRow, cellIndex++, timekeepingItem.getMaChamCong(), dataCellStyle);

                // 3. Mã nhân viên
                ExportExcelUtil.createCell(dataRow, cellIndex++, timekeepingItem.getMaNhanVien(), dataCellStyle);

                // 4. Tên nhân viên
                ExportExcelUtil.createCell(dataRow, cellIndex++, timekeepingItem.getTenNhanVien(), dataCellStyle);

                // 5. Ngày chấm
//                    String recruitmentDate = (timekeepingItem.get() != null)
//                            ? formatDate(staff.getRecruitmentDate())
//                            : "";
//                    ExportExcelUtil.createCell(dataRow, cellIndex++, recruitmentDate, dataCellStyle);
                ExportExcelUtil.createCell(dataRow, cellIndex++, timekeepingItem.getNgayCham(), dataCellStyle);

                // 6. Giờ chấm
                ExportExcelUtil.createCell(dataRow, cellIndex++, timekeepingItem.getGioCham(), dataCellStyle);

                // 7. Tên phòng ban
                ExportExcelUtil.createCell(dataRow, cellIndex++, timekeepingItem.getTenPhongBan(), dataCellStyle);


                // thêm dòng tiếp theo
                rowIndex++;
            }


            long endTime = System.nanoTime();
            long elapsedTimeMs = (endTime - startTime) / 1_000_000;

            logger.info("Xuất dữ liệu chấm công LA - Xử lý mất {} ms ", elapsedTimeMs);

            return workbook;


        } catch (Exception exception) {
            // TODO Auto-generated catch block
            exception.printStackTrace();
        }

        return null;
    }

    @Override
    public SearchTimeSheetDto getInitialFilter() {
        SearchTimeSheetDto response = new SearchTimeSheetDto();

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

}
