package com.globits.timesheet.service.impl;

import com.globits.core.dto.PersonDto;
import com.globits.core.service.impl.GenericServiceImpl;

import com.globits.hr.HrConstants;
import com.globits.hr.data.types.TimeSheetRegStatus;
import com.globits.hr.domain.*;
import com.globits.hr.dto.*;
import com.globits.hr.repository.ShiftWorkTimePeriodRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.repository.StaffWorkScheduleRepository;
import com.globits.hr.repository.TimeSheetStaffRepository;
import com.globits.hr.repository.WorkingStatusRepository;
import com.globits.hr.service.*;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.hr.utils.RoleUtils;
import com.globits.keycloak.auth.utils.Constants;
import com.globits.salary.repository.SalaryPeriodRepository;
import com.globits.salary.service.SalaryResultStaffItemService;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;
import com.globits.timesheet.domain.*;
import com.globits.timesheet.dto.*;
import com.globits.timesheet.dto.TimeSheetStaffDto;
import com.globits.timesheet.dto.search.SearchTimeSheetDto;
import com.globits.timesheet.repository.*;
import com.globits.timesheet.service.ProjectActivityService;
import com.globits.timesheet.service.TimeSheetService;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.io.IOException;
import java.io.InputStream;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Transactional
@Service
public class TimeSheetServiceImpl extends GenericServiceImpl<TimeSheet, UUID> implements TimeSheetService {
    private static final Logger logger = LoggerFactory.getLogger(TimeSheetServiceImpl.class);

    @PersistenceContext
    EntityManager manager;

    @Autowired
    TimeSheetRepository timeSheetRepository;

    @Autowired
    TimeSheetStaffRepository timeSheetStaffRepository;

    @Autowired
    WorkingStatusRepository workingStatusRepository;

    @Autowired
    WorkingStatusRepository workingstatusRepository;

    @Autowired
    StaffRepository staffRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectActivityService projectActivityService;

    @Autowired
    ProjectActivityRepository projectActivityRepository;

    @Autowired
    private UserExtService userExtService;

    @Autowired
    private TimeSheetShiftWorkPeriodRepository timeSheetShiftWorkPeriodRepository;

    @Autowired
    private ShiftWorkTimePeriodRepository shiftWorkTimePeriodRepository;

    @Autowired
    private StaffService staffService;

    @Autowired
    private TimeSheetDetailRepository timeSheetDetailRepository;

    @Autowired
    private StaffWorkScheduleRepository staffWorkScheduleRepository;

    @Autowired
    private StaffWorkScheduleService staffWorkScheduleService;

    @Autowired
    private SalaryPeriodRepository salaryPeriodRepository;

    @Autowired
    private SalaryResultStaffItemService salaryResultStaffItemService;

    @Autowired
    private CalculateStaffWorkTimeService calculateStaffWorkTimeService;

    @Autowired
    private CalculateStaffWorkTimeServiceV2 calculateStaffWorkTimeServiceV2;

    @Autowired
    private HrDepartmentIpService hrDepartmentIpService;

    public TimeSheet setEntityValue(TimeSheetDto dto, TimeSheet entity, UserDto user, Boolean isRoleUser) {
        String currentUserName = "Unknow User";
        LocalDateTime currentDate = LocalDateTime.now();
        if (entity == null) {
            entity = new TimeSheet();
            entity.setCreateDate(currentDate);
        }
        entity.setEndTime(dto.getEndTime());
        entity.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null && dto.getStartTime() != null) {
            double totalTime = (double) dto.getEndTime().getTime() - (double) dto.getStartTime().getTime();
            double totalHours = totalTime / (60 * 60 * 1000);
            entity.setTotalHours(Math.floor(totalHours * 10) / 10);
        }

        entity.setTotalHours(dto.getTotalHours());
        entity.setWorkingDate(dto.getWorkingDate());
        entity.setPriority(dto.getPriority());
        entity.setDescription(dto.getDescription());
        entity.setYear(dto.getYear());
        entity.setMonth(dto.getMonth());
        entity.setDay(dto.getDay());
        if (dto.getApproveStatus() == null) {
            dto.setApproveStatus(0);
        } else {
            entity.setApproveStatus(dto.getApproveStatus());
        }

        if (dto.getWorkingStatus() != null) {
            Optional<WorkingStatus> workingStatusOptional = workingStatusRepository
                    .findById(dto.getWorkingStatus().getId());
            if (workingStatusOptional.isPresent()) {
                WorkingStatus workingStatus = workingStatusOptional.get();
                entity.setWorkingStatus(workingStatus);
            }

        }
        if (dto.getStaff() != null) {
            Optional<Staff> staffOptionall = staffRepository
                    .findById(dto.getStaff().getId());
            if (staffOptionall.isPresent()) {
                Staff staff = staffOptionall.get();
                entity.setStaff(staff);
            }

        }

        List<StaffDto> timeSheetStaffSet = dto.getTimeSheetStaff();
        if (timeSheetStaffSet != null && !timeSheetStaffSet.isEmpty()) {
            HashSet<TimeSheetStaff> timeSheetStaffHash = new HashSet<>();
            for (StaffDto item : timeSheetStaffSet) {
                TimeSheetStaff timeSheetStaff = new TimeSheetStaff();
                Staff staff = null;
                if (item != null && item.getId() != null) {
                    Optional<Staff> staffOptional = staffRepository.findById(item.getId());
                    if (staffOptional.isPresent()) {
                        staff = staffOptional.get();
                    }
                }
                timeSheetStaff.setTimesheet(entity);
                if (staff != null) {
                    timeSheetStaff.setStaff(staff);
                }
                timeSheetStaffHash.add(timeSheetStaff);
            }
            if (entity.getTimeSheetStaffSet() != null) {
                entity.getTimeSheetStaffSet().clear();
                entity.getTimeSheetStaffSet().addAll(timeSheetStaffHash);
            } else {
                entity.setTimeSheetStaffSet(timeSheetStaffHash);
            }
        } else if (timeSheetStaffSet != null) {
            if (entity.getTimeSheetStaffSet() != null) {
                entity.getTimeSheetStaffSet().clear();
            }
        } else {
            if (user != null && isRoleUser) {
                PersonDto person = user.getPerson();
                if (person != null) {
                    TimeSheetStaff timeSheetStaff = new TimeSheetStaff();
                    Staff staff = staffRepository.getOne(person.getId());
                    timeSheetStaff.setTimesheet(entity);
                    timeSheetStaff.setStaff(staff);
                    entity.getTimeSheetStaffSet().add(timeSheetStaff);
                }
            }
        }
        Project project = null;
        if (dto.getProject() != null && dto.getProject().getId() != null) {
            Optional<Project> optional = projectRepository.findById(dto.getProject().getId());
            if (optional.isPresent()) {
                project = optional.get();
                entity.setProject(project);
            }
        }
        ProjectActivity activity = null;
        if (dto.getActivity() != null) {
            if (dto.getActivity().getId() != null) {
                Optional<ProjectActivity> optional = projectActivityRepository.findById(dto.getActivity().getId());
                if (optional.isPresent()) {
                    activity = optional.get();
                }
            }
            if (activity == null) {
                activity = new ProjectActivity();
            }
            activity.setCode(dto.getActivity().getCode());
            activity.setName(dto.getActivity().getName());
            activity.setProject(project);
            activity = projectActivityRepository.save(activity);
        }

        Set<TimeSheetDetail> details = new HashSet<>();
        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            for (TimeSheetDetailDto detailDto : dto.getDetails()) {
                TimeSheetDetail detail = null;
                if (detailDto != null && detailDto.getId() != null) {
                    detail = timeSheetDetailRepository.getOne(detailDto.getId());
                }
                if (detail == null) {
                    detail = new TimeSheetDetail();
                    detail.setTimeSheet(entity);
                    detail.setCreateDate(currentDate);
                    detail.setCreatedBy(currentUserName);
                }
                if (detailDto != null) {
                    detail = detailDto.toEntity(detailDto, detail);
                    if (detailDto.getEmployee() != null) {
                        Staff staff = null;
                        Optional<Staff> staffOptional = staffRepository.findById(detailDto.getEmployee().getId());
                        if (staffOptional.isPresent()) {
                            staff = staffOptional.get();
                        }
                        detail.setEmployee(staff);
                    }
                }
                details.add(detail);
            }
        }
        if (entity.getDetails() != null) {
            entity.getDetails().clear();
            entity.getDetails().addAll(details);
        } else {
            entity.setDetails(details);
        }
        Set<TimeSheetShiftWorkPeriod> timeSheetShiftWorkPeriods = new HashSet<>();
        if (dto.getTimeSheetShiftWorkPeriod() != null && dto.getTimeSheetShiftWorkPeriod().size() > 0) {
            for (TimeSheetShiftWorkPeriodDto timeSheetShiftWorkPeriodDto : dto.getTimeSheetShiftWorkPeriod()) {
                if (timeSheetShiftWorkPeriodDto != null) {
                    TimeSheetShiftWorkPeriod timeSheetShiftWorkPeriod = null;
                    if (timeSheetShiftWorkPeriodDto.getId() != null) {
                        timeSheetShiftWorkPeriod = timeSheetShiftWorkPeriodRepository
                                .getOne(timeSheetShiftWorkPeriodDto.getId());
                    }
                    if (timeSheetShiftWorkPeriod == null) {
                        timeSheetShiftWorkPeriod = new TimeSheetShiftWorkPeriod();
                    }
                    if (timeSheetShiftWorkPeriodDto.getShiftWorkTimePeriod() != null
                            && timeSheetShiftWorkPeriodDto.getShiftWorkTimePeriod().getId() != null) {
                        ShiftWorkTimePeriod shiftWorkTimePeriod = null;
                        Optional<ShiftWorkTimePeriod> optional = shiftWorkTimePeriodRepository
                                .findById(timeSheetShiftWorkPeriodDto.getShiftWorkTimePeriod().getId());
                        if (optional.isPresent()) {
                            shiftWorkTimePeriod = optional.get();
                        }
                        timeSheetShiftWorkPeriod.setShiftWorkTimePeriod(shiftWorkTimePeriod);
                    }
                    if (timeSheetShiftWorkPeriodDto.getTimeSheet() != null
                            && timeSheetShiftWorkPeriodDto.getTimeSheet().getId() != null) {
                        TimeSheet timeSheet = null;
                        Optional<TimeSheet> optional = timeSheetRepository
                                .findById(timeSheetShiftWorkPeriodDto.getTimeSheet().getId());
                        if (optional.isPresent()) {
                            timeSheet = optional.get();
                        }
                        timeSheetShiftWorkPeriod.setTimeSheet(timeSheet);
                    }
                    timeSheetShiftWorkPeriod.setNote(timeSheetShiftWorkPeriodDto.getNote());
                    timeSheetShiftWorkPeriod.setWorkingFormat(timeSheetShiftWorkPeriodDto.getWorkingFormat());
                    timeSheetShiftWorkPeriods.add(timeSheetShiftWorkPeriod);
                }
            }
        }
        if (timeSheetShiftWorkPeriods.size() > 0) {
            if (entity.getTimeSheetShiftWorkPeriod() == null) {
                entity.setTimeSheetShiftWorkPeriod(timeSheetShiftWorkPeriods);
            } else {
                entity.getTimeSheetShiftWorkPeriod().clear();
                entity.getTimeSheetShiftWorkPeriod().addAll(timeSheetShiftWorkPeriods);
            }
        } else {
            if (entity.getTimeSheetShiftWorkPeriod() != null) {
                entity.getTimeSheetShiftWorkPeriod().clear();
            }
        }

        List<LabelDto> labelSet = dto.getLabels();
        if (labelSet != null && !labelSet.isEmpty()) {
            HashSet<TimeSheetLabel> timeSheetLabels = new HashSet<>();
            for (LabelDto item : labelSet) {
                TimeSheetLabel timeSheetLabel = new TimeSheetLabel();
                Label label = new Label();
                if (item != null && item.getId() != null) {
                    Optional<Label> staffOptional = labelRepository.findById(item.getId());
                    if (staffOptional.isPresent()) {
                        label = staffOptional.get();
                    }
                }
                timeSheetLabel.setTimesheet(entity);
                if (label != null) {
                    timeSheetLabel.setLabel(label);
                }
                timeSheetLabels.add(timeSheetLabel);
            }
            if (entity.getLabels() != null) {
                entity.getLabels().clear();
                entity.getLabels().addAll(timeSheetLabels);
            } else {
                entity.setLabels(timeSheetLabels);
            }
        } else if (labelSet != null) {
            if (entity.getLabels() != null) {
                entity.getLabels().clear();
            }
        }
        entity.setActivity(activity);
        return entity;
    }

    @Override
    public TimeSheetDto findTimeSheetById(UUID id) {
        TimeSheet entity = this.getEntityById(id);
        if (entity != null) {
            return new TimeSheetDto(entity);
        }
        return null;
    }

    @Override
    public TimeSheet getEntityById(UUID id) {
        TimeSheet entity = null;
        Optional<TimeSheet> optional = timeSheetRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        return entity;
    }

    @Override
    public Boolean deleteTimeSheetById(UUID id) {
        try {
            timeSheetRepository.deleteById(id);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public Boolean deleteTimeSheets(List<TimeSheetDto> list) {
        try {
            ArrayList<TimeSheet> entities = new ArrayList<>();
            if (list != null) {
                for (TimeSheetDto timeSheetDto : list) {
                    if (timeSheetDto != null && timeSheetDto.getId() != null) {
                        TimeSheet ts = timeSheetRepository.getOne(timeSheetDto.getId());
                        entities.add(ts);
                    }
                }
            }
            timeSheetRepository.deleteInBatch(entities);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
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
    public Page<TimeSheetDto> getAllByWorkingDate(Date workDate, int pageIndex, int pageSize) {
        String sqlCount = "select count(sw) FROM TimeSheet as sw WHERE sw.workingDate=:workDate";
        String sql = "select new com.globits.timesheet.dto.TimeSheetDto(sw) FROM TimeSheet as sw WHERE sw.workingDate=:workDate";

        Query query = manager.createQuery(sql, TimeSheetDto.class);
        Query qCount = manager.createQuery(sqlCount);
        query.setParameter("workDate", workDate);
        qCount.setParameter("workDate", workDate);
        int startPosition = (pageIndex - 1) * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        List<TimeSheetDto> entities = query.getResultList();
        long count = (long) qCount.getSingleResult();
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public Page<TimeSheetDetailDto> getTimeSheetDetailByTimeSheetID(UUID id, int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        return timeSheetRepository.findTimeSheetDetailByTimeSheetId(id, pageable);
    }

    @Override
    public Boolean confirmTimeSheets(List<TimeSheetDto> list) {
        ArrayList<TimeSheet> entities = new ArrayList<>();
        try {
            for (TimeSheetDto timeSheetDto : list) {
                if (timeSheetDto != null && timeSheetDto.getId() != null) {
                    TimeSheet entity = timeSheetRepository.getOne(timeSheetDto.getId());
                    entity.setApproveStatus(1);
                    entities.add(entity);
                }
            }
            timeSheetRepository.saveAll(entities);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public Page<TimeSheetDto> findPageByStaff(String textSearch, int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        return timeSheetRepository.findPageByCodeOrName(textSearch, pageable);
    }

    @Override
    public Page<TimeSheetDto> searchByDto(SearchTimeSheetDto dto, int pageIndex, int pageSize) {
        if (dto.getFromDate() != null && dto.getToDate() != null && dto.getToDate().before(dto.getFromDate())) {
            return null;
        }
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        if (dto.getCodeAndName() != null) {
            if (dto.getWorkingDate() != null) {
                return timeSheetRepository.findPageByCodeAndNameAndDate(dto.getCodeAndName(),
                        dto.getWorkingDate(), pageable);
            } else {
                return timeSheetRepository.findPageByCodeOrName(dto.getCodeAndName(), pageable);
            }
        } else if (dto.getWorkingDate() != null) {
            return timeSheetRepository.findPageByDate(dto.getWorkingDate(), pageable);
        } else {
            return timeSheetRepository.getListPage(pageable);
        }
    }

    @Override
    public Page<SynthesisReportOfStaffDto> reportWorkingStatus(SearchReportDto searchReportDto, int pageIndex,
                                                               int pageSize) {

        return null;
    }

    //
    @Override
    public Page<TimeSheetDto> searchByPage(SearchTimeSheetDto dto) {

        if (dto == null) {
            return null;
        }
        if (dto.getFromDate() != null && dto.getToDate() != null && dto.getToDate().before(dto.getFromDate())) {
            return null;
        }
        if (dto.getFromDate() != null) {
            dto.setFromDate(DateTimeUtil.getStartOfDay(dto.getFromDate()));
        }
        if (dto.getToDate() != null) {
            dto.setToDate(DateTimeUtil.getEndOfDay(dto.getToDate()));
        }
        boolean isRoleUser = false;
        boolean isRoleAdmin = false;
        boolean isRoleManager = false;
        UserDto user = userExtService.getCurrentUser();
        if (user != null && user.getRoles() != null && user.getRoles().size() > 0) {
            for (RoleDto item : user.getRoles()) {
                if (item.getName() != null && "ROLE_ADMIN".equals(item.getName())) {
                    isRoleAdmin = true;
                }
                if (item.getName() != null && "HR_MANAGER".equals(item.getName())) {
                    isRoleManager = true;
                }
                if (item.getName() != null && "HR_USER".equals(item.getName())) {
                    isRoleUser = true;
                }
            }
        }
        if (isRoleAdmin) {
            isRoleManager = false;
            isRoleUser = false;
        } else {
            if (isRoleManager) {
                isRoleUser = false;
            }
        }
        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();
        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }

        String whereClause = "";
        String orderBy = " ORDER BY entity.startTime DESC";

        String sqlCount = "select count(entity.id) from TimeSheet as entity where (1=1)   ";
        String sql = "select new com.globits.timesheet.dto.TimeSheetDto(entity) from TimeSheet entity where (1=1) ";

        if (dto.getProjectId() != null && StringUtils.hasText(dto.getProjectId().toString())) {
            whereClause += " AND ( entity.project.id  =: projectId ) ";
        }

        if (isRoleUser && user.getUsername() != null && dto.getStaffId() == null) {
            // sqlCount = "select count(entity.id) from TimeSheet entity, Staff s
            // ,TimeSheetStaff ts where (s.id = ts.staff.id and entity.id = ts.timesheet.id)
            // ";
            // sql = "select new com.globits.timesheet.dto.TimeSheetDto(entity, false) from
            // TimeSheet entity, Staff s , TimeSheetStaff ts where s.id = ts.staff.id and
            // entity.id = ts.timesheet.id ";
            // whereClause += " and ( s.staffCode = :staffCode ) ";
            whereClause += " and ( entity.staff.user.username = :staffCode ) ";
        }
        if (dto.getStaffId() != null && StringUtils.hasText(dto.getStaffId().toString())) {
            // sqlCount = "select count(entity.id) from TimeSheet entity, Staff s
            // ,TimeSheetStaff ts where (s.id = ts.staff.id and entity.id = ts.timesheet.id)
            // ";
            // sql = "select new com.globits.timesheet.dto.TimeSheetDto(entity, false) from
            // TimeSheet entity, Staff s , TimeSheetStaff ts where s.id = ts.staff.id and
            // entity.id = ts.timesheet.id ";
            whereClause += " AND ( entity.staff.id  =: staffId ) ";
        }
        if (dto.getWorkingStatusId() != null && StringUtils.hasText(dto.getWorkingStatusId().toString())) {
            whereClause += " AND ( entity.workingStatus.id  =: workingStatusId ) ";
        }
        if (dto.getFromDate() != null) {

            whereClause += " AND ( entity.startTime >= :fromDate ) ";
        }
        if (dto.getToDate() != null) {
            whereClause += " AND ( entity.endTime <= :toDate ) ";
        }
        if (dto.getPriority() != null) {
            whereClause += " AND ( entity.priority = :priority ) ";
        }
        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, TimeSheetDto.class);
        Query qCount = manager.createQuery(sqlCount);
        if (dto.getFromDate() != null) {
            query.setParameter("fromDate", dto.getFromDate());
            qCount.setParameter("fromDate", dto.getFromDate());
        }
        if (dto.getToDate() != null) {
            query.setParameter("toDate", dto.getToDate());
            qCount.setParameter("toDate", dto.getToDate());
        }
        if (dto.getProjectId() != null && StringUtils.hasText(dto.getProjectId().toString())) {
            query.setParameter("projectId", dto.getProjectId());
            qCount.setParameter("projectId", dto.getProjectId());
        }

        if (dto.getStaffId() != null && StringUtils.hasText(dto.getStaffId().toString())) {
            query.setParameter("staffId", dto.getStaffId());
            qCount.setParameter("staffId", dto.getStaffId());
        }
        if (dto.getWorkingStatusId() != null && StringUtils.hasText(dto.getWorkingStatusId().toString())) {
            query.setParameter("workingStatusId", dto.getWorkingStatusId());
            qCount.setParameter("workingStatusId", dto.getWorkingStatusId());
        }
        if (dto.getPriority() != null) {
            query.setParameter("priority", dto.getPriority());
            qCount.setParameter("priority", dto.getPriority());
        }
        if (isRoleUser && user != null && user.getUsername() != null && dto.getStaffId() == null) {
            query.setParameter("staffCode", user.getUsername());
            qCount.setParameter("staffCode", user.getUsername());
        }

        if (dto.getIsExportExcel()) {
            List<TimeSheetDto> listExportExcel = query.getResultList();
            return new PageImpl<>(listExportExcel);
        }

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        List<TimeSheetDto> entities = query.getResultList();
        long count = (long) qCount.getSingleResult();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public TimeSheetDto saveOrUpdate(UUID id, TimeSheetDto dto) {
        if (dto != null && dto.getStartTime().before(dto.getEndTime())) {
            if (dto.getActivity().getName().length() < 1) {
                return null;
            }
            if (dto.getDetails() != null && dto.getTimeSheetStaff().size() == 1) {
                for (TimeSheetDetailDto detailDto : dto.getDetails()) {
                    if (detailDto.getEmployee() == null) {
                        detailDto.setEmployee(dto.getTimeSheetStaff().get(0));
                    }
                }
            }
            TimeSheet entity = null;
            boolean isRoleUser = false;
            boolean isRoleAdmin = false;
            boolean isRoleManager = false;
            UserDto user = userExtService.getCurrentUser();
            if (user != null && user.getRoles() != null && user.getRoles().size() > 0) {
                for (RoleDto item : user.getRoles()) {
                    if (item.getName() != null && "ROLE_ADMIN".equals(item.getName())) {
                        isRoleAdmin = true;
                    }
                    if (item.getName() != null && "HR_MANAGER".equals(item.getName())) {
                        isRoleManager = true;
                    }
                    if (item.getName() != null && "HR_USER".equals(item.getName())) {
                        isRoleUser = true;
                    }
                }
            }
            if (isRoleAdmin) {
                isRoleManager = false;
                isRoleUser = false;
            } else {
                if (isRoleManager) {
                    isRoleUser = false;
                }
            }
            boolean checkStaffIsUser = false;
            if (dto.getTimeSheetStaff() != null && !dto.getTimeSheetStaff().isEmpty()) {
                for (StaffDto staffDto : dto.getTimeSheetStaff()) {
                    if (user != null && user.getUsername().equalsIgnoreCase(staffDto.getStaffCode())) {
                        checkStaffIsUser = true;
                        break;
                    }
                }
            }
            if (dto.getId() != null) {
                if (!dto.getId().equals(id)) {
                    return null;
                }
                Optional<TimeSheet> projectOptional = timeSheetRepository.findById(id);
                if (projectOptional.isPresent()) {
                    entity = projectOptional.get();
                    if (isRoleUser && !checkStaffIsUser) {
                        return null;
                    }
                }
                if (entity != null) {
                    entity.setModifyDate(LocalDateTime.now());
                }
            }
            if (entity == null) {
                entity = new TimeSheet();
                entity.setCreateDate(LocalDateTime.now());
                entity.setModifyDate(LocalDateTime.now());
            }

            entity = setEntityValue(dto, entity, user, isRoleUser);
            entity = timeSheetRepository.save(entity);
            return new TimeSheetDto(entity);
        }
        return null;
    }

    @Override
    public String updateStatus(UUID id, UUID workingStatusId) {
        if (id != null) {
            Optional<TimeSheet> optional = timeSheetRepository.findById(id);
            TimeSheet entity = null;
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
                timeSheetRepository.save(entity);
                return "success";
            }
        }
        return null;
    }

    @Override
    public List<TimeSheetDto> getListTimeSheetByProjectActivityId(UUID id) {
        if (id != null) {
            return timeSheetRepository.getListTimeSheetByProjectActivityId(id);
        }
        return null;
    }

    public TimeSheetDto timekeepingService(TimekeepingItemDto dto, UUID id) {
        boolean isRoleAdmin = false;
        UserDto user = userExtService.getCurrentUser();
        if (user != null && user.getRoles() != null && user.getRoles().size() > 0) {
            for (RoleDto item : user.getRoles()) {
                if (item.getName() != null
                        && ("ROLE_ADMIN".equals(item.getName()) || "ROLE_SUPER_ADMIN".equals(item.getName()))) {
                    isRoleAdmin = true;
                }
            }
        }
        if (dto != null) {
            StaffDto staffDto = userExtService.getCurrentStaff();
            if (staffDto == null) {
                return null;
            }
            if (!isRoleAdmin) {
                Date nowDate = new Date();
                Date startTimeDate = DateTimeUtil.getStartOfDay(nowDate);
                Date endTimeDate = DateTimeUtil.getEndOfDay(nowDate);
                if (dto.getWorkingDate() != null && dto.getWorkingDate().before(startTimeDate)
                        || dto.getWorkingDate().after(endTimeDate)) {
                    return null;
                }
            }
            TimeSheet timeSheet = null;
            if (dto.getTimeSheetId() != null) {
                Optional<TimeSheet> timeSheetOptional = timeSheetRepository.findById(dto.getTimeSheetId());
                if (timeSheetOptional.isPresent()) {
                    timeSheet = timeSheetOptional.get();
                }
            }
            if (dto.getTimeSheetId() == null) {
                timeSheet = new TimeSheet();
                timeSheet.setCreateDate(LocalDateTime.now());
                timeSheet.setModifyDate(LocalDateTime.now());

            }
            Staff staff = null;
            Optional<Staff> staffOptional = staffRepository.findById(staffDto.getId());
            if (staffOptional.isPresent()) {
                staff = staffOptional.get();
                timeSheet.setStaff(staff);
            }
            if (dto.getStaffId() != null && isRoleAdmin) {
                Optional<Staff> staffTimeSheet = staffRepository.findById(dto.getStaffId());
                if (staffTimeSheet.isPresent()) {
                    staff = staffTimeSheet.get();
                    timeSheet.setStaff(staff);
                }
            }

            Set<TimeSheetShiftWorkPeriod> timeSheetShiftWorkPeriods = new HashSet<>();
            if (dto.getTimeSheetShiftWorkPeriods() != null && dto.getTimeSheetShiftWorkPeriods().size() > 0) {
                for (TimeSheetShiftWorkPeriodDto timeSheetShiftWorkPeriodDto : dto.getTimeSheetShiftWorkPeriods()) {
                    if (timeSheetShiftWorkPeriodDto != null) {
                        TimeSheetShiftWorkPeriod timeSheetShiftWorkPeriod = null;
                        if (timeSheetShiftWorkPeriodDto.getId() != null) {
                            timeSheetShiftWorkPeriod = timeSheetShiftWorkPeriodRepository
                                    .getOne(timeSheetShiftWorkPeriodDto.getId());
                        }
                        if (timeSheetShiftWorkPeriod == null) {
                            timeSheetShiftWorkPeriod = new TimeSheetShiftWorkPeriod();
                        }
                        timeSheetShiftWorkPeriod.setWorkingFormat(timeSheetShiftWorkPeriodDto.getWorkingFormat());
                        if (timeSheetShiftWorkPeriodDto.getShiftWorkTimePeriod() != null
                                && timeSheetShiftWorkPeriodDto.getShiftWorkTimePeriod().getId() != null) {
                            ShiftWorkTimePeriod shiftWorkTimePeriod = null;
                            Optional<ShiftWorkTimePeriod> optional = shiftWorkTimePeriodRepository
                                    .findById(timeSheetShiftWorkPeriodDto.getShiftWorkTimePeriod().getId());
                            if (optional.isPresent()) {
                                shiftWorkTimePeriod = optional.get();
                            }

                            timeSheetShiftWorkPeriod.setShiftWorkTimePeriod(shiftWorkTimePeriod);
                        }
                        timeSheetShiftWorkPeriod.setTimeSheet(timeSheet);
                        timeSheetShiftWorkPeriod.setNote(timeSheetShiftWorkPeriodDto.getNote());
                        timeSheetShiftWorkPeriods.add(timeSheetShiftWorkPeriod);
                        timeSheetShiftWorkPeriod.setRegStatus(TimeSheetRegStatus.REG);
                    }
                }
            }
            Date minDate = null;
            Date maxDate = null;
            for (TimeSheetShiftWorkPeriod timeSheetShiftWorkPeriod : timeSheetShiftWorkPeriods) {
                if (timeSheetShiftWorkPeriod != null && timeSheetShiftWorkPeriod.getShiftWorkTimePeriod() != null) {
                    if (timeSheetShiftWorkPeriod.getWorkingFormat() >= 0) {
                        Date start = timeSheetShiftWorkPeriod.getShiftWorkTimePeriod().getStartTime();
                        Date end = timeSheetShiftWorkPeriod.getShiftWorkTimePeriod().getEndTime();
                        if (minDate == null) {
                            minDate = start;
                        } else if (minDate.after(start)) {
                            minDate = start;
                        }
                        if (maxDate == null) {
                            maxDate = end;
                        } else if (maxDate.before(end)) {
                            maxDate = end;
                        }
                    }
                }
            }
            if (maxDate != null) {
                int maxHours = DateTimeUtil.getHours(maxDate);
                int maxMinutes = DateTimeUtil.getMinutes(maxDate);
                timeSheet.setEndTime(DateTimeUtil.setTime(dto.getWorkingDate(), maxHours, maxMinutes, 0, 0));
            } else {
                timeSheet.setEndTime(DateTimeUtil.setTime(dto.getWorkingDate(), 0, 0, 0, 0));
            }
            if (minDate != null) {
                int minHours = DateTimeUtil.getHours(minDate);
                int minMinutes = DateTimeUtil.getMinutes(minDate);
                timeSheet.setStartTime(DateTimeUtil.setTime(dto.getWorkingDate(), minHours, minMinutes, 0, 0));
            } else {
                timeSheet.setStartTime(DateTimeUtil.setTime(dto.getWorkingDate(), 0, 0, 0, 0));
            }

            if (timeSheetShiftWorkPeriods.size() > 0) {
                if (timeSheet.getTimeSheetShiftWorkPeriod() == null) {
                    timeSheet.setTimeSheetShiftWorkPeriod(timeSheetShiftWorkPeriods);
                } else {
                    timeSheet.getTimeSheetShiftWorkPeriod().clear();
                    timeSheet.getTimeSheetShiftWorkPeriod().addAll(timeSheetShiftWorkPeriods);
                }
            } else {
                if (timeSheet.getTimeSheetShiftWorkPeriod() != null) {
                    timeSheet.getTimeSheetShiftWorkPeriod().clear();
                }
            }
            timeSheet.setWorkingDate(dto.getWorkingDate());
            timeSheet.setYear(DateTimeUtil.getYear(dto.getWorkingDate()));
            timeSheet.setMonth(DateTimeUtil.getMonth(dto.getWorkingDate()));
            timeSheet.setDay(DateTimeUtil.getDay(dto.getWorkingDate()));
            timeSheet.setRegStatus(TimeSheetRegStatus.REG);
            timeSheet = timeSheetRepository.save(timeSheet);
            return new TimeSheetDto(timeSheet);
        }
        return null;
    }

    @Override

    public TimekeepingItemDto doTimekeeping(TimekeepingItemDto dto, UUID id) {
        boolean isRoleManager = false;
        UserDto currentUser = userExtService.getCurrentUser();

        // Determine if the current user has manager/admin roles
        if (currentUser != null && currentUser.getRoles() != null) {
            for (RoleDto role : currentUser.getRoles()) {
                if ("ROLE_ADMIN".equals(role.getName()) || "ROLE_SUPER_ADMIN".equals(role.getName())
                        || "HR_MANAGER".equals(role.getName())) {
                    isRoleManager = true;
                    break;
                }
            }
        }

        if (dto == null) {
            return null;
        }

        // Restrict non-admin users to today's date only
        if (!isRoleManager) {
            Date now = new Date();
            Date startOfDay = DateTimeUtil.getStartOfDay(now);
            Date endOfDay = DateTimeUtil.getEndOfDay(now);

            if (dto.getWorkingDate() == null || dto.getWorkingDate().before(startOfDay)
                    || dto.getWorkingDate().after(endOfDay)) {
                return null;
            }
        }

        // Find the staff member
        Staff staff = null;
        if (isRoleManager) {
            // Admins can manage attendance for others
            if (dto.getStaffId() != null) {
                staff = staffRepository.findById(dto.getStaffId()).orElse(null);
            }
            if (staff == null && dto.getStaffCode() != null) {
                List<Staff> staffs = staffRepository.findByCode(dto.getStaffCode());
                if (!staffs.isEmpty()) {
                    staff = staffs.get(0);
                }
            }
        } else {
            // Non-admin users can only manage their own attendance
            StaffDto staffDto = userExtService.getCurrentStaff();
            if (staffDto != null) {
                staff = staffRepository.findById(staffDto.getId()).orElse(null);
            }
        }

        // If no staff found, return null
        if (staff == null) {
            return null;
        }

        // Find or create a time sheet
        TimeSheet timeSheet = null;
        if (dto.getTimeSheetId() != null) {
            timeSheet = timeSheetRepository.findById(dto.getTimeSheetId()).orElse(null);
        }
        if (timeSheet == null) {
            List<TimeSheet> timeSheets = timeSheetRepository.getTimeSheetByWorkingDate(
                    staff.getId(),
                    DateTimeUtil.getDay(dto.getWorkingDate()),
                    DateTimeUtil.getMonth(dto.getWorkingDate()),
                    DateTimeUtil.getYear(dto.getWorkingDate()));
            if (!timeSheets.isEmpty()) {
                timeSheet = timeSheets.get(0);
            }
        }
        if (timeSheet == null) {
            timeSheet = new TimeSheet();
        }

        timeSheet.setStaff(staff);

        // Process shift work periods
        Set<TimeSheetShiftWorkPeriod> timeSheetShiftWorkPeriods = new HashSet<>();
        if (dto.getTimeSheetShiftWorkPeriods() != null) {
            for (TimeSheetShiftWorkPeriodDto shiftDto : dto.getTimeSheetShiftWorkPeriods()) {
                if (shiftDto == null)
                    continue;

                TimeSheetShiftWorkPeriod shiftWorkPeriod = null;
                if (shiftDto.getId() != null) {
                    shiftWorkPeriod = timeSheetShiftWorkPeriodRepository.findById(shiftDto.getId()).orElse(null);
                }
                if (shiftWorkPeriod == null) {
                    shiftWorkPeriod = new TimeSheetShiftWorkPeriod();
                }

                shiftWorkPeriod.setWorkingFormat(shiftDto.getWorkingFormat());

                // Find or create shift work time period
                ShiftWorkTimePeriod shiftWorkTimePeriod = null;
                if (shiftDto.getShiftWorkTimePeriod() != null) {
                    shiftWorkTimePeriod = shiftWorkTimePeriodRepository
                            .findById(shiftDto.getShiftWorkTimePeriod().getId()).orElse(null);
                }
                if (shiftWorkTimePeriod == null && shiftDto.getCode() != null) {
                    shiftWorkTimePeriod = shiftWorkTimePeriodRepository.findByCode(shiftDto.getCode());
                }
                shiftWorkPeriod.setShiftWorkTimePeriod(shiftWorkTimePeriod);
                shiftWorkPeriod.setTimeSheet(timeSheet);
                shiftWorkPeriod.setNote(shiftDto.getNote());
                shiftWorkPeriod.setRegStatus(TimeSheetRegStatus.REG);

                timeSheetShiftWorkPeriods.add(shiftWorkPeriod);
            }
        }

        // Calculate start and end times for the time sheet
        Date minStartTime = null;
        Date maxEndTime = null;
        for (TimeSheetShiftWorkPeriod shiftWorkPeriod : timeSheetShiftWorkPeriods) {
            if (shiftWorkPeriod.getShiftWorkTimePeriod() != null) {
                Date startTime = shiftWorkPeriod.getShiftWorkTimePeriod().getStartTime();
                Date endTime = shiftWorkPeriod.getShiftWorkTimePeriod().getEndTime();

                if (minStartTime == null || startTime.before(minStartTime)) {
                    minStartTime = startTime;
                }
                if (maxEndTime == null || endTime.after(maxEndTime)) {
                    maxEndTime = endTime;
                }
            }
        }

        // Set start and end times in the time sheet
        if (minStartTime != null) {
            timeSheet.setStartTime(DateTimeUtil.setTime(dto.getWorkingDate(), DateTimeUtil.getHours(minStartTime),
                    DateTimeUtil.getMinutes(minStartTime), 0, 0));
        } else {
            timeSheet.setStartTime(DateTimeUtil.setTime(dto.getWorkingDate(), 0, 0, 0, 0));
        }

        if (maxEndTime != null) {
            timeSheet.setEndTime(DateTimeUtil.setTime(dto.getWorkingDate(), DateTimeUtil.getHours(maxEndTime),
                    DateTimeUtil.getMinutes(maxEndTime), 0, 0));
        } else {
            timeSheet.setEndTime(DateTimeUtil.setTime(dto.getWorkingDate(), 0, 0, 0, 0));
        }

        // Update shift work periods in the time sheet
        if (!timeSheetShiftWorkPeriods.isEmpty()) {
            if (timeSheet.getTimeSheetShiftWorkPeriod() == null) {
                timeSheet.setTimeSheetShiftWorkPeriod(timeSheetShiftWorkPeriods);
            } else {
                timeSheet.getTimeSheetShiftWorkPeriod().clear();
                timeSheet.getTimeSheetShiftWorkPeriod().addAll(timeSheetShiftWorkPeriods);
            }
        } else if (timeSheet.getTimeSheetShiftWorkPeriod() != null) {
            timeSheet.getTimeSheetShiftWorkPeriod().clear();
        }

        // Update time sheet details
        timeSheet.setWorkingDate(dto.getWorkingDate());
        timeSheet.setYear(DateTimeUtil.getYear(dto.getWorkingDate()));
        timeSheet.setMonth(DateTimeUtil.getMonth(dto.getWorkingDate()));
        timeSheet.setDay(DateTimeUtil.getDay(dto.getWorkingDate()));
        timeSheet.setRegStatus(TimeSheetRegStatus.REG);

        // Save and return the time sheet
        timeSheet = timeSheetRepository.save(timeSheet);
        return new TimekeepingItemDto(timeSheet);
    }

    public List<TimeSheetDto> getTimeSheetByTime(TimekeepingItemDto dto) {
        String whereClause = "";
        String sql = "select new com.globits.timesheet.dto.TimeSheetDto(entity) from TimeSheet entity where (1=1) ";
        if (dto.getStaffId() != null) {
            whereClause += " AND ( entity.staff.id  =:staffId ) ";
        }
        if (dto.getWorkingDate() != null) {
            whereClause += "AND (entity.year =:year)";

            whereClause += "AND (entity.month =:month)";

            whereClause += "AND (entity.day =:day)";
        }
        sql += whereClause;
        Query query = manager.createQuery(sql, TimeSheetDto.class);
        if (dto.getStaffId() != null) {
            query.setParameter("staffId", dto.getStaffId());
        }
        if (dto.getWorkingDate() != null) {
            Integer year = DateTimeUtil.getYear(dto.getWorkingDate());
            Integer month = DateTimeUtil.getMonth(dto.getWorkingDate());
            Integer day = DateTimeUtil.getDay(dto.getWorkingDate());
            query.setParameter("year", year);
            query.setParameter("month", month);
            query.setParameter("day", day);
        }

        List<TimeSheetDto> timeSheets = query.getResultList();
        return timeSheets;
    }

    @Override
    public Boolean convertTimeSheet() {
        List<TimeSheet> timeSheets = timeSheetRepository.findAll();
        for (TimeSheet timeSheet : timeSheets) {
            convertTimeSheetDetail(timeSheet);
            convertStaffTimeSheet(timeSheet);
        }
        return true;
    }

    public TimeSheetDto convertStaffTimeSheet(TimeSheet timeSheet) {
        TimeSheetDto timeSheetDto = null;
        if (timeSheet.getTimeSheetStaffSet() != null && timeSheet.getTimeSheetStaffSet().size() > 0) {
            List<TimeSheetStaff> timeSheetStaffs = new ArrayList<>(timeSheet.getTimeSheetStaffSet());
            TimeSheetStaff timeSheetStaff = timeSheetStaffs.get(0);
            timeSheet.setStaff(timeSheetStaff.getStaff());
            timeSheet = timeSheetRepository.save(timeSheet);
        }
        if (timeSheet.getWorkingDate() != null) {
            timeSheet.setYear(DateTimeUtil.getYear(timeSheet.getWorkingDate()));
            timeSheet.setMonth(DateTimeUtil.getMonth(timeSheet.getWorkingDate()));
            timeSheet.setDay(DateTimeUtil.getDay(timeSheet.getWorkingDate()));
            timeSheet = timeSheetRepository.save(timeSheet);
        }
        timeSheetDto = new TimeSheetDto(timeSheet);
        return timeSheetDto;
    }

    public TimeSheetDetailDto convertTimeSheetToTimeSheetDetail(TimeSheet timeSheet) {
        TimeSheetDetailDto timeSheetDetailDto = null;
        if (timeSheet.getDetails() == null || timeSheet.getDetails().size() == 0) {
            TimeSheetDetail timeSheetDetail = new TimeSheetDetail();
            if (timeSheet.getTimeSheetStaffSet() != null && timeSheet.getTimeSheetStaffSet().size() > 0) {
                List<TimeSheetStaff> timeSheetStaffs = new ArrayList<>(timeSheet.getTimeSheetStaffSet());
                TimeSheetStaff timeSheetStaff = timeSheetStaffs.get(0);
                if (timeSheetStaff.getStaff() != null) {
                    timeSheetDetail.setEmployee(timeSheetStaff.getStaff());
                }
            }
            if (timeSheet.getProject() != null) {
                timeSheetDetail.setProject(timeSheet.getProject());
            }
            if (timeSheet.getActivity() != null) {
                timeSheetDetail.setActivity(timeSheet.getActivity());
            }
            if (timeSheet.getWorkingStatus() != null) {
                timeSheetDetail.setWorkingStatus(timeSheet.getWorkingStatus());
            }
            if (timeSheet.getStartTime() != null) {
                timeSheetDetail.setStartTime(timeSheet.getStartTime());
            }
            if (timeSheet.getEndTime() != null) {
                timeSheetDetail.setEndTime(timeSheet.getEndTime());
            }
            if (timeSheet.getStartTime() != null && timeSheet.getEndTime() != null) {
                timeSheetDetail.setDuration((double) Math
                        .round(DateTimeUtil.hoursDifference(timeSheet.getStartTime(), timeSheet.getEndTime()) * 10)
                        / 10);
            }
            timeSheetDetail.setTimeSheet(timeSheet);
            if (timeSheet.getDescription() != null) {
                timeSheetDetail.setDescription(timeSheet.getDescription());
            }
            if (timeSheet.getApproveStatus() != null) {
                timeSheetDetail.setApproveStatus(timeSheet.getApproveStatus());
            }
            if (timeSheet.getPriority() != null) {
                timeSheetDetail.setPriority(timeSheet.getPriority());
            }
            timeSheetDetail = timeSheetDetailRepository.save(timeSheetDetail);
            timeSheetDetailDto = new TimeSheetDetailDto(timeSheetDetail);
        } else {
            for (TimeSheetDetail timeSheetDetail : timeSheet.getDetails()) {
                if (timeSheet.getProject() != null) {
                    timeSheetDetail.setProject(timeSheet.getProject());
                }
                if (timeSheet.getTimeSheetStaffSet() != null && !timeSheet.getTimeSheetStaffSet().isEmpty()) {
                    List<TimeSheetStaff> timeSheetStaffs = new ArrayList<>(timeSheet.getTimeSheetStaffSet());
                    TimeSheetStaff timeSheetStaff = timeSheetStaffs.get(0);
                    if (timeSheetStaff.getStaff() != null) {
                        timeSheetDetail.setEmployee(timeSheetStaff.getStaff());
                    }
                }
                if (timeSheet.getActivity() != null) {
                    timeSheetDetail.setActivity(timeSheet.getActivity());
                }
                if (timeSheet.getPriority() != null) {
                    timeSheetDetail.setPriority(timeSheet.getPriority());
                }
                if (timeSheet.getProject() != null) {
                    timeSheetDetail.setProject(timeSheet.getProject());
                }
                if (timeSheet.getWorkingStatus() != null) {
                    timeSheetDetail.setWorkingStatus(timeSheet.getWorkingStatus());
                }

                if (timeSheet.getApproveStatus() != null) {
                    timeSheetDetail.setApproveStatus(timeSheet.getApproveStatus());
                }
                if (timeSheet.getStartTime() != null) {
                    if (timeSheetDetail.getStartTime() == null) {
                        timeSheetDetail.setStartTime(timeSheet.getStartTime());
                    }
                }
                if (timeSheet.getEndTime() != null) {
                    if (timeSheetDetail.getEndTime() == null) {
                        timeSheetDetail.setEndTime(timeSheet.getEndTime());
                    }
                }
                if (timeSheet.getStartTime() != null && timeSheet.getEndTime() != null) {
                    if (timeSheetDetail.getDuration() == 0) {
                        timeSheetDetail.setDuration((double) Math.round(
                                DateTimeUtil.hoursDifference(timeSheet.getStartTime(), timeSheet.getEndTime()) * 10)
                                / 10);
                    }
                }
                timeSheetDetail = timeSheetDetailRepository.save(timeSheetDetail);
                timeSheetDetailDto = new TimeSheetDetailDto(timeSheetDetail);
            }
        }
        return timeSheetDetailDto;
    }

    public List<TimeSheetDto> getListTimeSheetByStaffId(UUID id) {
        List<TimeSheetDto> timeSheetDtos = new ArrayList<>();
        if (id != null) {
            timeSheetDtos = timeSheetRepository.getListTimeSheetByStaffId(id);
        }
        return timeSheetDtos;
    }

    public Page<TimeSheetDto> getPageTimeSheetByStaffId(SearchTimeSheetDto dto) {
        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();
        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }
        String sql = "select new com.globits.timesheet.dto.TimeSheetDto(t,true) from TimeSheet t where t.staff.id =: staffId order by t.workingDate desc";
        Query q = manager.createQuery(sql);
        long count = timeSheetRepository.countTimeSheetByStaffId(dto.getId());

        if (dto.getStaffId() != null) {
            q.setParameter("staffId", dto.getStaffId());
        }
        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<TimeSheetDto> entities = q.getResultList();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    public TimeSheetDetailDto convertTimeSheetDetail(TimeSheet timeSheet) {
        TimeSheetDetailDto timeSheetDetailDto = null;
        if (timeSheet.getDetails() != null && !timeSheet.getDetails().isEmpty()) {
            for (TimeSheetDetail timeSheetDetail : timeSheet.getDetails()) {
                if (timeSheet.getTimeSheetStaffSet() != null && !timeSheet.getTimeSheetStaffSet().isEmpty()) {
                    List<TimeSheetStaff> timeSheetStaffs = new ArrayList<>(timeSheet.getTimeSheetStaffSet());
                    TimeSheetStaff timeSheetStaff = timeSheetStaffs.get(0);
                    if (timeSheetStaff.getStaff() != null) {
                        timeSheetDetail.setEmployee(timeSheetStaff.getStaff());
                    }
                }
                timeSheetDetail = timeSheetDetailRepository.save(timeSheetDetail);
                timeSheetDetailDto = new TimeSheetDetailDto(timeSheetDetail);
            }
        }
        return timeSheetDetailDto;
    }

    public List<TimeSheetDto> getTimeSheetByStaffId(UUID staffId, Date date) {
        String whereClause = "";
        String sql = "select new com.globits.timesheet.dto.TimeSheetDto(entity) from TimeSheet entity where (1=1) ";
        if (staffId != null) {
            whereClause += " AND ( entity.staff.id  =:staffId ) ";
        }
        if (date != null) {
            whereClause += "AND (entity.year =:year)";

            whereClause += "AND (entity.month =:month)";

            whereClause += "AND (entity.day =:day)";
        }
        sql += whereClause;
        Query query = manager.createQuery(sql, TimeSheetDto.class);
        if (staffId != null) {
            query.setParameter("staffId", staffId);
        }
        if (date != null) {
            Integer year = DateTimeUtil.getYear(date);
            Integer month = DateTimeUtil.getMonth(date);
            Integer day = DateTimeUtil.getDay(date);
            query.setParameter("year", year);
            query.setParameter("month", month);
            query.setParameter("day", day);
        }

        List<TimeSheetDto> timeSheets = query.getResultList();
        return timeSheets;
    }

    @Override
    // public List<StaffDto>checkTimeKeeping(TimekeepingDto dto) {
    // List<StaffDto> staffList = new ArrayList<>();
    // if (dto.getWorkingDate() != null) {
    // List<Staff> staffs = staffRepository.findAll();
    // if (staffs != null && staffs.size() > 0) {
    // for (Staff staff : staffs) {
    // List<TimeSheetDto> timeSheetDto =
    // getTimeSheetByStaffId(staff.getId(),dto.getWorkingDate());
    // if (timeSheetDto.size() == 0){
    // StaffDto staffDto = new StaffDto(staff,false);
    // staffList.add(staffDto);
    // }
    // }
    // }
    // }
    // return staffList;
    // }
    // public List<StaffDto> checkTimeSheetDetail(TimekeepingDto dto){
    // List<StaffDto> staffList = new ArrayList<>();
    // if (dto.getWorkingDate() != null) {
    // List<Staff> staffs = staffRepository.findAll();
    // if (staffs != null && staffs.size() > 0) {
    // for (Staff staff : staffs) {
    // List<TimeSheetDto> timeSheetDto = getTimeSheetByStaffId(staff.getId(),
    // dto.getWorkingDate());
    // if (timeSheetDto != null && timeSheetDto.size() > 0) {
    // TimeSheetDto timeSheetDto1 = timeSheetDto.get(0);
    // List<TimeSheetDetailDto> timeSheetDetailDto =
    // timeDetailSheetRepository.findTimeSheetDetailByTimeSheetId(timeSheetDto1.getId());
    // if (timeSheetDetailDto.size() <= 0) {
    // StaffDto staffDto = new StaffDto(staff, false);
    // staffList.add(staffDto);
    // }
    // }
    // }
    // }
    // }
    // return staffList;
    // }
    public Page<StaffDto> checkTimeKeeping(TimekeepingItemDto dto) {
        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();
        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }
        String keyword = '%' + dto.getKeyWord() + '%';
        String sql = "select new com.globits.hr.dto.StaffDto(entity, false) from  Staff entity  where entity.id not in (";
        String sqlCount = "select count(entity.id)from  Staff entity  where entity.id not in (";
        String whereClause = "select ts.staff.id from TimeSheet as ts where (1=1) ";
        if (dto.getWorkingDate() != null) {
            whereClause += "AND (ts.year =:year)";

            whereClause += "AND (ts.month =:month)";

            whereClause += "AND (ts.day =:day))";
        }
        if (dto.getKeyWord() != null) {
            whereClause += "And entity.displayName like: keyword";
        }
        sql += whereClause;
        sqlCount += whereClause;
        Query query = manager.createQuery(sql, StaffDto.class);
        Query qCount = manager.createQuery(sqlCount);
        if (dto.getWorkingDate() != null) {
            Integer year = DateTimeUtil.getYear(dto.getWorkingDate());
            Integer month = DateTimeUtil.getMonth(dto.getWorkingDate());
            Integer day = DateTimeUtil.getDay(dto.getWorkingDate());
            query.setParameter("year", year);
            qCount.setParameter("year", year);
            query.setParameter("month", month);
            qCount.setParameter("month", month);
            qCount.setParameter("day", day);
            query.setParameter("day", day);
        }
        if (dto.getKeyWord() != null) {
            query.setParameter("keyword", keyword);
            qCount.setParameter("keyword", keyword);
        }
        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        List<StaffDto> entities = query.getResultList();
        long count = (long) qCount.getSingleResult();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    public Page<StaffDto> checkTimeSheetDetail(TimekeepingItemDto dto) {
        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();
        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }
        String keyword = '%' + dto.getKeyWord() + '%';
        String sql = "select new com.globits.hr.dto.StaffDto(entity, false) from  Staff entity  where entity.id in (";
        String sqlCount = "select count(entity.id) from Staff entity where entity.id in (";
        String whereClause = "select ts.staff.id from TimeSheet as ts where ts.id not in (select tsd.timeSheet.id from TimeSheetDetail as tsd) ";

        if (dto.getWorkingDate() != null) {
            whereClause += " AND(ts.year =:year)";

            whereClause += " AND (ts.month =:month)";

            whereClause += " AND (ts.day =:day))";
        }
        if (dto.getKeyWord() != null) {
            whereClause += "And entity.displayName like: keyword";
        }
        sql += whereClause;
        sqlCount += whereClause;
        Query query = manager.createQuery(sql, StaffDto.class);
        Query qCount = manager.createQuery(sqlCount);
        if (dto.getWorkingDate() != null) {
            Integer year = DateTimeUtil.getYear(dto.getWorkingDate());
            Integer month = DateTimeUtil.getMonth(dto.getWorkingDate());
            Integer day = DateTimeUtil.getDay(dto.getWorkingDate());
            query.setParameter("year", year);
            query.setParameter("month", month);
            query.setParameter("day", day);
            qCount.setParameter("year", year);
            qCount.setParameter("month", month);
            qCount.setParameter("day", day);
        }
        if (dto.getKeyWord() != null) {
            query.setParameter("keyword", keyword);
            qCount.setParameter("keyword", keyword);
        }
        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        List<StaffDto> entities = query.getResultList();
        long count = (long) qCount.getSingleResult();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public TimeSheetDto timekeepingServicePlan(TimekeepingItemDto dto) {
        // boolean isRoleAdmin = false;
        // UserDto user = userExtService.getCurrentUser();
        // if (user != null && user.getRoles() != null && user.getRoles().size() > 0) {
        // for (RoleDto item : user.getRoles()) {
        // if (item.getName() != null &&
        // ("ROLE_ADMIN".equals(item.getName())||"ROLE_SUPER_ADMIN".equals(item.getName())))
        // {
        // isRoleAdmin = true;
        // }
        // }
        // }
        if (dto != null) {
            StaffDto staffDto = userExtService.getCurrentStaff();
            if (staffDto == null) {
                return null;
            }
            // if(!isRoleAdmin){
            // Date nowDate = new Date();
            // Date startTimeDate = DateTimeUtil.getStartOfDay(nowDate);
            // Date endTimeDate = DateTimeUtil.getEndOfDay(nowDate);
            // if ( dto.getWorkingDate() != null &&
            // dto.getWorkingDate().before(startTimeDate) ||
            // dto.getWorkingDate().after(endTimeDate)){
            // return null;
            // }
            // }
            TimeSheet timeSheet = null;
            if (dto.getTimeSheetId() != null) {
                Optional<TimeSheet> timeSheetOptional = timeSheetRepository.findById(dto.getTimeSheetId());
                if (timeSheetOptional.isPresent()) {
                    timeSheet = timeSheetOptional.get();
                }
            }
            if (dto.getTimeSheetId() == null) {
                timeSheet = new TimeSheet();
                timeSheet.setCreateDate(LocalDateTime.now());
                timeSheet.setModifyDate(LocalDateTime.now());

            }
            Staff staff = null;
            Optional<Staff> staffOptional = staffRepository.findById(staffDto.getId());
            if (staffOptional.isPresent()) {
                staff = staffOptional.get();
                timeSheet.setStaff(staff);
            }
            // if (dto.getStaffId() != null && isRoleAdmin) {
            // Optional<Staff> staffTimeSheet = staffRepository.findById(dto.getStaffId());
            // if (staffTimeSheet.isPresent()) {
            // staff = staffTimeSheet.get();
            // timeSheet.setStaff(staff);
            // }
            // }

            Set<TimeSheetShiftWorkPeriod> timeSheetShiftWorkPeriods = new HashSet<>();
            if (dto.getTimeSheetShiftWorkPeriods() != null && dto.getTimeSheetShiftWorkPeriods().size() > 0) {
                for (TimeSheetShiftWorkPeriodDto timeSheetShiftWorkPeriodDto : dto.getTimeSheetShiftWorkPeriods()) {
                    if (timeSheetShiftWorkPeriodDto != null) {
                        TimeSheetShiftWorkPeriod timeSheetShiftWorkPeriod = null;
                        if (timeSheetShiftWorkPeriodDto.getId() != null) {
                            timeSheetShiftWorkPeriod = timeSheetShiftWorkPeriodRepository
                                    .getOne(timeSheetShiftWorkPeriodDto.getId());
                        }
                        if (timeSheetShiftWorkPeriod == null) {
                            timeSheetShiftWorkPeriod = new TimeSheetShiftWorkPeriod();
                        }
                        timeSheetShiftWorkPeriod.setWorkingFormat(timeSheetShiftWorkPeriodDto.getWorkingFormat());
                        if (timeSheetShiftWorkPeriodDto.getShiftWorkTimePeriod() != null
                                && timeSheetShiftWorkPeriodDto.getShiftWorkTimePeriod().getId() != null) {
                            ShiftWorkTimePeriod shiftWorkTimePeriod = null;
                            Optional<ShiftWorkTimePeriod> optional = shiftWorkTimePeriodRepository
                                    .findById(timeSheetShiftWorkPeriodDto.getShiftWorkTimePeriod().getId());
                            if (optional.isPresent()) {
                                shiftWorkTimePeriod = optional.get();
                            }

                            timeSheetShiftWorkPeriod.setShiftWorkTimePeriod(shiftWorkTimePeriod);
                        }
                        timeSheetShiftWorkPeriod.setTimeSheet(timeSheet);
                        timeSheetShiftWorkPeriod.setNote(timeSheetShiftWorkPeriodDto.getNote());
                        timeSheetShiftWorkPeriods.add(timeSheetShiftWorkPeriod);
                        timeSheetShiftWorkPeriod.setRegStatus(TimeSheetRegStatus.PLAN);
                    }
                }
            }
            Date minDate = null;
            Date maxDate = null;
            for (TimeSheetShiftWorkPeriod timeSheetShiftWorkPeriod : timeSheetShiftWorkPeriods) {
                if (timeSheetShiftWorkPeriod != null && timeSheetShiftWorkPeriod.getShiftWorkTimePeriod() != null) {
                    if (timeSheetShiftWorkPeriod.getWorkingFormat() >= 0) {
                        Date start = timeSheetShiftWorkPeriod.getShiftWorkTimePeriod().getStartTime();
                        Date end = timeSheetShiftWorkPeriod.getShiftWorkTimePeriod().getEndTime();
                        if (minDate == null) {
                            minDate = start;
                        } else if (minDate.after(start)) {
                            minDate = start;
                        }
                        if (maxDate == null) {
                            maxDate = end;
                        } else if (maxDate.before(end)) {
                            maxDate = end;
                        }
                    }
                }
            }
            if (maxDate != null) {
                int maxHours = DateTimeUtil.getHours(maxDate);
                int maxMinutes = DateTimeUtil.getMinutes(maxDate);
                timeSheet.setEndTime(DateTimeUtil.setTime(dto.getWorkingDate(), maxHours, maxMinutes, 0, 0));
            } else {
                timeSheet.setEndTime(DateTimeUtil.setTime(dto.getWorkingDate(), 0, 0, 0, 0));
            }
            if (minDate != null) {
                int minHours = DateTimeUtil.getHours(minDate);
                int minMinutes = DateTimeUtil.getMinutes(minDate);
                timeSheet.setStartTime(DateTimeUtil.setTime(dto.getWorkingDate(), minHours, minMinutes, 0, 0));
            } else {
                timeSheet.setStartTime(DateTimeUtil.setTime(dto.getWorkingDate(), 0, 0, 0, 0));
            }

            if (timeSheetShiftWorkPeriods.size() > 0) {
                if (timeSheet.getTimeSheetShiftWorkPeriod() == null) {
                    timeSheet.setTimeSheetShiftWorkPeriod(timeSheetShiftWorkPeriods);
                } else {
                    timeSheet.getTimeSheetShiftWorkPeriod().clear();
                    timeSheet.getTimeSheetShiftWorkPeriod().addAll(timeSheetShiftWorkPeriods);
                }
            } else {
                if (timeSheet.getTimeSheetShiftWorkPeriod() != null) {
                    timeSheet.getTimeSheetShiftWorkPeriod().clear();
                }
            }
            timeSheet.setWorkingDate(dto.getWorkingDate());
            timeSheet.setYear(DateTimeUtil.getYear(dto.getWorkingDate()));
            timeSheet.setMonth(DateTimeUtil.getMonth(dto.getWorkingDate()));
            timeSheet.setDay(DateTimeUtil.getDay(dto.getWorkingDate()));
            timeSheet.setRegStatus(TimeSheetRegStatus.PLAN);
            timeSheet = timeSheetRepository.save(timeSheet);
            return new TimeSheetDto(timeSheet);
        }
        return null;
    }

    @Override
    public List<TimeSheetDto> getTimeSheetByTimeAndStaffId(UUID staffId, Date workingDate) {
        String whereClause = "";
        String sql = "select new com.globits.timesheet.dto.TimeSheetDto(entity) from TimeSheet entity where (1=1) ";
        if (staffId != null) {
            whereClause += " AND ( entity.staff.id  =:staffId ) ";
        }
        if (workingDate != null) {
            whereClause += "AND (entity.year =:year)";

            whereClause += "AND (entity.month =:month)";

            whereClause += "AND (entity.day =:day)";
        }
        sql += whereClause;
        Query query = manager.createQuery(sql, TimeSheetDto.class);
        if (staffId != null) {
            query.setParameter("staffId", staffId);
        }
        if (workingDate != null) {
            Integer year = DateTimeUtil.getYear(workingDate);
            Integer month = DateTimeUtil.getMonth(workingDate);
            Integer day = DateTimeUtil.getDay(workingDate);
            query.setParameter("year", year);
            query.setParameter("month", month);
            query.setParameter("day", day);
        }

        List<TimeSheetDto> timeSheets = query.getResultList();
        return timeSheets;
    }

    @Override
    public List<TimeSheetDto> getTimeSheetByTime(UUID staffId, Date fromDate, Date toDate) {
        Date fromDateBegin = DateTimeUtil.getStartOfDay(fromDate);
        Date toDateEnd = DateTimeUtil.getEndOfDay(toDate);

        String whereClause = "";
        String sql = "select new com.globits.timesheet.dto.TimeSheetDto(entity) from TimeSheet entity where (1=1) ";
        if (staffId != null) {
            whereClause += " AND ( entity.staff.id  =:staffId ) ";
        }
        if (fromDateBegin != null) {
            whereClause += "AND (entity.workingDate >= :fromDate)";
        }
        if (toDateEnd != null) {
            whereClause += "AND (entity.workingDate <= :toDate)";
        }

        sql += whereClause;
        Query query = manager.createQuery(sql, TimeSheetDto.class);

        if (staffId != null) {
            query.setParameter("staffId", staffId);
        }
        if (fromDateBegin != null) {
            query.setParameter("fromDate", fromDateBegin);
        }
        if (toDateEnd != null) {
            query.setParameter("toDate", toDateEnd);
        }

        List<TimeSheetDto> timeSheets = query.getResultList();
        return timeSheets;
    }

    @Override
    public List<TimekeepingItemDto> getTimeKeepingByMonth(int month, int year, UUID staffId) {
        List<TimekeepingItemDto> timeSheetList = timeSheetRepository.getTimeKeepingByMonth(year, month, staffId);
        return timeSheetList;
    }

    // public List<TimekeepingDto> readFromExcelFile(MultipartFile file) throws
    // IOException{
    // Workbook workbook = new XSSFWorkbook(file.getInputStream());
    // Sheet dataSheet = workbook.getSheetAt(0);
    // int rowIndex = 2;
    // int num = dataSheet.getLastRowNum();
    //
    // Row dateRow = dataSheet.getRow(0);
    // Cell dateCell = null;
    // Row periodRow = dataSheet.getRow(1);
    // Cell periodCell = null;
    //
    // logger.info("LastRowNum :"+num);
    // while (rowIndex <= num) {
    // logger.info("Read row :"+rowIndex);
    // Row currentRow = dataSheet.getRow(rowIndex);
    // Cell currentCell = null;
    // if (currentRow != null) {
    // List<TimekeepingDto> subList = new ArrayList<>();
    // currentCell = currentRow.getCell(0);
    //
    // String staffName=currentCell.getStringCellValue();
    // currentCell = currentRow.getCell(1);
    // String staffCode=currentCell.getStringCellValue();
    //
    // for (int i = 2; i < periodRow.getLastCellNum(); i++) {
    // periodCell = periodRow.getCell(i);
    // currentCell = currentRow.getCell(i);
    //
    // }
    //
    // }
    // rowIndex++;
    // }
    // logger.info("End Read FILE at Index :"+rowIndex);
    // }

    @Override
    public List<TimekeepingSummaryDto> getListTimekeepingSummary(SearchTimeSheetDto dto) {
        int year = dto.getYearReport() != null ? dto.getYearReport() : LocalDate.now().getYear();
        int month = dto.getMonthReport() != null ? dto.getMonthReport() : LocalDate.now().getMonthValue();
        UserDto userDto = userExtService.getCurrentUser();
        boolean isAdmin = userDto.getRoles() != null &&
                userDto.getRoles().stream()
                        .anyMatch(role -> Constants.ROLE_ADMIN.equals(role.getName()));
        if (!isAdmin) {
            return new ArrayList<>();
        }
        return timeSheetRepository.getListTimekeepingSummary(year, month);
    }

    @Override
    public void handleImportExcel(InputStream is) {
        try {
            List<TimeSheetImportItemDto> data = this.loadDataExcel(is);

            for (TimeSheetImportItemDto item : data) {
                try {
                    Staff staff = staffService.getByCode(item.getStaffCode());
                    if (staff == null)
                        continue;

                    LocalDate workingDate = LocalDate.of(item.getYear(), item.getMonth(), item.getDay());
                    LocalDateTime startOfDay = workingDate.atTime(9, 0);

                    // Delete all existing time sheets for the given date and staff
                    List<TimeSheet> timeSheets = timeSheetRepository.getTimeSheetByWorkingDate(staff.getId(),
                            Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant()));
                    timeSheetRepository.deleteAll(timeSheets);

                    // Create and populate a new TimeSheet object
                    TimeSheet timeSheet = new TimeSheet();
                    timeSheet.setStaff(staff);
                    timeSheet.setWorkingDate(Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant()));
                    timeSheet.setStartTime(Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant()));
                    timeSheet.setDay(item.getDay());
                    timeSheet.setMonth(item.getMonth() - 1);
                    timeSheet.setYear(item.getYear());
                    timeSheet.setCreateDate(LocalDateTime.now());

                    Set<TimeSheetShiftWorkPeriod> shiftWorkPeriods = new HashSet<>();
                    LocalDateTime endTime = startOfDay;

                    // Add shift work periods
                    endTime = addShiftWorkPeriod(item.getMorningShift(),
                            HrConstants.ShiftWorkTimePeriodEnum.MORNING_SHIFT, shiftWorkPeriods, timeSheet, endTime, 12,
                            0);
                    endTime = addShiftWorkPeriod(item.getAfternoonShift(),
                            HrConstants.ShiftWorkTimePeriodEnum.AFTERNOON_SHIFT, shiftWorkPeriods, timeSheet, endTime,
                            19, 0);
                    endTime = addShiftWorkPeriod(item.getEveningShift(),
                            HrConstants.ShiftWorkTimePeriodEnum.EVENING_SHIFT, shiftWorkPeriods, timeSheet, endTime, 23,
                            30);

                    timeSheet.setTimeSheetShiftWorkPeriod(shiftWorkPeriods);
                    timeSheet.setEndTime(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));

                    // Save the TimeSheet
                    timeSheetRepository.save(timeSheet);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LocalDateTime addShiftWorkPeriod(int shiftValue, HrConstants.ShiftWorkTimePeriodEnum shiftEnum,
                                             Set<TimeSheetShiftWorkPeriod> shiftWorkPeriods, TimeSheet timeSheet,
                                             LocalDateTime currentEndTime, int endHour, int endMinute) {
        if (shiftValue != HrConstants.WorkingFormatEnum.off.getValue()) {
            ShiftWorkTimePeriod shift = shiftWorkTimePeriodRepository.findById(shiftEnum.getValue()).orElse(null);
            if (shift != null) {
                TimeSheetShiftWorkPeriod shiftWorkPeriod = new TimeSheetShiftWorkPeriod();
                shiftWorkPeriod.setShiftWorkTimePeriod(shift);
                shiftWorkPeriod.setWorkingFormat(shiftValue);
                shiftWorkPeriod.setTimeSheet(timeSheet);
                shiftWorkPeriods.add(shiftWorkPeriod);
                currentEndTime = currentEndTime.withHour(endHour).withMinute(endMinute);
            }
        }
        return currentEndTime;
    }

    private List<TimeSheetImportItemDto> loadDataExcel(InputStream is) throws Exception {
        List<TimeSheetImportItemDto> data = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet datatSheet = workbook.getSheetAt(0);
            int rowIndex = 1;
            int number = datatSheet.getLastRowNum();
            while (rowIndex < number) {
                Row currentRow = datatSheet.getRow(rowIndex);
                Cell currentCell = null;
                if (currentRow != null) {
                    TimeSheetImportItemDto dto = new TimeSheetImportItemDto();
                    // staff code
                    int columnIndex = 1;
                    currentCell = currentRow.getCell(columnIndex++);
                    if (currentCell == null)
                        continue;

                    if (currentCell.getCellTypeEnum() == CellType.STRING) {
                        dto.setStaffCode(currentCell.getStringCellValue().trim());
                    } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        dto.setStaffCode(String.valueOf(Integer.valueOf((int) (currentCell.getNumericCellValue()))));
                    }
                    // day
                    currentCell = currentRow.getCell(columnIndex++);
                    if (currentCell != null) {
                        if (currentCell.getCellTypeEnum() == CellType.STRING) {
                            String dayStr = currentCell.getStringCellValue().trim();
                            dto.setDay(Integer.parseInt(dayStr));
                        } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            dto.setDay((int) currentCell.getNumericCellValue());
                        }
                    }
                    // month
                    currentCell = currentRow.getCell(columnIndex++);
                    if (currentCell != null) {
                        if (currentCell.getCellTypeEnum() == CellType.STRING) {
                            String monthStr = currentCell.getStringCellValue().trim();
                            dto.setMonth(Integer.parseInt(monthStr));
                        } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            dto.setMonth((int) currentCell.getNumericCellValue());
                        }
                    }
                    // year
                    currentCell = currentRow.getCell(columnIndex++);
                    if (currentCell != null) {
                        if (currentCell.getCellTypeEnum() == CellType.STRING) {
                            String yearStr = currentCell.getStringCellValue().trim();
                            dto.setYear(Integer.parseInt(yearStr));
                        } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            dto.setYear((int) currentCell.getNumericCellValue());
                        }
                    }
                    // morning shift
                    currentCell = currentRow.getCell(columnIndex++);
                    if (currentCell != null) {
                        if (currentCell.getCellTypeEnum() == CellType.STRING) {
                            String morningShift = currentCell.getStringCellValue().trim();
                            dto.setMorningShift(Integer.parseInt(morningShift));
                        } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            dto.setMorningShift((int) currentCell.getNumericCellValue());
                        }
                    }
                    // afternoon shift
                    currentCell = currentRow.getCell(columnIndex++);
                    if (currentCell != null) {
                        if (currentCell.getCellTypeEnum() == CellType.STRING) {
                            String afternoonShift = currentCell.getStringCellValue().trim();
                            dto.setAfternoonShift(Integer.parseInt(afternoonShift));
                        } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            dto.setAfternoonShift((int) currentCell.getNumericCellValue());
                        }
                    }
                    // evening shift
                    currentCell = currentRow.getCell(columnIndex);
                    if (currentCell != null) {
                        if (currentCell.getCellTypeEnum() == CellType.STRING) {
                            String afternoonShift = currentCell.getStringCellValue().trim();
                            dto.setEveningShift(Integer.parseInt(afternoonShift));
                        } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                            dto.setEveningShift((int) currentCell.getNumericCellValue());
                        }
                    }
                    data.add(dto);
                }
                rowIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public TimeSheetDto getByDate(TimeSheetDto timeSheetDto) {
        Staff staff = null;
        StaffDto staffDto = null;
        if (timeSheetDto.getStaff().getId() != null) {
            staff = staffRepository.findById(timeSheetDto.getStaff().getId()).orElse(null);
            staffDto = new StaffDto(staff);
        } else if (staff == null) {
            staffDto = userExtService.getCurrentStaff();
            if (staffDto != null) {
                staff = staffRepository.findById(staffDto.getId()).orElse(null);
            }
        }
        if (staff == null) {
            return null;
        }
        TimeSheetDto dto = null;
        if (staff != null && timeSheetDto.getWorkingDate() != null) {
            if (dto == null) {
                List<TimeSheet> timeSheets = timeSheetRepository.getTimeSheetByWorkingDate(
                        staff.getId(),
                        DateTimeUtil.getDay(timeSheetDto.getWorkingDate()),
                        DateTimeUtil.getMonth(timeSheetDto.getWorkingDate()),
                        DateTimeUtil.getYear(timeSheetDto.getWorkingDate()));
                if (!timeSheets.isEmpty()) {
                    dto = new TimeSheetDto(timeSheets.get(0));
                }
            }
            if (dto == null) {
                dto = new TimeSheetDto();
                dto.setWorkingDate(timeSheetDto.getWorkingDate());
                dto.setStaff(staffDto);
                List<StaffWorkSchedule> staffWorkSchedules = staffWorkScheduleRepository
                        .getByStaffAndWorkingDate(staff.getId(), timeSheetDto.getWorkingDate());
                if (!CollectionUtils.isEmpty(staffWorkSchedules)) {
                    List<TimeSheetDetailDto> timeSheetDetailsDto = new ArrayList<TimeSheetDetailDto>();
                    for (StaffWorkSchedule staffWorkSchedule : staffWorkSchedules) {
                        TimeSheetDetailDto timeSheetDetailDto = new TimeSheetDetailDto();
                        timeSheetDetailDto.setStaffWorkSchedule(new StaffWorkScheduleDto(staffWorkSchedule));
                        timeSheetDetailsDto.add(timeSheetDetailDto);
                    }
                    dto.setDetails(timeSheetDetailsDto);
                }
            }
        }
        return dto;
    }

    @Override
    public TimeSheetDto checkTimeSheet(TimeSheetDto dto, HttpServletRequest request) {
        if (dto == null) {
            return null;
        }
        boolean isRoleManager = false;
        UserDto currentUser = userExtService.getCurrentUser();

        if (currentUser != null && currentUser.getRoles() != null) {
            for (RoleDto role : currentUser.getRoles()) {
                if ("ROLE_ADMIN".equals(role.getName()) || "ROLE_SUPER_ADMIN".equals(role.getName())
                        || "HR_MANAGER".equals(role.getName())) {
                    isRoleManager = true;
                    break;
                }
            }
        }
        Staff staff = null;
        if (isRoleManager) {
            if (dto.getStaff().getId() != null) {
                staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
            }
            if (staff == null && dto.getStaff().getStaffCode() != null) {
                List<Staff> staffs = staffRepository.findByCode(dto.getStaff().getStaffCode());
                if (!staffs.isEmpty()) {
                    staff = staffs.get(0);
                }
            }
        } else {
            StaffDto staffDto = userExtService.getCurrentStaff();
            if (staffDto != null) {
                staff = staffRepository.findById(staffDto.getId()).orElse(null);
            }
        }

        // If no staff found, return null
        if (staff == null) {
            return null;
        }
        TimeSheet timeSheet = null;
        if (dto.getId() != null) {
            timeSheet = timeSheetRepository.findById(dto.getId()).orElse(null);
        }
        if (timeSheet == null) {
            List<TimeSheet> timeSheets = timeSheetRepository.getTimeSheetByWorkingDate(
                    staff.getId(),
                    DateTimeUtil.getDay(dto.getWorkingDate()),
                    DateTimeUtil.getMonth(dto.getWorkingDate()),
                    DateTimeUtil.getYear(dto.getWorkingDate()));
            if (!timeSheets.isEmpty()) {
                timeSheet = timeSheets.get(0);
            }
        }
        if (timeSheet == null) {
            timeSheet = new TimeSheet();
        }
        // Set Time Sheet Detail
        Set<TimeSheetDetail> details = new HashSet<>();
        if (!CollectionUtils.isEmpty(dto.getDetails())) {
            for (TimeSheetDetailDto itemDto : dto.getDetails()) {
                if (itemDto == null)
                    continue;
                // if (itemDto.getStartTime() == null && itemDto.getEndTime() == null) {
                // continue;
                // }
                TimeSheetDetail timeSheetDetail = null;
                if (itemDto.getId() != null) {
                    timeSheetDetail = timeSheetDetailRepository.findById(itemDto.getId()).orElse(null);
                }
                if (timeSheetDetail == null) {
                    timeSheetDetail = new TimeSheetDetail();
                }
                String ip = this.getClientIp(request);
                // timeSheetDetail.setAddressIP(ip);
                timeSheetDetail.setTimeSheet(timeSheet);
                timeSheetDetail.setNote(itemDto.getNote());
                timeSheetDetail.setStartTime(itemDto.getStartTime());
                timeSheetDetail.setEndTime(itemDto.getEndTime());
                if (itemDto.getStartTime() != null && itemDto.getEndTime() != null) {

                    long diffInMinutes = ChronoUnit.MINUTES.between(
                            itemDto.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                            itemDto.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

                    long hours = diffInMinutes / 60;
                    long minutes = diffInMinutes % 60;

                    double duration = Double.parseDouble(hours + "." + (minutes < 10 ? "0" + minutes : minutes));
                    timeSheetDetail.setDuration(duration);
                }
                timeSheetDetail.setEmployee(staff);
                StaffWorkSchedule staffWorkSchedule = null;
                if (itemDto.getStaffWorkSchedule() != null) {
                    staffWorkSchedule = staffWorkScheduleRepository.findById(itemDto.getStaffWorkSchedule().getId())
                            .orElse(null);
                }
                timeSheetDetail.setStaffWorkSchedule(staffWorkSchedule);
                details.add(timeSheetDetail);
            }
        }
        // Calculate start and end times for the time sheet
        Date minStartTime = null;
        Date maxEndTime = null;
        if (!CollectionUtils.isEmpty(details)) {
            for (TimeSheetDetail item : details) {
                Date startTime = item.getStartTime();
                Date endTime = item.getEndTime();

                if (startTime != null && (minStartTime == null || startTime.before(minStartTime))) {
                    minStartTime = startTime;
                }
                if (endTime != null && (maxEndTime == null || endTime.after(maxEndTime))) {
                    maxEndTime = endTime;
                }
            }
        }

        // Set start and end times in the time sheet
        if (minStartTime != null) {
            timeSheet.setStartTime(DateTimeUtil.setTime(dto.getWorkingDate(), DateTimeUtil.getHours(minStartTime),
                    DateTimeUtil.getMinutes(minStartTime), 0, 0));
        } else {
            timeSheet.setStartTime(DateTimeUtil.setTime(dto.getWorkingDate(), 0, 0, 0, 0));
        }

        if (maxEndTime != null) {
            timeSheet.setEndTime(DateTimeUtil.setTime(dto.getWorkingDate(), DateTimeUtil.getHours(maxEndTime),
                    DateTimeUtil.getMinutes(maxEndTime), 0, 0));
        } else {
            timeSheet.setEndTime(DateTimeUtil.setTime(dto.getWorkingDate(), 0, 0, 0, 0));
        }

        // Update shift work periods in the time sheet
        if (!details.isEmpty()) {
            if (timeSheet.getDetails() == null) {
                timeSheet.setDetails(details);
            } else {
                timeSheet.getDetails().clear();
                timeSheet.getDetails().addAll(details);
            }
        } else if (timeSheet.getDetails() != null) {
            timeSheet.getDetails().clear();
        }
        // Update time sheet details
        timeSheet.setWorkingDate(dto.getWorkingDate());
        timeSheet.setYear(DateTimeUtil.getYear(dto.getWorkingDate()));
        timeSheet.setMonth(DateTimeUtil.getMonth(dto.getWorkingDate()));
        timeSheet.setDay(DateTimeUtil.getDay(dto.getWorkingDate()));
        timeSheet.setStaff(staff);
        // Save and return the time sheet
        timeSheet = timeSheetRepository.save(timeSheet);
        return new TimeSheetDto(timeSheet);
    }

    @Override
    public String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // Xử lý trường hợp danh sách IP từ proxy (chỉ lấy IP đầu tiên)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        logger.info(ip);
        return extractValidIp(ip);
    }

    private String extractValidIp(String rawIp) {
        if (rawIp == null || rawIp.isEmpty()) {
            return null;
        }

        // Regex kiểm tra IPv6
        Pattern ipv6Pattern = Pattern.compile("([a-fA-F0-9:]+:[a-fA-F0-9:]+)");
        Matcher matcherIPv6 = ipv6Pattern.matcher(rawIp);
        if (matcherIPv6.find()) {
            return matcherIPv6.group();
        }

        // Regex kiểm tra IPv4
        Pattern ipv4Pattern = Pattern.compile("(\\d{1,3}(\\.\\d{1,3}){3})");
        Matcher matcherIPv4 = ipv4Pattern.matcher(rawIp);
        if (matcherIPv4.find()) {
            return matcherIPv4.group();
        }

        return null; // Không tìm thấy IP hợp lệ
    }

    @Override
    public String getClientIpV2(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @Override
    public String saveTimekeeping(TimeSheetStaffDto dto, HttpServletRequest request) {
        if (dto == null || dto.getWorkingDate() == null)
            return null;

        // Không xác định được là loại chấm công CHECKIN hay CHECKOUT => Lỗi
        if (dto.getTypeTimeSheetDetail() == null) {
            return "Không xác định được là loại chấm công CHECKIN hay CHECKOUT";
        }
        if (dto.getWorkingDate() == null) {
            return "Không xác định được ngày chấm công";
        }
        if (dto.getCurrentTime() == null) {
            return "Không xác định được thời gian chấm công";
        }
        Staff staff = null;
        if (dto.getStaffId() != null) {
            staff = staffRepository.findById(dto.getStaffId()).orElse(null);
        }
        if (staff == null) {
            staff = userExtService.getCurrentStaffEntity();
        }
        // Nhân viên được chấm công không được xác định
        if (staff == null) {
            return "Không xác định được nhân viên chấm công";
        }

        // Địa chỉ IP chấm công
        Boolean isDataImport = false;
        String timekeepingIp = null;
        if (request != null) {
            timekeepingIp = this.getClientIp(request);
        } else {
            isDataImport = true;
            if (dto.getIpCheckIn() != null) {
                timekeepingIp = dto.getIpCheckIn();
            }
            if (dto.getIpCheckOut() != null) {
                timekeepingIp = dto.getIpCheckOut();
            }

        }
        if (isDataImport == false) {
            // Địa chỉ IP có hợp lệ hay không, hợp lệ mới được chấm công
            Boolean isValidTimekeepingIP = false;
            // Có những nhân viên được phép chấm công với IP ngoài => Khi đó luôn hợp lệ
            if (staff.getAllowExternalIpTimekeeping() != null && staff.getAllowExternalIpTimekeeping().equals(true)) {
                isValidTimekeepingIP = true;
            } else {
                isValidTimekeepingIP = hrDepartmentIpService.isValidTimekeepingIP(staff.getId(), timekeepingIp);
            }

            if (isValidTimekeepingIP == null || isValidTimekeepingIP.equals(false)) {
                // Địa chỉ IP chấm công không hợp lệ
                return "Địa chỉ IP chấm công không hợp lệ, vui lòng đến nơi có các địa chỉ hợp lệ để thực hiện thao tác này";
            }
        }

        StaffWorkSchedule staffWorkSchedule = null;
        if (dto.getStaffWorkSchedule() != null && dto.getStaffWorkSchedule().getId() != null) {
            staffWorkSchedule = staffWorkScheduleRepository.findById(dto.getStaffWorkSchedule().getId()).orElse(null);
        }
        // Phân luôn ca làm việc khi chọn ca làm việc cần phân
        if (staffWorkSchedule == null && dto.getStaffWorkSchedule() != null
                && dto.getStaffWorkSchedule().getShiftWork() != null
                && (dto.getStaffWorkSchedule().getShiftWork().getId() != null
                || dto.getStaffWorkSchedule().getShiftWork().getCode() != null)
        ) {
            staffWorkSchedule = staffWorkScheduleService.generateScheduleFromTimeSheetStaffDto(dto);
        }
//        if (staffWorkSchedule == null) {
//            return "Không xác định chấm công cho ca làm việc nào đã được phân";
//        }

        if (staffWorkSchedule != null && staffWorkSchedule.getTimekeepingCalculationType() == null) {
            staffWorkSchedule
                    .setTimekeepingCalculationType(HrConstants.TimekeepingCalculationType.FIRST_IN_LAST_OUT.getValue());
        }

        // Các quyền chấm công
        UserDto userDto = userExtService.getCurrentUser();
        boolean isRoleUser = RoleUtils.hasRoleUser(userDto);
        boolean isRoleAdmin = RoleUtils.hasRoleAdmin(userDto);
        if (isRoleAdmin) {
            isRoleUser = false;
        }
        // Nếu là admin/hr manager => Lấy giờ chấm công theo giờ được gửi từ Client
        // Nếu là user => Lấy giờ chấm công theo thời gian hệ thống
        if (isRoleUser) {
            dto.setCurrentTime(new Date());
        }

        TimeSheet timeSheet = null;
        if (staffWorkSchedule != null && staffWorkSchedule.getId() != null) {
            List<TimeSheet> listTimeSheet = timeSheetRepository.findTimeSheetByWorkingDate(staff.getId(),
                    dto.getWorkingDate(), staffWorkSchedule.getId());

            if (listTimeSheet != null && !listTimeSheet.isEmpty()) {
                timeSheet = listTimeSheet.get(0);
            }
        } else {
            List<TimeSheet> timeSheets = timeSheetRepository.getTimeSheetByWorkingDate(staff.getId(), dto.getWorkingDate());
            if (timeSheets != null && !timeSheets.isEmpty()) {
                timeSheet = timeSheets.get(0);
            }
        }

        if (timeSheet == null) {
            timeSheet = new TimeSheet();
        }

        timeSheet.setStaff(staff);
        timeSheet.setWorkingDate(dto.getWorkingDate());
        timeSheet.setSchedule(staffWorkSchedule);

        // đã được phân ca
        if (staffWorkSchedule != null) {
            String errorMessage = null;
            // Chỉ cho phép chấm công ra vào 1 lần trong 1 ca làm việc
            if (staffWorkSchedule.getAllowOneEntryOnly() != null && staffWorkSchedule.getAllowOneEntryOnly().equals(true)) {
                errorMessage = handleOneEntryTimekeeping(timeSheet, staff, staffWorkSchedule, timekeepingIp, dto);
            }
            // Cho phép chấm công ra vào nhiều lần trong 1 ca
            else {
                errorMessage = handleMultipleEntriesTimekeeping(timeSheet, staff, staffWorkSchedule, timekeepingIp, dto);
            }

            // Có lỗi được trả ra
            if (errorMessage != null) {
                return errorMessage;
            }
        } else {
            // 1. Khởi tạo Set nếu null
            if (timeSheet.getDetails() == null) {
                timeSheet.setDetails(new HashSet<TimeSheetDetail>());
            }
            Set<TimeSheetDetail> details = timeSheet.getDetails();

            // 2. Lấy detail đầu tiên nếu có
            TimeSheetDetail detail = null;
            if (!details.isEmpty()) {
                for (TimeSheetDetail d : details) {
                    detail = d;
                    break;
                }
            }

            // 3. Nếu chưa có, tạo mới và thêm vào Set
            if (detail == null) {
                detail = new TimeSheetDetail();
                detail.setTimeSheet(timeSheet);
                details.add(detail);
            }

            // 4. Gán các trường còn lại
            detail.setEmployee(staff);
            if (HrConstants.TypeTimeSheetDetail.START.getValue() == (dto.getTypeTimeSheetDetail())) {
                detail.setStartTime(dto.getCurrentTime());
            } else if (HrConstants.TypeTimeSheetDetail.END.getValue() == (dto.getTypeTimeSheetDetail())) {
                detail.setEndTime(dto.getCurrentTime());
            }
        }

        timeSheet = timeSheetRepository.save(timeSheet);

        // đã được phân ca
        if (staffWorkSchedule != null) {
            manager.flush();
            // Kiểm tra nhân viên có làm đủ ca hay không
            // calculateStaffWorkTimeService.calculateStaffWorkTime(staffWorkSchedule.getId());
            calculateStaffWorkTimeServiceV2.calculateStaffWorkTimeAndSave(staffWorkSchedule.getId());

            manager.flush();
            manager.clear();

            // staffWorkSchedule =
            // staffWorkScheduleRepository.findById(staffWorkSchedule.getId()).orElse(null);

            // Cập nhật phiếu lương của nhân viên khi chấm công: số giờ làm việc, số ca làm
            // việc
            salaryResultStaffItemService.updateTimekeepingDataForPayslips(staff, staffWorkSchedule.getWorkingDate());
        }
        return "Chấm công thành công";

    }

    // Xử lý trường hợp chỉ chấm công 1 lần
    private String handleOneEntryTimekeeping(TimeSheet timeSheet, Staff staff, StaffWorkSchedule staffWorkSchedule,
                                             String timekeepingIp, TimeSheetStaffDto dto) {
        // Dữ liệu lần chấm công của ca làm việc này
        TimeSheetDetail timeSheetDetail = null;
        List<TimeSheetDetail> listDetails = new ArrayList<>();

        if (timeSheet.getDetails() != null && !timeSheet.getDetails().isEmpty()) {
            listDetails.addAll(timeSheet.getDetails());
            timeSheetDetail = listDetails.get(0); // Lấy bản ghi đầu tiên
        }

        if (timeSheetDetail == null) {
            timeSheetDetail = new TimeSheetDetail();
            timeSheetDetail.setStaffWorkSchedule(staffWorkSchedule);
            timeSheetDetail.setEmployee(staff);
            timeSheetDetail.setTimeSheet(timeSheet);
        }

        Integer timekeepingCalculationType = staffWorkSchedule.getTimekeepingCalculationType();
        boolean isCheckedDetail = false;

        // Xử lý theo loại tính thời gian chấm công
        if (dto.getTypeTimeSheetDetail() != null
                && dto.getTypeTimeSheetDetail().equals(HrConstants.TypeTimeSheetDetail.START.getValue())) {
            // Trường hợp CHECKIN
            if (timekeepingCalculationType != null) {
                if (timekeepingCalculationType
                        .equals(HrConstants.TimekeepingCalculationType.FIRST_IN_FIRST_OUT.getValue()) ||
                        timekeepingCalculationType
                                .equals(HrConstants.TimekeepingCalculationType.FIRST_IN_LAST_OUT.getValue())) {
                    // FIFO và FILO: Chỉ set StartTime nếu chưa có (lấy lần đầu tiên)
                    if (timeSheetDetail.getStartTime() != null) {
                        return "Bạn đã chấm công bắt đầu ca làm việc!";
                    }
                    timeSheetDetail.setStartTime(dto.getCurrentTime());
                    timeSheetDetail.setAddressIPCheckIn(timekeepingIp);
                } else if (timekeepingCalculationType
                        .equals(HrConstants.TimekeepingCalculationType.LAST_IN_LAST_OUT.getValue())) {
                    // LILO: Mỗi lần check-in sẽ cập nhật lại StartTime (lấy lần cuối cùng)
                    timeSheetDetail.setStartTime(dto.getCurrentTime());
                    timeSheetDetail.setAddressIPCheckIn(timekeepingIp);
                }
            }
            ShiftWorkTimePeriod shiftWorkTimePeriod = null;
            if (dto.getShiftWorkTimePeriod() != null) {
                shiftWorkTimePeriod = shiftWorkTimePeriodRepository.findById(dto.getShiftWorkTimePeriod().getId())
                        .orElse(null);
            }
            if (staffWorkSchedule.getAllowOneEntryOnly() == null
                    || (staffWorkSchedule != null && staffWorkSchedule.getAllowOneEntryOnly() == false)) {
                if (dto.getShiftWorkTimePeriod() == null) {
                    return "Ca làm việc cho phép chấm công ra vào nhiều lần trong toàn ca, cần bổ sung mã giao đoạn ca làm việc";
                } else {
                    shiftWorkTimePeriod = shiftWorkTimePeriodRepository.findById(dto.getShiftWorkTimePeriod().getId())
                            .orElse(null);
                }
                if (shiftWorkTimePeriod == null) {
                    return "Chưa xác định được giai đoạn làm việc";
                }
            }
            timeSheetDetail.setShiftWorkTimePeriod(shiftWorkTimePeriod);

            isCheckedDetail = true;
        } else if (dto.getTypeTimeSheetDetail().equals(HrConstants.TypeTimeSheetDetail.END.getValue())) {
            // Trường hợp CHECKOUT
//            if (timeSheetDetail.getStartTime() == null) {
//                return "Bạn chưa chấm công bắt đầu ca làm việc, vui lòng bắt đầu ca";
//            }

            if (timekeepingCalculationType != null) {
                if (timekeepingCalculationType
                        .equals(HrConstants.TimekeepingCalculationType.FIRST_IN_FIRST_OUT.getValue())) {
                    // FIFO: Chỉ set EndTime nếu chưa có (lấy lần đầu tiên)
                    if (timeSheetDetail.getEndTime() != null) {
                        return "Bạn đã chấm công kết thúc ca làm việc!";
                    }
                    timeSheetDetail.setEndTime(dto.getCurrentTime());
                    timeSheetDetail.setAddressIPCheckOut(timekeepingIp);
                } else if (timekeepingCalculationType
                        .equals(HrConstants.TimekeepingCalculationType.FIRST_IN_LAST_OUT.getValue()) ||
                        timekeepingCalculationType
                                .equals(HrConstants.TimekeepingCalculationType.LAST_IN_LAST_OUT.getValue())) {
                    // FILO và LILO: Mỗi lần check-out sẽ cập nhật lại EndTime (lấy lần cuối cùng)
                    timeSheetDetail.setEndTime(dto.getCurrentTime());
                    timeSheetDetail.setAddressIPCheckOut(timekeepingIp);
                }
            }
            isCheckedDetail = true;
        }

        // Nếu không chấm công được, báo lỗi
        if (!isCheckedDetail) {
            return "Có lỗi xảy ra, vui lòng thử lại";
        }

        // Cập nhật danh sách details
        listDetails.clear();
        listDetails.add(timeSheetDetail);

        if (timeSheet.getDetails() == null) {
            timeSheet.setDetails(new HashSet<>(listDetails));
        } else {
            timeSheet.getDetails().clear();
            timeSheet.getDetails().addAll(listDetails);
        }

        // Không có lỗi xảy ra
        return null;
    }

    // Xử lý trường hợp cho phép chấm công nhiều lần trong 1 ca
    private String handleMultipleEntriesTimekeeping(TimeSheet timeSheet, Staff staff,
                                                    StaffWorkSchedule staffWorkSchedule, String timekeepingIp, TimeSheetStaffDto dto) {
        ShiftWorkTimePeriod shiftWorkTimePeriod = null;

        if (dto.getShiftWorkTimePeriod() != null && dto.getShiftWorkTimePeriod().getId() != null) {
            shiftWorkTimePeriod = shiftWorkTimePeriodRepository.findById(dto.getShiftWorkTimePeriod().getId())
                    .orElse(null);
        }
        if (shiftWorkTimePeriod == null) {
            return "Không xác định được giai đoạn làm việc cần chấm công";
        }

        // Các lần chấm công cũ được sắp xếp theo startTime, endTime
        List<TimeSheetDetail> listDetails = new ArrayList<>();
        if (timeSheet.getDetails() != null && !timeSheet.getDetails().isEmpty()) {
            listDetails.addAll(timeSheet.getDetails());
        }
        Collections.sort(listDetails, new Comparator<TimeSheetDetail>() {
            @Override
            public int compare(TimeSheetDetail o1, TimeSheetDetail o2) {
                // First, compare by StartTime
                if (o1.getStartTime() == null && o2.getStartTime() == null)
                    return 0;
                if (o1.getStartTime() == null)
                    return 1;
                if (o2.getStartTime() == null)
                    return -1;

                int startTimeComparison = o1.getStartTime().compareTo(o2.getStartTime());
                if (startTimeComparison != 0) {
                    return startTimeComparison;
                }

                // If StartTime is the same, compare by endTime (handling nulls)
                if (o1.getEndTime() == null && o2.getEndTime() == null)
                    return 0;
                if (o1.getEndTime() == null)
                    return 1;
                if (o2.getEndTime() == null)
                    return -1;

                return o1.getEndTime().compareTo(o2.getEndTime());
            }
        });

        // Đã chấm công hay chưa:
        // + isCheckedDetail = false => Chưa chấm công
        // + isCheckedDetail = true => Đã chấm công rồi
        boolean isCheckedDetail = false;

        // Chấm công kết hợp kiểm tra cùng các lần chấm công trước
        if (!listDetails.isEmpty()) {
            for (TimeSheetDetail item : listDetails) {
                // Nếu TimesheetDetail không thuộc giai đoạn làm việc nào hoặc đã có thời gian
                // Checkout => skip
                if (item == null || item.getShiftWorkTimePeriod() == null || item.getEndTime() != null)
                    continue;

                // Cần lấy được Lần chấm công (TimesheetDetail) CỦA ĐÚNG TIMESHEET VÀ GIAI ĐOẠN
                // LÀM VIỆC
                // Nếu TimesheetDetail đang lặp không đáp ứng điều kiện => skip
                if (!item.getTimeSheet().getId().equals(timeSheet.getId())
                        || !item.getShiftWorkTimePeriod().getId().equals(shiftWorkTimePeriod.getId()))
                    continue;

                // Đây không phải lần chấm công hiện thời hoặc lần chấm công đã kết thúc => skip
                if (item.getIsCurrent() == null || item.getIsCurrent().equals(false))
                    continue;

                // Phát hiện 1 lần chấm công ĐÃ CHECKIN nhưng CHƯA CHECKOUT => Báo lỗi đã
                // checkin
                if (dto.getTypeTimeSheetDetail().equals(HrConstants.TypeTimeSheetDetail.START.getValue())) {
                    return "Bạn đã chấm công bắt đầu ca làm việc";
                }

                item.setEndTime(dto.getCurrentTime());
                item.setIsCurrent(false);
                item.setAddressIPCheckOut(timekeepingIp);

                // Đã chấm được công
                isCheckedDetail = true;

                break;

            }
        }

        // Nếu ở bên trên CHƯA CHẤM CÔNG ĐƯỢC và lần chấm công hiện tại là Checkout =>
        // Cập nhật thời gian Checkout của lần chấm công CUỐI CÙNG
        if (!isCheckedDetail && dto.getTypeTimeSheetDetail().equals(HrConstants.TypeTimeSheetDetail.END.getValue())) {
            if (listDetails.isEmpty()) {
                // Không có lần chấm công cũ nào => CHƯA CHECKIN LẦN NÀO => Lỗi chấm công
                return "Bạn chưa chấm công bắt đầu ca làm việc, vui lòng bắt đầu ca!";
            }

            // Cập nhật thời gian CHECKOUT của lần chấm công cuối cùng
            TimeSheetDetail lastDetail = listDetails.get(listDetails.size() - 1);

            if (lastDetail.getShiftWorkTimePeriod() != null
                    && !lastDetail.getShiftWorkTimePeriod().getId().equals(shiftWorkTimePeriod.getId())) {
                // Không có lần chấm công cũ nào => CHƯA CHECKIN LẦN NÀO => Lỗi chấm công
                return "Bạn chưa chấm công bắt đầu ca làm việc, vui lòng bắt đầu ca!";
            }

            lastDetail.setEndTime(dto.getCurrentTime());
            lastDetail.setIsCurrent(false);
            lastDetail.setAddressIPCheckOut(timekeepingIp);

            // Đã chấm được công
            isCheckedDetail = true;
        }

        // Nếu Vẫn CHƯA CHẤM CÔNG ĐƯỢC và lần chấm công hiện tại là CHECKIN (bắt đầu
        // mới) => Tạo lần chấm công mới
        if (!isCheckedDetail && dto.getTypeTimeSheetDetail().equals(HrConstants.TypeTimeSheetDetail.START.getValue())) {
            TimeSheetDetail timeSheetDetail = new TimeSheetDetail();

            timeSheetDetail.setStartTime(dto.getCurrentTime());
            timeSheetDetail.setAddressIPCheckIn(timekeepingIp);
            timeSheetDetail.setStaffWorkSchedule(staffWorkSchedule);
            timeSheetDetail.setShiftWorkTimePeriod(shiftWorkTimePeriod);
            timeSheetDetail.setEmployee(staff);
            timeSheetDetail.setTimeSheet(timeSheet);
            // là lần chấm công hiện thời
            timeSheetDetail.setIsCurrent(true);

            listDetails.add(timeSheetDetail);

            if (timeSheet.getDetails() == null) {
                timeSheet.setDetails(new HashSet<>(listDetails));
            } else {
                timeSheet.getDetails().clear();
                timeSheet.getDetails().addAll(listDetails);
            }

            // Đã chấm được công
            isCheckedDetail = true;
        }

        // Vẫn chưa chấm được công => Có lỗi xảy ra
        if (!isCheckedDetail) {
            return "Có lỗi xảy ra, vui lòng thử lại";
        }

        // Không có lỗi xảy ra
        return null;
    }

    private Integer determineCheckType(Date currentTime, ShiftWorkTimePeriod shiftWorkTimePeriod) {
        if (shiftWorkTimePeriod == null) {
            return null; // Không xác định được
        }

        // Chuyển đổi Date thành LocalTime
        LocalTime startTime = shiftWorkTimePeriod.getStartTime().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalTime();
        LocalTime endTime = shiftWorkTimePeriod.getEndTime().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalTime();
        LocalTime current = currentTime.toInstant()
                .atZone(ZoneId.systemDefault()).toLocalTime();

        // Xử lý trường hợp ca làm việc qua đêm
        boolean isOvernight = startTime.isAfter(endTime);

        LocalTime midpoint;
        if (isOvernight) {
            // Tính tổng thời gian ca làm việc (từ startTime đến endTime của ngày hôm sau)
            long totalMinutes = Duration.between(startTime, endTime.plusHours(24)).toMinutes();
            midpoint = startTime.plusMinutes(totalMinutes / 2);

            // Nếu currentTime không nằm trong ca làm việc
            if (current.isBefore(startTime) && current.isAfter(endTime)) {
                return null;
            }
        } else {
            // Tính midpoint cho ca làm việc bình thường
            midpoint = startTime.plusMinutes(Duration.between(startTime, endTime).toMinutes() / 2);
        }

        // Nếu currentTime <= midpoint => Check-in (1), ngược lại => Check-out (2)
        return current.isBefore(midpoint) || current.equals(midpoint) ? HrConstants.TypeTimeSheetDetail.START.getValue()
                : HrConstants.TypeTimeSheetDetail.END.getValue();
    }

    // TH1: CHỈ CHO PHÉP CHẤM CÔNG VÀO RA 1 LẦN TRONG 1 CA LÀM VIỆC
    private Integer autoDetectCheckTypeForOneEntry(Date currentTime, ShiftWorkTimePeriod shiftWorkTimePeriod,
                                                   StaffWorkSchedule staffWorkSchedule) {
        // Tự động xác định Checkin hay Checkout dựa vào thời gian đầu ca hay cuối ca
        // Integer checkType = this.determineCheckType(currentTime,
        // shiftWorkTimePeriod);
        Integer checkType = null;
        // TH 1.1: Nếu chưa CheckIn lần nào => Loại luôn là CheckIn
        if (staffWorkSchedule.getTimesheetDetails() == null || staffWorkSchedule.getTimesheetDetails().isEmpty()) {
            checkType = HrConstants.TypeTimeSheetDetail.START.getValue();
        }
        // TH 1.2: Nếu đã có lần Checkin mà chưa có lần Checkout => Cần Checkout
        else {
            // List<TimeSheetDetail> details =
            // timeSheetDetailRepository.findUncompletedDetailsByScheduleId(staffWorkSchedule.getId());
            // if (details != null && !details.isEmpty()) {
            checkType = HrConstants.TypeTimeSheetDetail.END.getValue();
            // }
        }

        return checkType;
    }

    // TH2: CHO PHÉP CHẤM CÔNG VÀO RA NHIỀU LẦN TRONG 1 CA LÀM VIỆC
    private Integer autoDetectCheckTypeForMultipleEntry(Date currentTime, ShiftWorkTimePeriod shiftWorkTimePeriod,
                                                        StaffWorkSchedule staffWorkSchedule) {
        // Tự động xác định Checkin hay Checkout dựa vào thời gian đầu ca hay cuối ca
        // Integer checkType = this.determineCheckType(currentTime,
        // shiftWorkTimePeriod);
        Integer checkType = null;

        // Tìm các lần Chấm công chưa hoàn thành trong quá khứ
        List<TimeSheetDetail> details = timeSheetDetailRepository
                .findUncompletedDetailsByScheduleId(staffWorkSchedule.getId());

        // TH 2.1: Nếu đã có lần Checkin mà chưa có lần Checkout => Cần Checkout
        if (details != null && !details.isEmpty()) {
            checkType = HrConstants.TypeTimeSheetDetail.END.getValue();
        }
        // TH 2.2: Cần Checkin
        else {
            checkType = HrConstants.TypeTimeSheetDetail.START.getValue();
        }

        return checkType;
    }

    private Integer detectCheckTypeBySchedule(Date currentTime, ShiftWorkTimePeriod shiftWorkTimePeriod,
                                              StaffWorkSchedule staffWorkSchedule) {
        if (staffWorkSchedule == null || currentTime == null || shiftWorkTimePeriod == null)
            return null;

        Integer checkType = null;

        // TH1: CHỈ CHO PHÉP CHẤM CÔNG VÀO RA 1 LẦN TRONG 1 CA LÀM VIỆC
        if (staffWorkSchedule.getAllowOneEntryOnly() != null && staffWorkSchedule.getAllowOneEntryOnly().equals(true)) {
            checkType = this.autoDetectCheckTypeForOneEntry(currentTime, shiftWorkTimePeriod, staffWorkSchedule);
        }
        // TH2: CHO PHÉP CHẤM CÔNG VÀO RA NHIỀU LẦN TRONG 1 CA LÀM VIỆC
        else {
            checkType = this.autoDetectCheckTypeForMultipleEntry(currentTime, shiftWorkTimePeriod, staffWorkSchedule);
        }

        return checkType;
    }

    // Lấy dữ liệu chấm công hiện thời của nhân viên
    @Override
    public TimeSheetStaffDto getCurrentTimekeepingData() {
        TimeSheetStaffDto response = new TimeSheetStaffDto();

        Date workingDate = new Date();
        response.setWorkingDate(workingDate);
        response.setCurrentTime(workingDate);
        response.setTypeTimeSheetDetail(HrConstants.TypeTimeSheetDetail.START.getValue());

        Staff staff = userExtService.getCurrentStaffEntity();
        // Nhân viên được chấm công không được xác định
        if (staff == null) {
            return response;
        }

        response.setStaffId(staff.getId());

        StaffDto staffDto = new StaffDto();
        staffDto.setId(staff.getId());
        staffDto.setStaffCode(staff.getStaffCode());
        staffDto.setDisplayName(staff.getDisplayName());
        response.setStaff(staffDto);

        // Tìm ca làm việc cần chấm công trong khoảng thời gian hiện tại
        List<StaffWorkSchedule> availableWorkSchedules = staffWorkScheduleRepository
                .findCurrentScheduleByStaffIdAndCurrentTime(staff.getId(), workingDate);
        if (availableWorkSchedules == null || availableWorkSchedules.isEmpty()) {
            // Tìm ca làm viec cần chấm công GẦN NHẤT với thời gian hiện tại (có thể diễn ra
            // trước hoặc sau thời gian hiện tại)
            availableWorkSchedules = staffWorkScheduleRepository
                    .findNearestScheduleByStaffIdAndCurrentTime(staff.getId(), workingDate);

            if (availableWorkSchedules == null || availableWorkSchedules.isEmpty()) {
                response.setErrorMessage("Hiện tại không có ca làm việc nào được phân trong ngày hôm nay.");
                return response;
            }
        }

        StaffWorkSchedule staffWorkSchedule = availableWorkSchedules.get(0);
        if (staffWorkSchedule == null)
            return response;

        // Tìm ShiftWorkTimePeriod hiện tại
        ShiftWork shiftWork = staffWorkSchedule.getShiftWork();
        if (shiftWork == null) {
            return response;
        }

        ShiftWorkDto shiftWorkDto = new ShiftWorkDto(shiftWork);
        StaffWorkScheduleDto staffWorkScheduleDto = new StaffWorkScheduleDto(staffWorkSchedule);
        staffWorkScheduleDto.setShiftWork(shiftWorkDto);

        response.setStaffWorkSchedule(new StaffWorkScheduleDto(staffWorkSchedule));

        // Tìm giai đoạn làm việc cần chấm công trong khoảng thời gian hiện tại
        List<ShiftWorkTimePeriod> availablePeriods = shiftWorkTimePeriodRepository.getCurrentTimePeriod(workingDate,
                shiftWork.getId());
        if (availablePeriods == null || availablePeriods.isEmpty()) {
            // Tìm giai đoạn làm việc cần chấm công GẦN NHẤT với thời gian hiện tại (có thể
            // diễn ra trước hoặc sau thời gian hiện tại)
            availablePeriods = shiftWorkTimePeriodRepository.getNearestTimePeriod(workingDate, shiftWork.getId());

            if (availablePeriods == null || availablePeriods.isEmpty()) {
                return response;
            }
        }

        ShiftWorkTimePeriod shiftWorkTimePeriod = availablePeriods.get(0);
        response.setShiftWorkTimePeriod(new ShiftWorkTimePeriodDto(shiftWorkTimePeriod));

        // Tự động xác định Checkin hay Checkout
        Integer checkType = this.detectCheckTypeBySchedule(response.getCurrentTime(), shiftWorkTimePeriod,
                staffWorkSchedule);
        response.setTypeTimeSheetDetail(checkType);

        return response;
    }

}
