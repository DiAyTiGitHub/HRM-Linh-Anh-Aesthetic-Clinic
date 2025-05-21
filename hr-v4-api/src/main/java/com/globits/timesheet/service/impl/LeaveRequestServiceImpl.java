package com.globits.timesheet.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.hr.dto.*;
import com.globits.hr.repository.ShiftWorkRepository;
import com.globits.hr.repository.ShiftWorkTimePeriodRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.repository.StaffWorkScheduleRepository;
import com.globits.hr.service.CalculateStaffWorkTimeServiceV2;
import com.globits.hr.service.StaffHierarchyService;
import com.globits.hr.service.StaffWorkScheduleService;
import com.globits.hr.service.UserExtService;
import com.globits.hr.utils.RoleUtils;
import com.globits.salary.dto.SalaryPeriodDto;
import com.globits.salary.service.SalaryPeriodService;
import com.globits.security.dto.UserDto;
import com.globits.timesheet.domain.LeaveRequest;
import com.globits.timesheet.domain.LeaveType;
import com.globits.timesheet.dto.LeaveRequestDto;
import com.globits.timesheet.dto.search.LeaveRequestSearchDto;
import com.globits.timesheet.dto.search.SearchStaffWorkScheduleDto;
import com.globits.timesheet.repository.LeaveRequestRepository;
import com.globits.timesheet.repository.LeaveTypeRepository;
import com.globits.timesheet.service.LeaveRequestService;
import jakarta.persistence.Query;
import org.apache.http.HttpStatus;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LeaveRequestServiceImpl extends GenericServiceImpl<LeaveRequest, UUID> implements LeaveRequestService {
    private static final Logger logger = LoggerFactory.getLogger(LeaveRequestServiceImpl.class);

    @Autowired
    private LeaveRequestRepository repository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private UserExtService userExtService;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;
    @Autowired
    private StaffWorkScheduleService staffWorkScheduleService;
    @Autowired
    private StaffWorkScheduleRepository staffWorkScheduleRepository;
    @Autowired
    private StaffHierarchyService staffHierarchyService;

    @Autowired
    private ShiftWorkRepository shiftWorkRepository;

    @Autowired
    private ShiftWorkTimePeriodRepository shiftWorkTimePeriodRepository;

    @Autowired
    private CalculateStaffWorkTimeServiceV2 calculateStaffWorkTimeServiceV2;

    @Autowired
    private StaffWorkScheduleRepository staffWorkScheduleRepositoryV2;
    @Autowired
    private SalaryPeriodService salaryPeriodService;

    @Override
    public LeaveRequestDto getById(UUID id) {
        if (id != null) {
            LeaveRequest entity = repository.findById(id).orElse(null);
            if (entity != null) {
                return new LeaveRequestDto(entity);
            }
        }
        return null;
    }

    @Override
    public LeaveRequestDto saveOrUpdate(LeaveRequestDto dto) {
        if (dto == null) return null;

//        UserDto user = userExtService.getCurrentUser();
//        boolean isAdmin = user != null && user.getRoles() != null &&
//                user.getRoles().stream().anyMatch(role ->
//                        role.getName() != null &&
//                                ("ROLE_ADMIN".equals(role.getName()) || "HR_MANAGER".equals(role.getName()))
//                );

        UserDto userDto = userExtService.getCurrentUser();
        boolean isAdmin = RoleUtils.hasRoleAdmin(userDto);
        boolean isManager = RoleUtils.hasRoleHrManager(userDto);

        LeaveRequest entity = null;
        if (dto.getId() != null) {
            entity = repository.findById(dto.getId()).orElse(null);
        }
        if (entity == null) entity = new LeaveRequest();

        if (isAdmin || isManager) {
            // If user is a manager, use the provided requestStaff (or null if not provided)
            if (dto.getRequestStaff() != null && dto.getRequestStaff().getId() != null) {
                Staff requestStaff = staffRepository.findById(dto.getRequestStaff().getId()).orElse(null);
                entity.setRequestStaff(requestStaff);
            } else {
                entity.setRequestStaff(null);
            }
        } else {
            if (!(dto.getRequestStaff() != null && dto.getRequestStaff().getId() != null)) {
                StaffDto staff = userExtService.getCurrentStaff();
                if (staff != null) {
                    Staff requestStaff = staffRepository.findById(staff.getId()).orElse(null);
                    entity.setRequestStaff(requestStaff);
                } else {
                    return null;
                }
            } else {
                Staff requestStaff = staffRepository.findById(dto.getRequestStaff().getId()).orElse(null);
                entity.setRequestStaff(requestStaff);
            }
        }

        entity.setRequestDate(dto.getRequestDate());

        entity.setFromDate(dto.getFromDate());

        if (dto.getFromDateLeaveType() == null) {
            dto.setFromDateLeaveType(HrConstants.LeaveShiftType.FULL_SHIFT_OFF.getValue());
        }
        entity.setFromDateLeaveType(dto.getFromDateLeaveType());

        entity.setToDate(dto.getToDate());

        if (dto.getToDateLeaveType() == null) {
            dto.setToDateLeaveType(HrConstants.LeaveShiftType.FULL_SHIFT_OFF.getValue());
        }
        entity.setToDateLeaveType(dto.getToDateLeaveType());

        entity.setRequestReason(dto.getRequestReason());
        entity.setApprovalStatus(dto.getApprovalStatus());
        if (entity.getApprovalStatus() != null && !entity.getApprovalStatus().equals(HrConstants.AbsenceRequestApprovalStatus.NOT_APPROVED_YET.getValue())) {
            StaffDto currentStaff = userExtService.getCurrentStaff();
            UUID approvalStaffId = currentStaff.getId();
            if (approvalStaffId != null) {
                Staff approvalStaff = staffRepository.findById(approvalStaffId).orElse(null);
                entity.setApprovalStaff(approvalStaff);
            }
        } else {
            entity.setApprovalStaff(null);
        }

        LeaveType leaveType = null;
        if (dto.getLeaveType() != null && dto.getLeaveType().getId() != null) {
            leaveType = leaveTypeRepository.findById(dto.getLeaveType().getId()).orElse(null);
            entity.setLeaveType(leaveType);
        }
        if (leaveType == null) {
            return null;
        }
        if (dto.getHalfDayLeave() != null) {
            entity.setHalfDayLeave(dto.getHalfDayLeave());
        }
        if (dto.getHalfDayLeaveStart() != null) {
            entity.setHalfDayLeaveStart(dto.getHalfDayLeaveStart());
            if (entity.getHalfDayLeaveStart()) {
                if (dto.getShiftWorkStart() != null) {
                    ShiftWork shiftWork = shiftWorkRepository.findById(dto.getShiftWorkStart().getId()).orElse(null);
                    if (shiftWork != null) {
                        entity.setShiftWorkStart(shiftWork);
                        if (dto.getTimePeriodStart() != null) {
                            entity.setTimePeriodStart(shiftWorkTimePeriodRepository.findById(dto.getTimePeriodStart().getId()).orElse(null));
                        }
                    }
                }
            }
        }
        if (dto.getHalfDayLeaveEnd() != null) {
            entity.setHalfDayLeaveEnd(dto.getHalfDayLeaveEnd());
            if (entity.getHalfDayLeaveEnd()) {
                if (dto.getShiftWorkEnd() != null) {
                    ShiftWork shiftWork = shiftWorkRepository.findById(dto.getShiftWorkEnd().getId()).orElse(null);
                    if (shiftWork != null) {
                        entity.setShiftWorkEnd(shiftWork);
                        if (dto.getTimePeriodEnd() != null) {
                            entity.setTimePeriodEnd(shiftWorkTimePeriodRepository.findById(dto.getTimePeriodEnd().getId()).orElse(null));
                        }
                    }
                }
            }
        }
        this.calculateAndSetTotalTime(entity);
        entity = repository.save(entity);
        return new LeaveRequestDto(entity);
    }

    public void calculateAndSetTotalTime(LeaveRequest entity) {
        if (entity.getFromDate() != null && entity.getToDate() != null) {
            List<LocalDate> dates = getDatesBetween(entity.getFromDate(), entity.getToDate());
            int size = dates.size();
            entity.setTotalDays((double) size);
            if (entity.getHalfDayLeaveStart()) {
                entity.setTotalDays(entity.getTotalDays() - 0.5);
            }
            if (entity.getHalfDayLeaveEnd()) {
                entity.setTotalDays(entity.getTotalDays() - 0.5);
            }
            if (entity.getTotalDays() < 0D) {
                entity.setTotalDays(0D);
            }
            entity.setTotalHours(entity.getTotalDays() * 8);
        }
    }

    public static List<LocalDate> getDatesBetween(Date fromDate, Date toDate) {
        LocalDate start = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                .collect(Collectors.toList());
    }

    @Override
    public LeaveRequestDto getLeaveRequest(UUID id) {
        if (id == null) return null;
        LeaveRequest entity = repository.findById(id).orElse(null);
        return new LeaveRequestDto(entity);
    }

    @Override
    public Page<LeaveRequestDto> searchByPage(LeaveRequestSearchDto dto) {
        if (dto == null) return null;

        int pageIndex = Math.max(dto.getPageIndex() - 1, 0);
        int pageSize = dto.getPageSize();

        UserDto userDto = userExtService.getCurrentUser();
        Staff staff = userExtService.getCurrentStaffEntity();

        boolean isAdmin = RoleUtils.hasRoleAdmin(userDto);
        boolean isManager = RoleUtils.hasRoleHrManager(userDto);
        boolean isStaffView = RoleUtils.hasRoleHrStaffView(userDto);
        boolean isAssignment = RoleUtils.hasRoleHrAssignment(userDto);
        boolean isShiftAssignment = RoleUtils.hasShiftAssignmentPermission(staff);

        // Phân quyền lọc theo staff
        if (!(isAdmin || isManager || isStaffView)) {
            if (isAssignment && isShiftAssignment && staff != null) {
                List<UUID> managedStaff = staffHierarchyService.getAllManagedStaff(staff.getId(), List.of(staff.getId()));
                dto.setStaffIdList(managedStaff);
            } else {
                if (staff == null) return null;
                dto.setStaffId(staff.getId());
            }
        }

        String sql = "SELECT NEW com.globits.timesheet.dto.LeaveRequestDto(entity) FROM LeaveRequest entity";
        String countSql = "SELECT COUNT(entity.id) FROM LeaveRequest entity";
        String joinPositionStaff = "";
        if (dto.getPositionTitleId() != null || dto.getDepartmentId() != null || dto.getOrganizationId() != null) {
            joinPositionStaff += " JOIN Position pos ON pos.isMain = true AND pos.staff.id = entity.requestStaff.id ";
        }
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        Map<String, Object> params = new HashMap<>();

        if (StringUtils.hasText(dto.getKeyword())) {
            where.append(" AND (entity.requestStaff.displayName LIKE :text OR entity.requestStaff.staffCode LIKE :text)");
            params.put("text", "%" + dto.getKeyword() + "%");
        }
        if (dto.getStaffId() != null) {
            where.append(" AND entity.requestStaff.id = :staffId");
            params.put("staffId", dto.getStaffId());
        }
        if (dto.getStaffIdList() != null && !dto.getStaffIdList().isEmpty()) {
            where.append(" AND entity.requestStaff.id IN :staffIdList");
            params.put("staffIdList", dto.getStaffIdList());
        }
        if (dto.getApprovalStaffId() != null) {
            where.append(" AND entity.approvalStaff.id = :approvalStaffId");
            params.put("approvalStaffId", dto.getApprovalStaffId());
        }
        if (dto.getApprovalStatus() != null) {
            where.append(" AND entity.approvalStatus = :approvalStatus");
            params.put("approvalStatus", dto.getApprovalStatus());
        }
        if (dto.getFromDate() != null) {
            where.append(" AND entity.fromDate >= :fromDate");
            params.put("fromDate", dto.getFromDate());
        }
        if (dto.getToDate() != null) {
            where.append(" AND entity.toDate <= :toDate");
            params.put("toDate", dto.getToDate());
        }
        if (dto.getOrganizationId() != null && StringUtils.hasText(dto.getOrganizationId().toString())) {
            where.append(" AND pos.department.organization.id = :organizationId ");
            params.put("organizationId", dto.getOrganizationId());
        }
        if (dto.getDepartmentId() != null && StringUtils.hasText(dto.getDepartmentId().toString())) {
            where.append(" AND pos.department.id = :departmentId ");
            params.put("departmentId", dto.getDepartmentId());
        }
        if (dto.getPositionTitleId() != null && StringUtils.hasText(dto.getPositionTitleId().toString())) {
            where.append(" AND pos.title.id = :positionTitleId ");
            params.put("positionTitleId", dto.getPositionTitleId());
        }

        sql += joinPositionStaff;
        countSql += joinPositionStaff;
        sql += where.toString() + " ORDER BY entity.modifyDate DESC";
        countSql += where.toString();

        Query query = manager.createQuery(sql, LeaveRequestDto.class);
        Query countQuery = manager.createQuery(countSql);
        params.forEach((k, v) -> {
            query.setParameter(k, v);
            countQuery.setParameter(k, v);
        });

        query.setFirstResult(pageIndex * pageSize);
        query.setMaxResults(pageSize);

        List<LeaveRequestDto> resultList = query.getResultList();
        long total = (long) countQuery.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(resultList, pageable, total);
    }

    @Override
    public List<LeaveRequestDto> getAllLeaveRequestBySearchStaffWorkSchedule(SearchStaffWorkScheduleDto dto) {
        if (dto == null) return null;

        String sqlCount = "select count(entity.id) from LeaveRequest as entity ";
        String sql = "select new com.globits.timesheet.dto.LeaveRequestDto(entity) from LeaveRequest as entity ";
        String whereClause = "where (1=1) ";

        if (dto.getStaffIds() != null && !dto.getStaffIds().isEmpty()) {
            whereClause += " and entity.requestStaff.id in (:staffIds)";
        }

        if (dto.getFromDate() != null) {
            whereClause += " and entity.fromDate >= :fromDate";
        }

        if (dto.getToDate() != null) {
            whereClause += " and entity.toDate <= :toDate";
        }

        sql += whereClause;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, LeaveRequestDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getStaffIds() != null && !dto.getStaffIds().isEmpty()) {
            q.setParameter("staffIds", dto.getStaffIds());
            qCount.setParameter("staffIds", dto.getStaffIds());
        }

        if (dto.getFromDate() != null) {
            q.setParameter("fromDate", dto.getFromDate());
            qCount.setParameter("fromDate", dto.getFromDate());
        }

        if (dto.getToDate() != null) {
            q.setParameter("toDate", dto.getToDate());
            qCount.setParameter("toDate", dto.getToDate());
        }
        List<LeaveRequestDto> entities = q.getResultList();

        return entities;
    }


    @Override
    public Boolean deleteById(UUID id) {
        if (id == null) return false;
        LeaveRequest leaveRequest = repository.findById(id).orElse(null);
        if (leaveRequest != null) {
            List<LocalDate> dateList = getDatesBetween(leaveRequest.getFromDate(), leaveRequest.getToDate());
            if (!CollectionUtils.isEmpty(dateList)) {
                for (LocalDate date : dateList) {
                    Date workingDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    List<StaffWorkSchedule> staffWorkSchedules = staffWorkScheduleRepository
                            .findAllByStaffIdAndWorkingDate(
                                    leaveRequest.getRequestStaff().getId(),
                                    workingDate,
                                    workingDate
                            );

                    if (!CollectionUtils.isEmpty(staffWorkSchedules)) {
                        for (StaffWorkSchedule staffWorkSchedule : staffWorkSchedules.stream().filter(s -> s.getLeaveType() != null).toList()) {
                            staffWorkSchedule.setLeaveType(null);
                            staffWorkScheduleRepositoryV2.saveAndFlush(staffWorkSchedule);
                            calculateStaffWorkTimeServiceV2.calculateStaffWorkTimeAndSave(staffWorkSchedule.getId());
                        }
                    }
                }
            }
            repository.delete(leaveRequest);
            return true;
        }
        return false;
    }

    @Override
    public Boolean deleteMultiple(List<UUID> ids) {
        if (ids == null)
            return false;
        boolean isValid = true;
        for (UUID itemId : ids) {
            boolean deleteRes = this.deleteById(itemId);
            if (!deleteRes)
                isValid = false;
        }
        return isValid;
    }

    @Override
    @Modifying
    public List<UUID> updateRequestsApprovalStatus(LeaveRequestSearchDto dto) {
        if (dto == null || dto.getApprovalStatus() == null || dto.getChosenIds() == null || dto.getChosenIds().isEmpty()) {
            return Collections.emptyList(); // Trả về danh sách rỗng thay vì null
        }

        Staff currentStaff = userExtService.getCurrentStaffEntity();

        List<LeaveRequest> onSaveRequests = new ArrayList<>();
        for (UUID id : dto.getChosenIds()) {
            Optional<LeaveRequest> optionalRequest = repository.findById(id);
            if (optionalRequest.isPresent()) {
                LeaveRequest request = optionalRequest.get();
                request.setApprovalStatus(dto.getApprovalStatus());

                if (dto.getApprovalStatus().equals(HrConstants.setApprovalStatus.APPROVED.getValue())) {
                    request.setApprovalStaff(currentStaff);
                }

                onSaveRequests.add(request);
            }
        }

        if (onSaveRequests.isEmpty()) {
            return Collections.emptyList();
        }

        if (dto.getApprovalStatus() == HrConstants.setApprovalStatus.APPROVED.getValue()) {
            changeStaffWorkSchedules(onSaveRequests);
        }

        List<LeaveRequest> savedRequests = repository.saveAll(onSaveRequests);
        List<UUID> updatedRequestIds = new ArrayList<>();
        for (LeaveRequest request : savedRequests) {
            updatedRequestIds.add(request.getId());
        }

        return updatedRequestIds;
    }


    private void changeStaffWorkSchedules(List<LeaveRequest> onSaveRequests) {
        List<StaffWorkSchedule> updatedSchedules = onSaveRequests.stream()
                .flatMap(leaveRequest -> staffWorkScheduleService.changeStatusFromLeaveRequest(leaveRequest).stream())
                .collect(Collectors.toList());

        if (!updatedSchedules.isEmpty()) {
            staffWorkScheduleRepository.saveAll(updatedSchedules);
        }
    }

    @Override
    public ApiResponse<List<UUID>> isExistLeaveRequestInPeriod(List<UUID> staffIds, Date fromDate, Date toDate) {
        return new ApiResponse<>(HttpStatus.SC_OK, "OK", repository.findLeaveRequestByDate(staffIds, fromDate, toDate));
    }

    @Override
    public XWPFDocument generateUnpaidLeaveDocx(UUID id) throws IOException {
        if (id == null) {
            return null;
        }
        LeaveRequest entity = repository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        // Đọc template
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("DON_XIN_NGHI_PHEP_KHONG_LUONG.docx");

        XWPFDocument document = new XWPFDocument(inputStream);
        inputStream.close();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Map<String, String> replacements = new HashMap<>();

        // Thay thế các placeholder trong document
        String fullName = "";
        String birthDay = "";
        String position = "";
        String phoneNumber = "";
        String gender = "";
        String placeOfBirth = "";
        String fromDate = "";
        String toDate = "";
        String reason = "";
        String today = "";

        if (entity.getRequestStaff() != null) {
            if (entity.getRequestStaff().getDisplayName() != null) {
                fullName = entity.getRequestStaff().getDisplayName();
            }

            if (entity.getRequestStaff().getBirthDate() != null) {
                try {
                    birthDay = dateFormat.format(entity.getRequestStaff().getBirthDate());
                } catch (Exception e) {
                    logger.error("Lỗi định dạng ngày sinh trong LeaveRequestServiceImpl: {}", e.getMessage());
                }
            }
            if (entity.getRequestStaff().getCurrentPositions() != null && !entity.getRequestStaff().getCurrentPositions().isEmpty()) {
                for (Position p : entity.getRequestStaff().getCurrentPositions()) {
                    if (p.getIsMain() != null && p.getIsMain()) {
                        position = p.getName();
                        break;
                    }
                }
            }
            if (entity.getRequestStaff().getPhoneNumber() != null) {
                phoneNumber = entity.getRequestStaff().getPhoneNumber();
            }

            if (entity.getRequestStaff().getGender() != null) {
                gender = HrConstants.Gender.fromCode(entity.getRequestStaff().getGender()).getName();
            }

            if (entity.getRequestStaff().getBirthPlace() != null) {
                placeOfBirth = entity.getRequestStaff().getBirthPlace();
            }
        }

        if (entity.getFromDate() != null) {
            try {
                fromDate = dateFormat.format(entity.getFromDate());
            } catch (Exception e) {
                logger.error("Lỗi định dạng ngày bắt đầu trong LeaveRequestServiceImpl: {}", e.getMessage());
            }
        }

        if (entity.getFromDate() != null) {
            try {
                toDate = dateFormat.format(entity.getToDate());
            } catch (Exception e) {
                logger.error("Lỗi định dạng ngày kết thúc trong LeaveRequestServiceImpl: {}", e.getMessage());
            }
        }

        if (entity.getRequestReason() != null) {
            reason = entity.getRequestReason();
        }

        today = "TP HCM, " + convertDate(new Date());


        // Thêm các thông tin chi tiết vào map replacements
        replacements.put("fullName", fullName);
        replacements.put("birthDay", birthDay);
        replacements.put("position", position);
        replacements.put("phoneNumber", phoneNumber);
        replacements.put("gender", gender);
        replacements.put("placeOfBirth", placeOfBirth);
        replacements.put("fromDate", fromDate);
        replacements.put("toDate", toDate);
        replacements.put("reason", reason);
        replacements.put("today", today);


        // Thay thế tất cả placeholder trong document
        List<String> replaceBold = Arrays.asList("today");
        Map<String, String> replaceColor = new HashMap<>();
        //replaceColor.put("code", "ff0000"); // Mã "code" có màu đỏ

        // Gọi phương thức thay thế
        replacePlaceholdersInDocument(document, replacements, replaceBold, replaceColor);

        return document;
    }

    @Override
    public Integer saveListLeaveRequest(List<LeaveRequestDto> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<LeaveRequest> leaveRequests = new ArrayList<>();
        for (LeaveRequestDto dto : list) {
            LeaveRequest entity = new LeaveRequest();

            Staff requestStaff = null;
            if (dto.getRequestStaff() != null && dto.getRequestStaff().getStaffCode() != null) {
                List<Staff> staffList = staffRepository.getByCode(dto.getRequestStaff().getStaffCode());
                if (staffList != null && !staffList.isEmpty()) {
                    requestStaff = staffList.get(0);
                }
            }
            if (requestStaff == null) {
                continue;
            }
            entity.setRequestStaff(requestStaff);
            entity.setRequestDate(dto.getRequestDate());
            if (dto.getFromDate() == null || dto.getToDate() == null) {
                continue;
            }
            entity.setFromDate(dto.getFromDate());
            entity.setToDate(dto.getToDate());
            entity.setHalfDayLeave(dto.getHalfDayLeave());
            entity.setHalfDayLeaveStart(dto.getHalfDayLeaveStart());

            if (dto.getHalfDayLeaveStart() != null) {
                entity.setHalfDayLeaveStart(dto.getHalfDayLeaveStart());
                if (entity.getHalfDayLeaveStart()) {
                    if (dto.getShiftWorkStart() != null) {
                        List<ShiftWork> shiftWorkList = shiftWorkRepository.findByCode(dto.getShiftWorkStart().getCode());
                        if (shiftWorkList != null && !shiftWorkList.isEmpty()) {
                            entity.setShiftWorkStart(shiftWorkList.get(0));
                            if (dto.getTimePeriodStart() != null) {
                                ShiftWorkTimePeriod timeSheetShiftWorkPeriod = shiftWorkTimePeriodRepository.findByCode(dto.getTimePeriodStart().getCode());
                                entity.setTimePeriodStart(timeSheetShiftWorkPeriod);
                            }
                        }
                    }
                }
            }

            if (dto.getHalfDayLeaveEnd() != null) {
                entity.setHalfDayLeaveEnd(dto.getHalfDayLeaveEnd());
                if (entity.getHalfDayLeaveEnd()) {
                    if (dto.getShiftWorkEnd() != null) {
                        List<ShiftWork> shiftWorkList = shiftWorkRepository.findByCode(dto.getShiftWorkEnd().getCode());
                        if (shiftWorkList != null && !shiftWorkList.isEmpty()) {
                            entity.setShiftWorkEnd(shiftWorkList.get(0));
                            if (dto.getTimePeriodEnd() != null) {
                                ShiftWorkTimePeriod timeSheetShiftWorkPeriod = shiftWorkTimePeriodRepository.findByCode(dto.getTimePeriodEnd().getCode());
                                entity.setTimePeriodEnd(timeSheetShiftWorkPeriod);
                            }
                        }
                    }
                }
            }
            LeaveType leaveType = null;
            if (dto.getLeaveType() != null) {
                List<LeaveType> leaveRequestList = leaveTypeRepository.findByCode(dto.getLeaveType().getCode());
                if (leaveRequestList != null && !leaveRequestList.isEmpty()) {
                    leaveType = leaveRequestList.get(0);
                }
            }
            entity.setLeaveType(leaveType);
            entity.setRequestReason(dto.getRequestReason());
            entity.setApprovalStatus(dto.getApprovalStatus());

            this.calculateAndSetTotalTime(entity);
            leaveRequests.add(entity);
        }
        leaveRequests = repository.saveAll(leaveRequests);
        return leaveRequests.size();
    }

    @Override
    public LeaveRequestSearchDto getInitialFilter() {
        LeaveRequestSearchDto response = new LeaveRequestSearchDto();

        response.setApprovalStatus(0);
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

    private void replacePlaceholdersInDocument(XWPFDocument document, Map<String, String> replacements, List<String> replaceBold, Map<String, String> replaceColor) {
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replaceInParagraph(paragraph, replacements, replaceBold, replaceColor);
        }

        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    if (cell == null) continue; // <- thêm dòng này
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        if (paragraph == null) continue; // <- thêm dòng này
                        replaceInParagraph(paragraph, replacements, replaceBold, replaceColor);
                    }
                }
            }
        }

    }

    private void replaceInParagraph(XWPFParagraph paragraph,
                                    Map<String, String> replacements,
                                    List<String> replaceBold,
                                    Map<String, String> replaceColor) {
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs == null || runs.isEmpty()) return;

        StringBuilder fullText = new StringBuilder();
        for (XWPFRun run : runs) {
            if (run.getText(0) != null) {
                fullText.append(run.getText(0));
            }
        }

        String text = fullText.toString();

        boolean hasPlaceholder = replacements.keySet().stream()
                .anyMatch(k -> text.contains("{{" + k + "}}"));
        if (!hasPlaceholder) return;

        for (int i = runs.size() - 1; i >= 0; i--) {
            paragraph.removeRun(i);
        }

        int currentIndex = 0;
        while (currentIndex < text.length()) {
            int nextPlaceholderStart = text.indexOf("{{", currentIndex);

            if (nextPlaceholderStart == -1) {
                String remainingText = text.substring(currentIndex);
                if (!remainingText.isEmpty()) {
                    XWPFRun run = paragraph.createRun();
                    run.setText(remainingText);
                    run.setFontFamily("Times New Roman");
                    run.setFontSize(12);
                }
                break;
            }

            if (nextPlaceholderStart > currentIndex) {
                String beforeText = text.substring(currentIndex, nextPlaceholderStart);
                XWPFRun beforeRun = paragraph.createRun();
                beforeRun.setText(beforeText);
                beforeRun.setFontFamily("Times New Roman");
                beforeRun.setFontSize(12);
            }

            int nextPlaceholderEnd = text.indexOf("}}", nextPlaceholderStart);
            if (nextPlaceholderEnd == -1) break;

            String placeholderKey = text.substring(nextPlaceholderStart + 2, nextPlaceholderEnd);
            String replacement = replacements.getOrDefault(placeholderKey, "");

            if (replacement.contains("\n")) {
                String[] lines = replacement.split("\n");
                for (int i = 0; i < lines.length; i++) {
                    XWPFRun lineRun = paragraph.createRun();
                    lineRun.setText(lines[i]);
                    lineRun.setFontFamily("Times New Roman");
                    lineRun.setFontSize(12);

                    if (replaceBold != null && replaceBold.contains(placeholderKey)) {
                        lineRun.setBold(true);
                    }
                    if (replaceColor != null && replaceColor.containsKey(placeholderKey)) {
                        lineRun.setColor(replaceColor.get(placeholderKey));
                    }

                    if (i < lines.length - 1) {
                        lineRun.addBreak();  // Xuống dòng
                    }
                }
            } else {
                XWPFRun replaceRun = paragraph.createRun();
                replaceRun.setText(replacement);
                replaceRun.setFontFamily("Times New Roman");
                replaceRun.setFontSize(12);

                if (replaceBold != null && replaceBold.contains(placeholderKey)) {
                    replaceRun.setBold(true);
                }
                if (replaceColor != null && replaceColor.containsKey(placeholderKey)) {
                    replaceRun.setColor(replaceColor.get(placeholderKey));
                }
            }

            currentIndex = nextPlaceholderEnd + 2;
        }
    }

    public static String convertDate(Date date) {
        if (date == null) return "ngày ... tháng ... năm ....";
        SimpleDateFormat outputFormat = new SimpleDateFormat("'ngày' dd 'tháng' MM 'năm' yyyy");

        return outputFormat.format(date);
    }

}
