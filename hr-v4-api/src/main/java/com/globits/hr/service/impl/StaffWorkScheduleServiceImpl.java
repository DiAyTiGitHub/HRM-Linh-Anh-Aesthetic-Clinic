package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.hr.dto.*;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.*;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.hr.utils.ExportExcelUtil;
import com.globits.hr.utils.RoleUtils;
import com.globits.salary.dto.SalaryPeriodDto;
import com.globits.salary.dto.search.SearchStaffAdvancePaymentDto;
import com.globits.salary.service.SalaryPeriodService;
import com.globits.salary.service.SalaryResultStaffItemService;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;
import com.globits.timesheet.domain.LeaveRequest;
import com.globits.timesheet.domain.LeaveType;
import com.globits.timesheet.domain.OvertimeRequest;
import com.globits.timesheet.domain.TimeSheet;
import com.globits.timesheet.domain.TimeSheetDetail;
import com.globits.timesheet.dto.LeaveRequestDto;
import com.globits.timesheet.dto.TimeSheetDetailDto;
import com.globits.timesheet.dto.TimeSheetStaffDto;
import com.globits.timesheet.dto.search.SearchStaffWorkScheduleDto;
import com.globits.timesheet.repository.LeaveRequestRepository;
import com.globits.timesheet.repository.LeaveTypeRepository;
import com.globits.timesheet.repository.OvertimeRequestRepository;
import com.globits.timesheet.repository.TimeSheetDetailRepository;
import com.globits.timesheet.repository.TimeSheetRepository;
import com.globits.timesheet.service.LeaveRequestService;
import com.globits.timesheet.service.LeaveTypeService;
import com.globits.timesheet.service.PublicHolidayDateService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import org.apache.http.HttpStatus;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class StaffWorkScheduleServiceImpl extends GenericServiceImpl<StaffWorkSchedule, UUID>
		implements StaffWorkScheduleService {
	private static final Logger logger = LoggerFactory.getLogger(StaffWorkScheduleServiceImpl.class);

	@Autowired
	private StaffWorkScheduleRepository staffWorkScheduleRepository;

	@Autowired
	private ShiftWorkRepository shiftWorkRepository;

	@Autowired
	private PublicHolidayDateService publicHolidayDateService;

	@Autowired
	private StaffHierarchyService staffHierarchyService;

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
	private LeaveRequestService leaveRequestService;

	@Autowired
	private LeaveTypeService leaveTypeService;

	@Autowired
	private CalculateStaffWorkTimeService calculateStaffWorkTimeService;

	@Autowired
	private CalculateStaffWorkTimeServiceV2 calculateStaffWorkTimeServiceV2;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private StaffWorkScheduleShiftPeriodRepository staffWorkScheduleShiftPeriodRepository;

	@Autowired
	private ShiftWorkTimePeriodRepository shiftWorkTimePeriodRepository;

	@Autowired
	private StaffMaternityHistoryService staffMaternityHistoryService;

	@Autowired
	private TimeSheetRepository timeSheetRepository;

	@Autowired
	private SalaryPeriodService salaryPeriodService;

	@Autowired
	private SystemConfigService systemConfigService;

	@Autowired
	private HRDepartmentService hrDepartmentService;

	/*
	 * Phân 1 loạt ca làm việc cho 1 loạt nhân viên Hàm log lỗi qua RuntimeException
	 */
	@Override
	public StaffWorkScheduleListDto assignShiftForMultipleStaffs(StaffWorkScheduleListDto dto) {

		// Kiểm tra quyền
		UserDto userDto = userExtService.getCurrentUser();
		Staff currentStaff = userExtService.getCurrentStaffEntity();
		boolean isAdmin = RoleUtils.hasRoleAdmin(userDto);
		boolean isManager = RoleUtils.hasRoleHrManager(userDto);
		boolean isAssignment = RoleUtils.hasRoleHrAssignment(userDto);
		boolean isShiftAssignment = RoleUtils.hasShiftAssignmentPermission(userDto, currentStaff);
		if (!((isAssignment && isShiftAssignment) || isAdmin || isManager)) {
			return null;
		}
		//
		if (dto.getStaffs() == null || dto.getShiftWorks() == null) {
			return null;
		}
		// kiểm tra phòng ban nhân viên có nằm trong phòng ban của ca làm việc
		this.validateStaffInShiftDepartments(dto.getStaffs(), dto.getShiftWorks());

		// Khoảng thời gian phân ca
		Date startDate = dto.getFromDate();
		Date endDate = dto.getToDate();
		LocalDate startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		// Loại tính chấm thời gian chấm công
		Integer timekeepingCalculateType = dto.getTimekeepingCalculationType();
		if (timekeepingCalculateType == null) {
			timekeepingCalculateType = HrConstants.TimekeepingCalculationType.FIRST_IN_LAST_OUT.getValue();
		}

		// Các ngày nghỉ lễ trong khoảng thời gian phân ca
		Set<Date> holidayDates = publicHolidayDateService.getHolidaysInRangeTime(dto.getFromDate(), dto.getToDate());

		LeaveType leaveType = null;

		if (!holidayDates.isEmpty()) {
			List<LeaveType> availableLeaveTypes = leaveTypeRepository
					.findByCode(HrConstants.LeaveTypeCode.PUBLIC_HOLIDAY.getCode());
			if (availableLeaveTypes != null && !availableLeaveTypes.isEmpty()) {
				leaveType = availableLeaveTypes.get(0);
			}
		}

		List<StaffWorkSchedule> schedules = new ArrayList<>();
		for (LocalDate date = startLocalDate; !date.isAfter(endLocalDate); date = date.plusDays(1)) {
			Date workingDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

			for (StaffDto staffDto : dto.getStaffs()) {
				Staff staff = staffRepository.findById(staffDto.getId())
						.orElseThrow(() -> new RuntimeException("Không tìm thấy Staff có id: " + staffDto.getId()));

				for (ShiftWorkDto shiftDto : dto.getShiftWorks()) {
					ShiftWork shiftWork = shiftWorkRepository.findById(shiftDto.getId()).orElseThrow(
							() -> new RuntimeException("Không tìm thấy ShiftWork có id: " + shiftDto.getId()));

					StaffWorkSchedule schedule = null;

					List<StaffWorkSchedule> availableRecords = staffWorkScheduleRepository
							.getByStaffIdAndShiftWorkIdAndWorkingDate(staff.getId(), shiftWork.getId(), workingDate);

					if (availableRecords != null && !availableRecords.isEmpty()) {
						schedule = availableRecords.get(0);
					}

					if (schedule == null)
						schedule = new StaffWorkSchedule();

					schedule.setStaff(staff);
					schedule.setShiftWork(shiftWork);
					schedule.setWorkingDate(workingDate);
					schedule.setOvertimeHours(dto.getOvertimeHours());
					schedule.setWorkingType(dto.getWorkingType());
					schedule.setAllowOneEntryOnly(dto.getAllowOneEntryOnly());

					if (DateTimeUtil.isContainsDate(holidayDates, workingDate)) {
						schedule.setLeaveType(leaveType);
					}

					staffMaternityHistoryService.handleSetDuringPregnancyStatus(schedule);

					if (dto.getNeedManagerApproval() == null || dto.getNeedManagerApproval().equals(false)) {
						schedule.setNeedManagerApproval(false);
					} else {
						schedule.setNeedManagerApproval(true);
					}
					if (dto.getNeedManagerApproval() != null && dto.getNeedManagerApproval().equals(true)) {
						schedule.setApprovalStatus(
								HrConstants.StaffWorkScheduleApprovalStatus.NOT_APPROVED_YET.getValue());
					}

					if (currentStaff != null) {
						schedule.setCoordinator(currentStaff);
					}
					schedule.setTimekeepingCalculationType(timekeepingCalculateType);

					if (schedule.getTotalHours() == null) {
						schedule.setTotalHours(0.0);
					}

					int dayInWeek = getDayOfWeeks(
							schedule.getWorkingDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

					if ((dayInWeek == 2 && Boolean.TRUE.equals(dto.getLoopOnMonday()))
							|| (dayInWeek == 3 && Boolean.TRUE.equals(dto.getLoopOnTuesDay()))
							|| (dayInWeek == 4 && Boolean.TRUE.equals(dto.getLoopOnWednesday()))
							|| (dayInWeek == 5 && Boolean.TRUE.equals(dto.getLoopOnThursday()))
							|| (dayInWeek == 6 && Boolean.TRUE.equals(dto.getLoopOnFriday()))
							|| (dayInWeek == 7 && Boolean.TRUE.equals(dto.getLoopOnSaturday()))
							|| (dayInWeek == 1 && Boolean.TRUE.equals(dto.getLoopOnSunday()))) {

						schedules.add(schedule);
					}
				}
			}
		}

		schedules = staffWorkScheduleRepository.saveAllAndFlush(schedules);

		List<UUID> scheduleIds = new ArrayList<>();

		if (!schedules.isEmpty()) {
			for (StaffWorkSchedule schedule : schedules) {
				scheduleIds.add(schedule.getId());
			}
		}

		entityManager.flush();
		entityManager.clear();

		for (UUID scheduleId : scheduleIds) {
			// Tự động map các lần chấm công vào ca làm việc
			StaffWorkScheduleDto mappedSchedule = calculateStaffWorkTimeServiceV2
					.mapOrphanedTimesheetDetailInDayToSchedule(scheduleId);

			// Thống kê lại kết quả làm việc
			StaffWorkScheduleDto response = calculateStaffWorkTimeServiceV2.calculateStaffWorkTimeAndSave(scheduleId);

		}

		return dto;
	}

	@Override
	public StaffWorkScheduleListDto getInitialShiftAssignmentForm() {
		StaffWorkScheduleListDto response = new StaffWorkScheduleListDto();

		response.setShiftWorks(new ArrayList<>());
		response.setStaffs(new ArrayList<>());

		// Set first and last date of the current month
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date firstDateOfMonth = calendar.getTime();

		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date lastDateOfMonth = calendar.getTime();

		response.setFromDate(firstDateOfMonth);
		response.setToDate(lastDateOfMonth);

		// Check loop options from Monday to Friday
		response.setLoopOnMonday(true);
		response.setLoopOnTuesDay(true);
		response.setLoopOnWednesday(true);
		response.setLoopOnThursday(true);
		response.setLoopOnFriday(true);
		response.setLoopOnSaturday(true);
		response.setTimekeepingCalculationType(HrConstants.TimekeepingCalculationType.FIRST_IN_LAST_OUT.getValue());
		response.setAllowOneEntryOnly(true);

		Staff staff = userExtService.getCurrentStaffEntity();

		if (staff == null || !RoleUtils.isHeadOfDepartment(staff)) {
			return response;
		}

		List<Position> availablePositions = positionRepository.findMainPositionByStaffId(staff.getId());
		if (availablePositions == null || availablePositions.isEmpty()) {
			return response;
		}

		Position mainPosition = availablePositions.get(0);

		if (mainPosition.getDepartment() != null) {
			HRDepartmentDto department = new HRDepartmentDto();
			department.setId(mainPosition.getDepartment().getId());
			department.setName(mainPosition.getDepartment().getName());
			department.setCode(mainPosition.getDepartment().getCode());

			response.setDepartment(department);
		}

		if (mainPosition.getDepartment() != null && mainPosition.getDepartment().getOrganization() != null) {
			HrOrganizationDto organization = new HrOrganizationDto();
			organization.setId(mainPosition.getDepartment().getOrganization().getId());
			organization.setCode(mainPosition.getDepartment().getOrganization().getCode());
			organization.setName(mainPosition.getDepartment().getOrganization().getName());

			response.setOrganization(organization);
		}

		// Tự động điền danh sách nhân viên của phòng ban
		if (response.getOrganization() != null || response.getDepartment() != null) {
			SearchStaffDto searchStaffDto = new SearchStaffDto();
			searchStaffDto.setPageIndex(1);
			searchStaffDto.setPageSize(9999999);

			if (response.getOrganization() != null) {
				searchStaffDto.setOrganizationId(response.getOrganization().getId());
			}
			if (response.getDepartment() != null) {
				searchStaffDto.setDepartmentId(response.getDepartment().getId());
			}

			Page<StaffDto> staffsPage = staffService.searchByPage(searchStaffDto);
			if (staffsPage != null && staffsPage.getContent() != null) {
				response.setStaffs(staffsPage.getContent());
			}
		}

		// if (mainPosition.getDepartment() != null &&
		// mainPosition.getDepartment().getDepartmentShiftWorks() != null &&
		// !mainPosition.getDepartment().getDepartmentShiftWorks().isEmpty()) {
		//
		// for (HrDepartmentShiftWork departmentShiftWork :
		// mainPosition.getDepartment().getDepartmentShiftWorks()) {
		// if (departmentShiftWork.getShiftWork() == null) continue;
		//
		// ShiftWorkDto shiftWork = new
		// ShiftWorkDto(departmentShiftWork.getShiftWork());
		//
		// response.getShiftWorks().add(shiftWork);
		// }
		// }

		return response;
	}

	@Override
	public StaffWorkScheduleDto saveOrUpdate(StaffWorkScheduleDto dto) {
		if (dto == null || dto.getWorkingDate() == null)
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

		entity.setWorkingDate(dto.getWorkingDate());

		ShiftWork shiftWork = null;
		if (dto.getShiftWork() != null && dto.getShiftWork().getId() != null) {
			shiftWork = shiftWorkRepository.findById(dto.getShiftWork().getId()).orElse(null);
		}
		entity.setShiftWork(shiftWork);

		Staff staff = null;
		if (dto.getStaff() != null && dto.getStaff().getId() != null) {
			staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
		}
		entity.setStaff(staff);

		entity.setWorkingStatus(dto.getWorkingStatus());
		entity.setWorkingType(dto.getWorkingType());

		if (entity.getWorkingType() != null && entity.getWorkingType()
				.equals(HrConstants.StaffWorkScheduleWorkingType.EXTENDED_OVERTIME.getValue())) {
			entity.setOvertimeHours(dto.getOvertimeHours());
		}

		if (dto.getCoordinator() != null) {
			Staff coordinator = staffRepository.findById(dto.getCoordinator().getId()).orElse(null);
			if (coordinator == null)
				return null;
			entity.setCoordinator(coordinator);
		} else {
			entity.setCoordinator(null);
		}

		if (dto.getOtEndorser() != null) {
			Staff otEndorser = staffRepository.findById(dto.getOtEndorser().getId()).orElse(null);
			if (otEndorser == null)
				return null;
			entity.setOtEndorser(otEndorser);
		} else {
			entity.setOtEndorser(null);
		}

		entity.setConfirmedOTHoursBeforeShift(dto.getConfirmedOTHoursBeforeShift());
		if (entity.getConfirmedOTHoursBeforeShift() == null)
			entity.setConfirmedOTHoursBeforeShift(0.0);

		entity.setConfirmedOTHoursAfterShift(dto.getConfirmedOTHoursAfterShift());
		if (entity.getConfirmedOTHoursAfterShift() == null)
			entity.setConfirmedOTHoursAfterShift(0.0);

		entity.setAllowOneEntryOnly(dto.getAllowOneEntryOnly());

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

		Integer timekeepingCalculateType = dto.getTimekeepingCalculationType();
		if (timekeepingCalculateType == null) {
			timekeepingCalculateType = HrConstants.TimekeepingCalculationType.FIRST_IN_LAST_OUT.getValue();
		}

		staffMaternityHistoryService.handleSetDuringPregnancyStatus(entity);
		entity.setTimekeepingCalculationType(timekeepingCalculateType);

		if (dto.getNeedManagerApproval() == null || dto.getNeedManagerApproval().equals(false)) {
			entity.setNeedManagerApproval(false);
		} else {
			entity.setNeedManagerApproval(true);
		}
		if (dto.getNeedManagerApproval() != null && dto.getNeedManagerApproval().equals(true)) {
			entity.setApprovalStatus(HrConstants.StaffWorkScheduleApprovalStatus.NOT_APPROVED_YET.getValue());
		}

		// Kiểm tra và đặt trạng thái nghỉ lễ
		if (isCreateNew) {
			// Các ngày nghỉ lễ trong khoảng thời gian phân ca
			Set<Date> holidayDates = publicHolidayDateService.getHolidaysInRangeTime(dto.getWorkingDate(),
					dto.getWorkingDate());

			if (!holidayDates.isEmpty()) {
				LeaveType leaveType = null;

				List<LeaveType> availableLeaveTypes = leaveTypeRepository
						.findByCode(HrConstants.LeaveTypeCode.PUBLIC_HOLIDAY.getCode());
				if (availableLeaveTypes != null && !availableLeaveTypes.isEmpty()) {
					leaveType = availableLeaveTypes.get(0);
				}

				entity.setLeaveType(leaveType);
			}
		}

		StaffWorkSchedule responseEntity = staffWorkScheduleRepository.saveAndFlush(entity);
		UUID scheduleId = responseEntity.getId();

		entityManager.flush();
		entityManager.clear();

		// Tự động map các lần chấm công vào ca làm việc
		StaffWorkScheduleDto mappedSchedule = calculateStaffWorkTimeServiceV2
				.mapOrphanedTimesheetDetailInDayToSchedule(scheduleId);

		// Thống kê lại kết quả lịch làm việc
		StaffWorkScheduleDto response = calculateStaffWorkTimeServiceV2.calculateStaffWorkTimeAndSave(scheduleId);

		entityManager.flush();
		entityManager.clear();

		return response;
	}

	@Override
	@Transactional
	public Integer saveMultiple(StaffWorkScheduleDto dto) {
		Integer count = 0;
		if (dto == null)
			return 0;

		if (dto.getShiftWorks() == null || dto.getShiftWorks().isEmpty()) {
			return 0;
		}

		if (dto.getStaff() == null || dto.getStaff().getId() == null) {
			return 0;
		}

		Staff staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
		if (staff == null)
			return 0;

		if (dto.getWorkingDate() == null) {
			return 0;
		}

		for (ShiftWorkDto swDto : dto.getShiftWorks()) {
			dto.setShiftWork(swDto);
			this.saveOrUpdate(dto);
			count++;
		}
		return count;
	}

	@Override
	public ByteArrayResource exportActualTimesheet(SearchStaffWorkScheduleDto dto) {
		long startTime = System.currentTimeMillis(); // Start time for the entire method
		try {
			InputStream inputStream = new ClassPathResource("Excel/BANG_CHAM_CONG_THUC_TE.xlsx").getInputStream();
			XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

			dto.setExportExcel(true);
			Page<StaffWorkScheduleDto> page = this.searchByPage(dto);

			List<UUID> allStaffIds = this.getAllStaffId(dto);

			List<StaffWorkScheduleDto> datas = page.getContent();

			dto.setStaffIds(allStaffIds);
			List<LeaveRequestDto> leaveRequests = leaveRequestService.getAllLeaveRequestBySearchStaffWorkSchedule(dto);

			ByteArrayResource result = ExportExcelUtil.handleExportActualTimesheet(datas, dto, leaveRequests, workbook);

			long endTime = System.currentTimeMillis(); // End time for the entire method
			logger.info("Total time taken: {} ms", (endTime - startTime));

			return result;
		} catch (Exception e) {
			logger.error("Error during exportActualTimesheet: ", e);
		}
		return null;
	}

	private int getDayOfWeeks(LocalDateTime inputDate) {
		if (inputDate == null)
			return 0;

		LocalDate date = inputDate.toLocalDate();

		DayOfWeek dayOfWeek = date.getDayOfWeek();

		int dayNumber = dayOfWeek.getValue() + 1;
		if (dayNumber == 8)
			dayNumber = 1; // Ensure Sunday is 8 instead of 9

		return dayNumber;
	}

	@Override
	public StaffWorkScheduleDto getById(UUID id) {
		StaffWorkSchedule entity = staffWorkScheduleRepository.findById(id).orElse(null);

		if (entity == null)
			return null;

		StaffWorkScheduleDto result = new StaffWorkScheduleDto(entity);
		List<TimeSheetDetailDto> timeSheetDetails = timeSheetDetailRepository.getByScheduleId(entity.getId());
		result.setTimeSheetDetails(timeSheetDetails);

		return result;
	}

	@Override
	public Page<StaffWorkScheduleDto> searchByPage(SearchStaffWorkScheduleDto dto) {
		if (dto == null) {
			return null;
		}
		int pageIndex = dto.getPageIndex();
		int pageSize = dto.getPageSize();

		if (pageIndex > 0) {
			pageIndex--;
		} else {
			pageIndex = 0;
		}

		UserDto userDto = userExtService.getCurrentUser();
		Staff staff = userExtService.getCurrentStaffEntity();
		boolean isAdmin = RoleUtils.hasRoleAdmin(userDto);
		boolean isManager = RoleUtils.hasRoleHrManager(userDto);
		boolean isAssignment = RoleUtils.hasRoleHrAssignment(userDto);
		boolean isShiftAssignment = RoleUtils.hasShiftAssignmentPermission(userDto, staff);

		if (!(isAdmin || isManager || isAssignment || isShiftAssignment) && staff != null && dto.getStaffId() == null) {
			dto.setStaffId(staff.getId());
		}

		if (isManager || isAdmin) {

		} else if (isAssignment && isShiftAssignment) {
			if (staff != null) {
				List<UUID> listStaffId = new ArrayList<>();
				listStaffId.add(staff.getId());
				List<UUID> staffIds = staffHierarchyService.getAllManagedStaff(staff.getId(), listStaffId);
				dto.setStaffIdList(staffIds);
			} else {
				return Page.empty();
			}
		} else {
			if (staff != null) {
				dto.setStaffId(staff.getId());
			} else {
				return Page.empty();
			}
		}

		String orderBy = " ";
		if (dto.getOrderBy() != null && StringUtils.hasLength(dto.getOrderBy()))
			orderBy = " ORDER BY entity." + dto.getOrderBy() + " ASC ";
		else {
			orderBy = " order by entity.workingDate desc, entity.modifyDate desc ";
		}
		String sqlCount = "select count(entity.id) from StaffWorkSchedule as entity ";
		String sql = "select new com.globits.hr.dto.StaffWorkScheduleDto(entity) from StaffWorkSchedule as entity ";
		String whereClause = "where (1=1) ";

		if (Boolean.FALSE.equals(dto.getIsLeaveSchechual())) {
			whereClause += " and entity.staff.id IS NOT NULL and entity.shiftWork.id IS NOT NULL and entity.workingDate IS NOT NULL ";
		}

		String joinPositionStaff = "";
		boolean hasJoinMainPosition = false;
		if (dto.getPositionTitleId() != null || dto.getDepartmentId() != null || dto.getOrganizationId() != null) {
			joinPositionStaff = " JOIN Position pos ON pos.isMain = true AND pos.staff.id = entity.staff.id ";
			hasJoinMainPosition = true;
		}

		if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
			whereClause += " AND (entity.staff.staffCode like :keyword or entity.staff.firstName like :keyword or entity.staff.lastName like :keyword or entity.staff.displayName like :keyword) ";
		}
		if (dto.getIsFutureDate() != null && dto.getIsFutureDate()) {
			whereClause += " AND (entity.workingDate >= CURRENT_DATE) ";
		}

		if (dto.getStaffId() != null) {
			whereClause += " AND (entity.staff.id =:staffId) ";
		}
		if (dto.getShiftWorkId() != null) {
			whereClause += " AND (entity.shiftWork.id =:shiftWorkId) ";
		}
		if (dto.getWorkingStatus() != null) {
			// Trường hợp không đi làm
			if (dto.getWorkingStatus().equals(HrConstants.StaffWorkScheduleWorkingStatus.NOT_ATTENDANCE.getValue())) {
				whereClause += " and (date(entity.workingDate) <= CURRENT_DATE) and (entity.workingStatus is null or entity.workingStatus = :workingStatus) ";
			} else {
				whereClause += " AND (entity.workingStatus =:workingStatus) ";
			}
		}
		if (dto.getLeaveTypeId() != null && StringUtils.hasText(dto.getLeaveTypeId().toString())) {
			whereClause += " AND (entity.leaveType.id =:leaveTypeId) ";
		}
		if (dto.getFromDate() != null && dto.getToDate() != null) {
			whereClause += " AND date(entity.workingDate) >= date(:fromDate) AND date(entity.workingDate) <= date(:toDate)";
		} else if (dto.getFromDate() != null) {
			whereClause += " AND date(entity.workingDate) >= date(:fromDate)";
		} else if (dto.getToDate() != null) {
			whereClause += " AND date(entity.workingDate) <= date(:toDate)";
		}
		if (isAssignment && isShiftAssignment) {
			if (dto.getStaffIdList() != null && !dto.getStaffIdList().isEmpty()) {
				whereClause += " AND ( entity.staff.id IN (:staffIds) ) ";
			}
		}
		Boolean voided = dto.getVoided();
		if (voided == null) {
			whereClause += " AND (entity.voided is null or entity.voided=false)";
		} else if (!voided) {
			whereClause += " AND (entity.voided=false)";
		} else if (voided) {
			whereClause += " AND (entity.voided=true)";
		}

		if (hasJoinMainPosition) {
			if (dto.getOrganizationId() != null && StringUtils.hasText(dto.getOrganizationId().toString())) {
				whereClause += " AND ( pos.department.organization.id  =: organizationId ) ";
			}
			if (dto.getDepartmentId() != null && StringUtils.hasText(dto.getDepartmentId().toString())) {
				whereClause += " AND ( pos.department.id  =: departmentId ) ";
			}
			if (dto.getPositionTitleId() != null && StringUtils.hasText(dto.getPositionTitleId().toString())) {
				whereClause += " AND ( pos.title.id  =: positionTitleId ) ";
			}
		}

		sql += joinPositionStaff + whereClause + orderBy;
		sqlCount += joinPositionStaff + whereClause;

		Query q = manager.createQuery(sql, StaffWorkScheduleDto.class);
		Query qCount = manager.createQuery(sqlCount);
		if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
			q.setParameter("keyword", "%" + dto.getKeyword() + "%");
			qCount.setParameter("keyword", "%" + dto.getKeyword() + "%");
		}
		if (dto.getStaffId() != null) {
			q.setParameter("staffId", dto.getStaffId());
			qCount.setParameter("staffId", dto.getStaffId());
		}
		if (dto.getShiftWorkId() != null) {
			q.setParameter("shiftWorkId", dto.getShiftWorkId());
			qCount.setParameter("shiftWorkId", dto.getShiftWorkId());
		}
		if (dto.getWorkingStatus() != null) {
			q.setParameter("workingStatus", dto.getWorkingStatus());
			qCount.setParameter("workingStatus", dto.getWorkingStatus());
		}
		if (dto.getFromDate() != null) {
			q.setParameter("fromDate", dto.getFromDate());
			qCount.setParameter("fromDate", dto.getFromDate());
		}
		if (dto.getToDate() != null) {
			q.setParameter("toDate", dto.getToDate());
			qCount.setParameter("toDate", dto.getToDate());
		}
		if (dto.getLeaveTypeId() != null && StringUtils.hasText(dto.getLeaveTypeId().toString())) {
			q.setParameter("leaveTypeId", dto.getLeaveTypeId());
			qCount.setParameter("leaveTypeId", dto.getLeaveTypeId());
		}
		if (isAssignment && isShiftAssignment) {
			if (dto.getStaffIdList() != null && !dto.getStaffIdList().isEmpty()) {
				q.setParameter("staffIds", dto.getStaffIdList());
				qCount.setParameter("staffIds", dto.getStaffIdList());
			}
		}

		if (hasJoinMainPosition) {
			if (dto.getOrganizationId() != null && StringUtils.hasText(dto.getOrganizationId().toString())) {
				q.setParameter("organizationId", dto.getOrganizationId());
				qCount.setParameter("organizationId", dto.getOrganizationId());
			}
			if (dto.getDepartmentId() != null && StringUtils.hasText(dto.getDepartmentId().toString())) {
				q.setParameter("departmentId", dto.getDepartmentId());
				qCount.setParameter("departmentId", dto.getDepartmentId());
			}
			if (dto.getPositionTitleId() != null && StringUtils.hasText(dto.getPositionTitleId().toString())) {
				q.setParameter("positionTitleId", dto.getPositionTitleId());
				qCount.setParameter("positionTitleId", dto.getPositionTitleId());
			}
		}

		if (dto.getExportExcel() != null && dto.getExportExcel()) {
			List<StaffWorkScheduleDto> entities = q.getResultList();
			return new PageImpl<>(entities);
		} else {
			int startPosition = pageIndex * pageSize;
			q.setFirstResult(startPosition);
			q.setMaxResults(pageSize);
			List<StaffWorkScheduleDto> entities = q.getResultList();
			long count = (long) qCount.getSingleResult();
			Pageable pageable = PageRequest.of(pageIndex, pageSize);
			return new PageImpl<>(entities, pageable, count);
		}
	}

	/*
	 * Tìm kiếm các ca làm việc của nhân viên 
	 * đang làm việc trong khoảng thời gian ( fromDate - toDate ) 
	 * xác định nhân viên đang làm việc: ca làm việc không có loại nghỉ
	 */
	@Override
	public List<StaffWorkScheduleDto> searchStaffWorkSchedulesForCRM(SearchStaffWorkScheduleDto dto) {
		if (dto == null) {
			return null;
		}
		// lấy ra list phòng ban thế hệ con và chính nó
		List<HRDepartment> listHRDepartments = hrDepartmentService.findAllDescendantsOfDepartment(dto.getDepartmentId(),
				dto.getDepartmentCode());
		if ((dto.getDepartmentId() != null || dto.getDepartmentCode() != null) && listHRDepartments == null) {
			return Collections.emptyList();
		}
		// lấy ra các vị trí chính của phòng ban -> vị trí đã có staff -> staffId
		// staffCode
		String sql = "select entity " 
				+ "from StaffWorkSchedule entity "
				+ "join entity.staff s " 
				+ "join Position p on p.staff.id = s.id "
				+ "where (entity.voided is null or entity.voided = false) AND (s.voided is null or s.voided = false) "
				+ "and p.isMain = true and (p.voided is null or p.voided = false) "
				+ "and entity.leaveType is null ";

		if (dto.getFromDate() != null) {
			sql += " and DATE(entity.workingDate) >= DATE(:fromDate)";
		}
		if (dto.getToDate() != null) {
			sql += " and DATE(entity.workingDate) <= DATE(:toDate)";
		}
		if (listHRDepartments != null && !listHRDepartments.isEmpty()) {
			sql += " and p.department.id in :departmentIds";
		}
		if (dto.getStaffCode() != null) {
			sql += " and s.staffCode = :staffCode";
		}
		if (dto.getStaffCodes() != null && dto.getStaffCodes().size() > 0) {
			sql += " and s.staffCode in :staffCodes";
		}
		sql += " ORDER BY entity.workingDate DESC ";
		
		Query q = manager.createQuery(sql);

		if (dto.getFromDate() != null) {
			q.setParameter("fromDate", dto.getFromDate());
		}
		if (dto.getToDate() != null) {
			q.setParameter("toDate", dto.getToDate());
		}
		if (listHRDepartments != null && !listHRDepartments.isEmpty()) {
			List<UUID> departmentIds = listHRDepartments.stream().map(HRDepartment::getId).collect(Collectors.toList());
			q.setParameter("departmentIds", departmentIds);
		}
		if (dto.getStaffCode() != null) {
			q.setParameter("staffCode", dto.getStaffCode());
		}
		if (dto.getStaffCodes() != null && dto.getStaffCodes().size() > 0) {
			q.setParameter("staffCodes", dto.getStaffCodes());
		}
		List<StaffWorkSchedule> listStaffWorkSchedule = q.getResultList();
		
		List<StaffWorkScheduleDto> result = new ArrayList<>();

//		for (StaffWorkSchedule s : listStaffWorkSchedule) {
//		    if (s.getShiftWork() == null || s.getShiftWork().getTimePeriods() == null) {
//		        continue;
//		    }
//		    result.add(new StaffWorkScheduleDto(s));
//		}
		
		// tìm kiếm ca làm việc có giai đoạn giao với khoảng thời gian tìm kiếm
		for (StaffWorkSchedule s : listStaffWorkSchedule) {
		    if (s.getShiftWork() == null || s.getShiftWork().getTimePeriods() == null) {
		        continue;
		    }
		    Date workingDate = s.getWorkingDate();
		    if (workingDate == null) continue;

		    for (ShiftWorkTimePeriod tp : s.getShiftWork().getTimePeriods()) {
		        if (tp.getStartTime() == null || tp.getEndTime() == null) continue;

		        Date periodStart = combineDateAndTime(workingDate, tp.getStartTime());
		        Date periodEnd = combineDateAndTime(workingDate, tp.getEndTime());

		        boolean isOverlap = true;

		        if (dto.getFromDate() != null && periodEnd.before(dto.getFromDate())) {
		            isOverlap = false;
		        }
		        if (dto.getToDate() != null && periodStart.after(dto.getToDate())) {
		            isOverlap = false;
		        }

		        if (isOverlap) {
		        	result.add(new StaffWorkScheduleDto(s));
		            break;
		        }
		    }
		}
		return result;
	}
	
	// Kết hợp ngày + giờ
	private Date combineDateAndTime(Date datePart, Date timePart) {
	    Calendar dateCal = Calendar.getInstance();
	    dateCal.setTime(datePart);

	    Calendar timeCal = Calendar.getInstance();
	    timeCal.setTime(timePart);

	    dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
	    dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
	    dateCal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
	    dateCal.set(Calendar.MILLISECOND, 0);

	    return dateCal.getTime();
	}

	@Override
	public List<UUID> getAllStaffId(SearchStaffWorkScheduleDto dto) {
		if (dto == null) {
			return null;
		}

		UserDto userDto = userExtService.getCurrentUser();
		Staff staff = userExtService.getCurrentStaffEntity();
		boolean isAdmin = RoleUtils.hasRoleAdmin(userDto);
		boolean isManager = RoleUtils.hasRoleHrManager(userDto);
		boolean isAssignment = RoleUtils.hasRoleHrAssignment(userDto);
		boolean isShiftAssignment = RoleUtils.hasShiftAssignmentPermission(userDto, staff);

		if (!(isAdmin || isManager || isAssignment || isShiftAssignment) && staff != null && dto.getStaffId() == null) {
			dto.setStaffId(staff.getId());
		}

		if (isManager || isAdmin) {
			// No additional filter
		} else if (isAssignment && isShiftAssignment) {
			if (staff != null) {
				List<UUID> listStaffId = new ArrayList<>();
				listStaffId.add(staff.getId());
				List<UUID> staffIds = staffHierarchyService.getAllManagedStaff(staff.getId(), listStaffId);
				dto.setStaffIdList(staffIds);
			} else {
				return null;
			}
		} else {
			if (staff != null) {
				dto.setStaffId(staff.getId());
			} else {
				return null;
			}
		}

		// --> Thay đổi chỗ này: chỉ select distinct staff.id
		String sql = "select distinct entity.staff.id from StaffWorkSchedule as entity ";
		String whereClause = "where (1=1) ";

		if (Boolean.FALSE.equals(dto.getIsLeaveSchechual())) {
			whereClause += " and entity.staff.id IS NOT NULL and entity.shiftWork.id IS NOT NULL and entity.workingDate IS NOT NULL ";
		}
		String joinPositionStaff = "";
		boolean hasJoinMainPosition = false;
		if (dto.getPositionTitleId() != null || dto.getDepartmentId() != null || dto.getOrganizationId() != null) {
			joinPositionStaff = " JOIN Position pos ON pos.isMain = true AND pos.staff.id = entity.staff.id ";
			hasJoinMainPosition = true;
		}
		if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
			whereClause += " AND (entity.staff.staffCode like :keyword or entity.staff.firstName like :keyword or entity.staff.lastName like :keyword or entity.staff.displayName like :keyword) ";
		}
		if (dto.getIsFutureDate() != null && dto.getIsFutureDate()) {
			whereClause += " AND (entity.workingDate >= CURRENT_DATE) ";
		}
		if (dto.getStaffId() != null) {
			whereClause += " AND (entity.staff.id =:staffId) ";
		}
		if (dto.getShiftWorkId() != null) {
			whereClause += " AND (entity.shiftWork.id =:shiftWorkId) ";
		}
		if (dto.getWorkingStatus() != null) {
			if (dto.getWorkingStatus().equals(HrConstants.StaffWorkScheduleWorkingStatus.NOT_ATTENDANCE.getValue())) {
				whereClause += " and (date(entity.workingDate) <= CURRENT_DATE) and (entity.workingStatus is null or entity.workingStatus = :workingStatus) ";
			} else {
				whereClause += " AND (entity.workingStatus =:workingStatus) ";
			}
		}
		if (dto.getLeaveTypeId() != null && StringUtils.hasText(dto.getLeaveTypeId().toString())) {
			whereClause += " AND (entity.leaveType.id =:leaveTypeId) ";
		}
		if (dto.getFromDate() != null && dto.getToDate() != null) {
			whereClause += " AND date(entity.workingDate) >= date(:fromDate) AND date(entity.workingDate) <= date(:toDate)";
		} else if (dto.getFromDate() != null) {
			whereClause += " AND date(entity.workingDate) >= date(:fromDate)";
		} else if (dto.getToDate() != null) {
			whereClause += " AND date(entity.workingDate) <= date(:toDate)";
		}
		if (isAssignment && isShiftAssignment) {
			if (dto.getStaffIdList() != null && !dto.getStaffIdList().isEmpty()) {
				whereClause += " AND ( entity.staff.id IN (:staffIds) ) ";
			}
		}
		Boolean voided = dto.getVoided();
		if (voided == null) {
			whereClause += " AND (entity.voided is null or entity.voided=false)";
		} else if (!voided) {
			whereClause += " AND (entity.voided=false)";
		} else if (voided) {
			whereClause += " AND (entity.voided=true)";
		}
		if (hasJoinMainPosition) {
			if (dto.getOrganizationId() != null && StringUtils.hasText(dto.getOrganizationId().toString())) {
				whereClause += " AND ( pos.department.organization.id  =:organizationId ) ";
			}
			if (dto.getDepartmentId() != null && StringUtils.hasText(dto.getDepartmentId().toString())) {
				whereClause += " AND ( pos.department.id  =:departmentId ) ";
			}
			if (dto.getPositionTitleId() != null && StringUtils.hasText(dto.getPositionTitleId().toString())) {
				whereClause += " AND ( pos.title.id  =:positionTitleId ) ";
			}
		}

		sql += joinPositionStaff + whereClause;

		Query q = manager.createQuery(sql, UUID.class);

		if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
			q.setParameter("keyword", "%" + dto.getKeyword() + "%");
		}
		if (dto.getStaffId() != null) {
			q.setParameter("staffId", dto.getStaffId());
		}
		if (dto.getShiftWorkId() != null) {
			q.setParameter("shiftWorkId", dto.getShiftWorkId());
		}
		if (dto.getWorkingStatus() != null) {
			q.setParameter("workingStatus", dto.getWorkingStatus());
		}
		if (dto.getFromDate() != null) {
			q.setParameter("fromDate", dto.getFromDate());
		}
		if (dto.getToDate() != null) {
			q.setParameter("toDate", dto.getToDate());
		}
		if (dto.getLeaveTypeId() != null && StringUtils.hasText(dto.getLeaveTypeId().toString())) {
			q.setParameter("leaveTypeId", dto.getLeaveTypeId());
		}
		if (isAssignment && isShiftAssignment) {
			if (dto.getStaffIdList() != null && !dto.getStaffIdList().isEmpty()) {
				q.setParameter("staffIds", dto.getStaffIdList());
			}
		}
		if (hasJoinMainPosition) {
			if (dto.getOrganizationId() != null && StringUtils.hasText(dto.getOrganizationId().toString())) {
				q.setParameter("organizationId", dto.getOrganizationId());
			}
			if (dto.getDepartmentId() != null && StringUtils.hasText(dto.getDepartmentId().toString())) {
				q.setParameter("departmentId", dto.getDepartmentId());
			}
			if (dto.getPositionTitleId() != null && StringUtils.hasText(dto.getPositionTitleId().toString())) {
				q.setParameter("positionTitleId", dto.getPositionTitleId());
			}
		}

		List<UUID> staffIds = q.getResultList();
		return staffIds;
	}

	@Override
	public SearchStaffWorkScheduleDto getInitialFilter() {
		SearchStaffWorkScheduleDto response = new SearchStaffWorkScheduleDto();

		response.setWorkingStatus(0);
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

	@Override
	@Transactional
	public Boolean deleteStaffWorkSchedule(UUID id) {
		StaffWorkSchedule entity = this.findById(id);
		if (entity == null) {
			return false;
		}
		if (Boolean.TRUE.equals(entity.getIsLocked())) {
			return false;
		}

		Staff staff = entity.getStaff();
		Date workingDate = entity.getWorkingDate();

		// 1. get all TimeSheet need delete
		List<TimeSheet> timeSheetsToDelete = new ArrayList<>();
		if (entity.getTimesheetDetails() != null) {
			for (TimeSheetDetail detail : entity.getTimesheetDetails()) {
				if (detail.getTimeSheet() != null) {
					timeSheetsToDelete.add(detail.getTimeSheet());
				}
			}
		}

		// 2. delete StaffWorkSchedule
		staffWorkScheduleRepository.delete(entity);

		// 3. delete TimeSheets with try-catch
		for (TimeSheet ts : timeSheetsToDelete) {
			try {
				timeSheetRepository.delete(ts);
			} catch (Exception ex) {
				logger.error("Failed to delete TimeSheet id {}: {}", ts.getId(), ex.getMessage());
				// continue deleting remaining records
			}
		}
		// 4. Cập nhật dữ liệu chấm công cho payslips
		salaryResultStaffItemService.updateTimekeepingDataForPayslips(staff, workingDate);

		return true;
	}

	@Override
	@Modifying
	public Boolean deleteMultiple(List<UUID> ids) {
		if (ids == null)
			return false;
		boolean isValid = true;
		for (UUID itemId : ids) {
			boolean deleteRes = this.deleteStaffWorkSchedule(itemId);
			if (!deleteRes)
				isValid = false;
		}
		return isValid;
	}

	private int getMinOTMinutesConfig() {
		SystemConfigDto minOTMinutesConfig = systemConfigService
				.getByKeyCode(HrConstants.SystemConfigCode.MIN_OT_MINUTES_TO_SHOW_CONFIRM.getCode());

		if (minOTMinutesConfig == null || !StringUtils.hasText(minOTMinutesConfig.getConfigValue())) {
			return 0;
		}

		String configValue = minOTMinutesConfig.getConfigValue();

		try {
			return Integer.parseInt(configValue);
		} catch (Exception exception) {
			exception.printStackTrace();
			return 0;
		}
	}

	// Danh sách Kết quả chấm công làm việc theo lịch được phân
	@Override
	public Page<StaffWorkScheduleDto> pagingOverTimeSchedules(SearchStaffWorkScheduleDto dto) {
		if (dto == null) {
			return null;
		}

		if (dto.getFromDate() != null) {
			dto.setFromDate(DateTimeUtil.getStartOfDay(dto.getFromDate()));
		}
		if (dto.getToDate() != null) {
			dto.setToDate(DateTimeUtil.getEndOfDay(dto.getToDate()));
		}

		int pageIndex = dto.getPageIndex();
		int pageSize = dto.getPageSize();

		if (pageIndex > 0) {
			pageIndex--;
		} else {
			pageIndex = 0;
		}
		if (dto.getStaffId() != null) {
			dto.setStaffId(dto.getStaffId());
		}

		boolean isRoleManager = false;
		UserDto user = userExtService.getCurrentUser();
		if (user != null && user.getRoles() != null && !user.getRoles().isEmpty()) {
			for (RoleDto item : user.getRoles()) {
				if (item.getName() != null
						&& ("ROLE_ADMIN".equals(item.getName()) || "HR_MANAGER".equals(item.getName()))) {
					isRoleManager = true;
				}
			}
		}
		if (!isRoleManager && dto.getStaffId() == null) {
			StaffDto currentStaff = userExtService.getCurrentStaff();
			dto.setStaffId(currentStaff.getId());
		}

		String orderBy = " ";
		if (dto.getOrderBy() != null && StringUtils.hasLength(dto.getOrderBy()))
			orderBy = " ORDER BY entity." + dto.getOrderBy() + " ASC ";
		else {
			orderBy = " order by entity.workingDate desc ";
		}

		String sqlCount = "select count(entity.id) from StaffWorkSchedule as entity ";
		String sql = "select new com.globits.hr.dto.StaffWorkScheduleDto(entity) from StaffWorkSchedule as entity ";
		String whereClause = " where (1=1) " + "and (date(entity.workingDate) <= CURRENT_DATE) "
				+ "and (entity.earlyArrivalMinutes >= :minOTMinutes or entity.lateExitMinutes >= :minOTMinutes) "
				+ "and (entity.staff.skipOvertimeCount is null or entity.staff.skipOvertimeCount = false) ";

		String joinPositionStaff = "";
		boolean hasJoinMainPosition = false;
		if (dto.getPositionTitleId() != null || dto.getDepartmentId() != null || dto.getOrganizationId() != null) {
			joinPositionStaff += " JOIN Position pos ON pos.isMain = true AND pos.staff.id = entity.staff.id ";
			hasJoinMainPosition = true;
		}
		if (dto.getStaffId() != null) {
			whereClause += " AND (entity.staff.id =:staffId) ";
		}
		if (dto.getCoordinatorId() != null) {
			whereClause += " AND (entity.coordinator.id = :coordinatorId) ";
		}
		if (dto.getShiftWorkId() != null) {
			whereClause += " AND (entity.shiftWork.id =:shiftWorkId) ";
		}
		if (dto.getWorkingStatus() != null) {
			whereClause += " AND (entity.workingStatus =:workingStatus) ";
		}
		if (dto.getFromDate() != null && dto.getToDate() == null) {
			whereClause += " AND date(entity.workingDate) >= date(:fromDate) ";
		} else if (dto.getFromDate() == null && dto.getToDate() != null) {
			whereClause += " AND date(entity.workingDate) <= date(:toDate) ";
		} else if (dto.getFromDate() != null && dto.getToDate() != null) {
			whereClause += " AND date(entity.workingDate) >= date(:fromDate) AND date(entity.workingDate) <= date(:toDate) ";
		}
		Boolean voided = dto.getVoided();
		if (voided == null) {
			whereClause += " AND (entity.voided is null or entity.voided=false)";
		} else if (!voided) {
			whereClause += " AND (entity.voided=false)";
		} else if (voided) {
			whereClause += " AND (entity.voided=true)";
		}
		if (hasJoinMainPosition) {
			if (dto.getOrganizationId() != null && StringUtils.hasText(dto.getOrganizationId().toString())) {
				whereClause += " AND pos.department.organization.id = :organizationId ";
			}
			if (dto.getDepartmentId() != null && StringUtils.hasText(dto.getDepartmentId().toString())) {
				whereClause += " AND pos.department.id = :departmentId ";
			}
			if (dto.getPositionTitleId() != null && StringUtils.hasText(dto.getPositionTitleId().toString())) {
				whereClause += " AND pos.title.id = :positionTitleId ";
			}
		}

		sql += joinPositionStaff + whereClause + orderBy;
		sqlCount += joinPositionStaff + whereClause;

		Query q = manager.createQuery(sql, StaffWorkScheduleDto.class);
		Query qCount = manager.createQuery(sqlCount);

		Integer minOTMinutes = this.getMinOTMinutesConfig();
		q.setParameter("minOTMinutes", minOTMinutes);
		qCount.setParameter("minOTMinutes", minOTMinutes);

		if (dto.getStaffId() != null) {
			q.setParameter("staffId", dto.getStaffId());
			qCount.setParameter("staffId", dto.getStaffId());
		}
		if (dto.getCoordinatorId() != null) {
			q.setParameter("coordinatorId", dto.getCoordinatorId());
			qCount.setParameter("coordinatorId", dto.getCoordinatorId());
		}
		if (dto.getShiftWorkId() != null) {
			q.setParameter("shiftWorkId", dto.getShiftWorkId());
			qCount.setParameter("shiftWorkId", dto.getShiftWorkId());
		}
		if (dto.getWorkingStatus() != null) {
			q.setParameter("workingStatus", dto.getWorkingStatus());
			qCount.setParameter("workingStatus", dto.getWorkingStatus());
		}
		if (dto.getFromDate() != null) {
			q.setParameter("fromDate", dto.getFromDate());
			qCount.setParameter("fromDate", dto.getFromDate());
		}
		if (dto.getToDate() != null) {
			q.setParameter("toDate", dto.getToDate());
			qCount.setParameter("toDate", dto.getToDate());
		}
		if (hasJoinMainPosition) {
			if (dto.getOrganizationId() != null && StringUtils.hasText(dto.getOrganizationId().toString())) {
				q.setParameter("organizationId", dto.getOrganizationId());
				qCount.setParameter("organizationId", dto.getOrganizationId());
			}
			if (dto.getDepartmentId() != null && StringUtils.hasText(dto.getDepartmentId().toString())) {
				q.setParameter("departmentId", dto.getDepartmentId());
				qCount.setParameter("departmentId", dto.getDepartmentId());
			}
			if (dto.getPositionTitleId() != null && StringUtils.hasText(dto.getPositionTitleId().toString())) {
				q.setParameter("positionTitleId", dto.getPositionTitleId());
				qCount.setParameter("positionTitleId", dto.getPositionTitleId());
			}
		}
		if (dto.getExportExcel() != null && dto.getExportExcel()) {
			List<StaffWorkScheduleDto> entities = q.getResultList();
			return new PageImpl<>(entities);
		} else {
			int startPosition = pageIndex * pageSize;
			q.setFirstResult(startPosition);
			q.setMaxResults(pageSize);
			List<StaffWorkScheduleDto> entities = q.getResultList();
			long count = (long) qCount.getSingleResult();
			Pageable pageable = PageRequest.of(pageIndex, pageSize);
			return new PageImpl<>(entities, pageable, count);
		}
	}

	// Xác nhận số giờ OT
	@Override
	public StaffWorkScheduleDto updateScheduleOTHours(StaffWorkScheduleDto scheduleDto) {
		if (scheduleDto == null || scheduleDto.getId() == null || scheduleDto.getStaff() == null)
			return null;

		StaffWorkSchedule staffWorkSchedule = staffWorkScheduleRepository.findById(scheduleDto.getId()).orElse(null);
		if (staffWorkSchedule == null)
			return null;

		// Các quyền
		boolean isRoleUser = false;
		boolean isRoleAdmin = false;
		boolean isRoleManager = false;

		UserDto userDto = userExtService.getCurrentUser();
		if (userDto != null && userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
			for (RoleDto item : userDto.getRoles()) {
				if (item.getName() != null && (HrConstants.ROLE_ADMIN.equals(item.getName())
						|| HrConstants.ROLE_SUPER_ADMIN.equals(item.getName()))) {
					isRoleAdmin = true;
				}
				if (item.getName() != null && HrConstants.HR_MANAGER.equals(item.getName())) {
					isRoleManager = true;
				}
				if (item.getName() != null && (HrConstants.HR_USER.equals(item.getName())
						|| HrConstants.ROLE_USER.equals(item.getName()))) {
					isRoleUser = true;
				}
			}
		}
		if (isRoleAdmin) {
			isRoleUser = false;
		} else if (isRoleManager) {
			isRoleUser = false;

		}

		Staff otEndorser = null;

//        if ((isRoleAdmin || isRoleManager) && scheduleDto.getOtEndorser() != null) {
//            otEndorser = staffRepository.findById(scheduleDto.getOtEndorser().getId()).orElse(null);
//        }

		if (otEndorser == null)
			otEndorser = userExtService.getCurrentStaffEntity();

		// Duyệt các yêu cầu tính OT cho ca làm việc này
		List<OvertimeRequest> overtimeRequests = overtimeRequestRepository.findByScheduleId(staffWorkSchedule.getId());
		if (overtimeRequests != null && !overtimeRequests.isEmpty()) {
			for (OvertimeRequest overtimeRequest : overtimeRequests) {
				overtimeRequest.setApprovalStatus(HrConstants.OvertimeRequestApprovalStatus.APPROVED.getValue());
			}

			overtimeRequests = overtimeRequestRepository.saveAllAndFlush(overtimeRequests);
		}

		staffWorkSchedule.setOtEndorser(otEndorser);

		staffWorkSchedule.setConfirmedOTHoursAfterShift(scheduleDto.getConfirmedOTHoursAfterShift());
		staffWorkSchedule.setConfirmedOTHoursBeforeShift(scheduleDto.getConfirmedOTHoursBeforeShift());

		staffWorkSchedule = staffWorkScheduleRepository.saveAndFlush(staffWorkSchedule);

		manager.flush();

		// Thống kê kết quả làm việc
		// calculateStaffWorkTimeService.calculateStaffWorkTime(staffWorkSchedule.getId());
		StaffWorkScheduleDto result = calculateStaffWorkTimeServiceV2
				.calculateStaffWorkTimeAndSave(staffWorkSchedule.getId());

		manager.flush();

		// staffWorkSchedule =
		// staffWorkScheduleRepository.findById(staffWorkSchedule.getId()).orElse(null);

		if (scheduleDto != null && scheduleDto.getStaff() != null && scheduleDto.getStaff().getId() != null)
			// Cập nhật phiếu lương của nhân viên khi chấm công: số giờ làm việc, số ca làm
			// việc
			salaryResultStaffItemService.updateTimekeepingDataForPayslips(scheduleDto.getStaff().getId(),
					staffWorkSchedule.getWorkingDate());

		return new StaffWorkScheduleDto(staffWorkSchedule);
	}

	// Tính số giờ công quy đổi
	private double calculateConvertedWorkingHoursForOneEntry(StaffWorkSchedule staffWorkSchedule, Date firstCheckin,
			Date lastCheckout) {
		if (staffWorkSchedule.getTotalHours() == null || staffWorkSchedule.getShiftWork() == null
				|| staffWorkSchedule.getShiftWork().getConvertedWorkingHours() == null)
			return 0D;

		// Nếu nhân viên chưa có đủ CheckIn và CheckOut => Không tính số giờ công quy
		// đổi
		if (firstCheckin == null || lastCheckout == null)
			return 0D;

		// double totalHours = staffWorkSchedule.getTotalHours();
		// // Lấy các giá trị, mặc định là 0 nếu bị null
		// int earlyArrivalMinutes = staffWorkSchedule.getEarlyArrivalMinutes() != null
		// ? staffWorkSchedule.getEarlyArrivalMinutes()
		// : 0;
		int lateArrivalMinutes = staffWorkSchedule.getLateArrivalMinutes() != null
				? staffWorkSchedule.getLateArrivalMinutes()
				: 0;
		int earlyExitMinutes = staffWorkSchedule.getEarlyExitMinutes() != null ? staffWorkSchedule.getEarlyExitMinutes()
				: 0;

		// int lateExitMinutes = staffWorkSchedule.getLateExitMinutes() != null ?
		// staffWorkSchedule.getLateExitMinutes()
		// : 0;
		//
		// Double otBeforeShift = staffWorkSchedule.getConfirmedOTHoursBeforeShift() !=
		// null
		// ? staffWorkSchedule.getConfirmedOTHoursBeforeShift()
		// : 0.0;
		// Double otAfterShift = staffWorkSchedule.getConfirmedOTHoursAfterShift() !=
		// null
		// ? staffWorkSchedule.getConfirmedOTHoursAfterShift()
		// : 0.0;

		// Mặc định 8 giờ
		double maximumConversionHours = staffWorkSchedule.getShiftWork().getConvertedWorkingHours();

		// Cộng thời gian đi sớm, về muộn, thời gian OT
		// maximumConversionHours += (earlyArrivalMinutes + lateExitMinutes) / 60.0;
		// Cộng thời gian OT
		// maximumConversionHours += otBeforeShift + otAfterShift;
		// totalHours += otBeforeShift + otAfterShift;
		// Trừ thời gian đi muộn, về sớm
		maximumConversionHours -= (lateArrivalMinutes + earlyExitMinutes) / 60.0;
		if (maximumConversionHours >= HrConstants.STANDARD_CONVERTED_WORKING_HOURS * HrConstants.WORK_ENOUGH_THRESHOLD)
			maximumConversionHours = HrConstants.STANDARD_CONVERTED_WORKING_HOURS;
		// Cập nhật giá trị tổng giờ thực tế
		// staffWorkSchedule.setTotalHours(totalHours);
		// Cập nhật giá trị chuyển đổi

		if (maximumConversionHours < 0)
			maximumConversionHours = 0;

		return maximumConversionHours;
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

		double totalConvertedHours = shiftWork.getConvertedWorkingHours() != null ? shiftWork.getConvertedWorkingHours()
				: 0.0;

		// Sắp xếp các lần chấm công theo thời gian bắt đầu
		timeSheetDetails.sort(new Comparator<TimeSheetDetail>() {
			@Override
			public int compare(TimeSheetDetail o1, TimeSheetDetail o2) {
				if (o1.getStartTime() == null)
					return 1;
				if (o2.getStartTime() == null)
					return -1;
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

	// tổng số giờ nhân viên đã làm trong 1 giai đoạn của ca
	private double totalCheckedHoursByPeriod(ShiftWorkTimePeriod shiftWorkTimePeriod,
			List<TimeSheetDetail> timeSheetDetails) {
		double totalCheckedHours = 0.0;

		if (CollectionUtils.isEmpty(timeSheetDetails)) {
			return totalCheckedHours;
		}
		// Chỉ lấy giờ - phút - giây, bỏ phần ngày tháng
		LocalTime shiftStart = shiftWorkTimePeriod.getStartTime().toInstant().atZone(ZoneId.systemDefault())
				.toLocalTime();
		LocalTime shiftEnd = shiftWorkTimePeriod.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

		// Sắp xếp danh sách theo startTime để xử lý khoảng trùng
		timeSheetDetails.sort(Comparator.comparing(TimeSheetDetail::getStartTime));

		LocalTime lastEndTime = null; // Thời điểm kết thúc của khoảng thời gian trước

		for (TimeSheetDetail detail : timeSheetDetails) {
			if (detail.getStartTime() == null || detail.getEndTime() == null) {
				continue; // Bỏ qua nếu thiếu dữ liệu
			}

			LocalTime start = detail.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
			LocalTime end = detail.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

			// Giới hạn thời gian chỉ nằm trong ca làm việc
			if (end.isBefore(shiftStart) || start.isAfter(shiftEnd)) {
				continue; // Bỏ qua nếu toàn bộ thời gian ngoài ca làm việc
			}
			if (start.isBefore(shiftStart)) {
				start = shiftStart; // Điều chỉnh nếu bắt đầu trước ca
			}
			if (end.isAfter(shiftEnd)) {
				end = shiftEnd; // Điều chỉnh nếu kết thúc sau ca
			}

			// Kiểm tra trùng lặp với khoảng thời gian trước
			if (lastEndTime != null && start.isBefore(lastEndTime)) {
				if (end.isAfter(lastEndTime)) {
					totalCheckedHours += Duration.between(lastEndTime, end).toMinutes() / 60.0;
					lastEndTime = end;
				}
			} else {
				totalCheckedHours += Duration.between(start, end).toMinutes() / 60.0;
				lastEndTime = end;
			}
		}

		return totalCheckedHours;
	}

	/*
	 * staffWorkSchedule: ca lam viec da phan startTimePeriod: thoi gian bat dau ca
	 * dau tien - lay ca co thoi gian vao som nhat endTimePeriod: thoi gian bat dau
	 * ca cuoi cung - lay ca co thoi gian vao som nhat
	 */
	private void checkLateForWorkOnlyOne(StaffWorkSchedule staffWorkSchedule, LocalTime startTimePeriodFirst,
			Date firstCheckin, LocalTime allowedLateTimePeriodFirst) {

		if (firstCheckin != null && startTimePeriodFirst != null) {
			ZoneId zone = ZoneId.systemDefault();
			LocalTime checkinTime = firstCheckin.toInstant().atZone(zone).toLocalTime();
			LocalTime shiftStart = startTimePeriodFirst;
			// LocalTime allowedLateTime = allowedLateTimePeriodFirst;
			if (checkinTime.isAfter(shiftStart)) { // Đi làm muộn
				// số phút đi làm muộn
				if (checkinTime.isAfter(allowedLateTimePeriodFirst))
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

	private void checkEarlyExitOnlyOne(StaffWorkSchedule staffWorkSchedule, LocalTime endTimePeriodLast,
			Date lastCheckout) {
		if (lastCheckout != null && endTimePeriodLast != null) {
			ZoneId zone = ZoneId.systemDefault();
			LocalTime checkoutTime = lastCheckout.toInstant().atZone(zone).toLocalTime();
			LocalTime shiftEnd = endTimePeriodLast;

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

	@Override
	public List<StaffWorkSchedule> changeStatusFromLeaveRequest(LeaveRequest leaveRequest) {
		if (leaveRequest == null || leaveRequest.getFromDate() == null || leaveRequest.getToDate() == null
				|| leaveRequest.getRequestStaff() == null || leaveRequest.getLeaveType() == null
				|| Boolean.FALSE.equals(leaveRequest.getLeaveType().getIsPaid())) {
			return Collections.emptyList();
		}

		List<StaffWorkSchedule> schedules = staffWorkScheduleRepository.findByStaffIdAndWorkingDateBetween(
				leaveRequest.getRequestStaff().getId(), leaveRequest.getFromDate(), leaveRequest.getToDate());

		if (schedules.isEmpty()) {
			return Collections.emptyList();
		}

		LeaveType fullShiftLeaveType = leaveTypeService.getFullLeaveOfLeaveType(leaveRequest.getLeaveType());
		LeaveType halfShiftLeaveType = leaveTypeService.getHalfLeaveOfLeaveType(leaveRequest.getLeaveType());

		for (StaffWorkSchedule schedule : schedules) {
			ShiftWork shiftWork = schedule.getShiftWork();
			if (shiftWork == null || shiftWork.getTimePeriods().isEmpty())
				continue;

			Date workingDate = schedule.getWorkingDate();

			int overlapCount = 0;
			List<ShiftWorkTimePeriod> overlappedCodes = new ArrayList<>();

			for (ShiftWorkTimePeriod period : shiftWork.getTimePeriods()) {
				Date periodStart = withDate(workingDate, period.getStartTime());
				Date periodEnd = withDate(workingDate, period.getEndTime());

				boolean isOverlapping = leaveRequest.getFromDate().before(periodEnd)
						&& leaveRequest.getToDate().after(periodStart);

				if (isOverlapping) {
					overlapCount++;
					overlappedCodes.add(period);
				}
			}

//            if (overlapCount == shiftWork.getTimePeriods().size()) {
//                schedule.setLeaveType(fullShiftLeaveType);
//                schedule.setLeavePeriod(null);
//            } else if (overlapCount > 0) {
//                schedule.setLeaveType(halfShiftLeaveType);
//                schedule.setLeavePeriod(overlappedCodes.get(0));
//            }
		}

		return staffWorkScheduleRepository.saveAll(schedules);
	}

	public static Date withDate(Date datePart, Date timePart) {
		Calendar dateCal = Calendar.getInstance();
		dateCal.setTime(datePart);

		Calendar timeCal = Calendar.getInstance();
		timeCal.setTime(timePart);

		dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
		dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
		dateCal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
		dateCal.set(Calendar.MILLISECOND, 0);

		return dateCal.getTime();
	}

	@Transactional
	@Override
	public Integer saveListImportExcel(List<StaffWorkScheduleDto> list) {
		if (list.isEmpty()) {
			return 0; // Return 0 for empty list
		}

		// Fetch all ShiftWork entities and map to DTOs with only id, code, name
		List<ShiftWork> shiftWorks = shiftWorkRepository.findAll();
		List<ShiftWorkDto> shiftWorkDtos = shiftWorks.stream().map(sw -> {
			ShiftWorkDto dto = new ShiftWorkDto();
			dto.setId(sw.getId());
			dto.setCode(sw.getCode());
			dto.setName(sw.getName());
			return dto;
		}).collect(Collectors.toList());
		Map<String, ShiftWorkDto> shiftWorkMap = shiftWorkDtos.stream()
				.filter(sw -> sw.getCode() != null && !sw.getCode().trim().isEmpty())
				.collect(Collectors.toMap(ShiftWorkDto::getCode, sw -> sw, (existing, replacement) -> existing // Keep
																												// existing
																												// entry
																												// if
																												// duplicate
																												// key
				));

		// Fetch all Staff entities and map to DTOs with only id, staffCode, name
		List<Staff> staffs = staffRepository.findAll();
		List<StaffDto> staffDtos = staffs.stream().map(s -> {
			StaffDto dto = new StaffDto();
			dto.setId(s.getId());
			dto.setStaffCode(s.getStaffCode());
			dto.setDisplayName(s.getDisplayName());
			return dto;
		}).collect(Collectors.toList());
		Map<String, StaffDto> staffMap = staffDtos.stream()
				.filter(s -> s.getStaffCode() != null && !s.getStaffCode().trim().isEmpty())
				.collect(Collectors.toMap(StaffDto::getStaffCode, s -> s, (existing, replacement) -> existing // Keep
																												// existing
																												// entry
																												// if
																												// duplicate
																												// key
				));

		int count = 0;
		List<StaffWorkScheduleDto> batch = new ArrayList<>();

		for (StaffWorkScheduleDto staffWorkScheduleDto : list) {
			StaffDto staff = staffMap.get(staffWorkScheduleDto.getStaffCode());
			ShiftWorkDto shiftWork = shiftWorkMap.get(staffWorkScheduleDto.getShiftWorkCode());
			Date date = staffWorkScheduleDto.getWorkingDate();

			if (staff == null || shiftWork == null || date == null) {
//                logger.warn("Skipping invalid row: staffCode={}, shiftWorkCode={}, date={}",
//                        staffWorkScheduleDto.getStaffCode(), staffWorkScheduleDto.getShiftWorkCode(), date);
				continue; // Skip invalid row
			}

			StaffWorkScheduleDto staffWorkSchedule = new StaffWorkScheduleDto();
			staffWorkSchedule.setStaff(staff); // Set StaffDto directly
			staffWorkSchedule.setShiftWork(shiftWork); // Set ShiftWorkDto directly
			staffWorkSchedule.setWorkingDate(date);
			staffWorkSchedule.setTimekeepingCalculationType(staffWorkScheduleDto.getTimekeepingCalculationType());
			staffWorkSchedule.setAllowOneEntryOnly(staffWorkScheduleDto.getAllowOneEntryOnly());

			batch.add(staffWorkSchedule);

			if (batch.size() >= 50) { // Batch size of 50
				batch.forEach(this::saveOrUpdate);
				batch.clear();
				count += 50;
			}
		}

		if (!batch.isEmpty()) {
			batch.forEach(this::saveOrUpdate);
			count += batch.size();
		}

		return count;
	}

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

	public void validateStaffInShiftDepartments(List<StaffDto> staffs, List<ShiftWorkDto> shifts) {
		List<UUID> staffIds = staffs.stream().map(StaffDto::getId).collect(Collectors.toList());

		List<UUID> shiftIds = shifts.stream().map(ShiftWorkDto::getId).collect(Collectors.toList());

		Map<UUID, List<UUID>> staffDeptMap = getStaffDepartments(staffIds);
		Map<UUID, List<UUID>> shiftDeptMap = getShiftDepartments(shiftIds);

		for (ShiftWorkDto shift : shifts) {
			List<UUID> allowedDepts = shiftDeptMap.getOrDefault(shift.getId(), Collections.emptyList());
			for (StaffDto staff : staffs) {
				List<UUID> staffDepts = staffDeptMap.getOrDefault(staff.getId(), Collections.emptyList());

				boolean match = staffDepts.stream().anyMatch(allowedDepts::contains);

				if (!match) {
					throw new RuntimeException(
							String.format("Nhân viên [%s] không thuộc phòng ban được phân của ca [%s]",
									staff.getDisplayName(), shift.getName()));
				}
			}
		}
	}

	private Map<UUID, UUID> getStaffMainDepartments(List<UUID> staffIds) {
		return hRDepartmentRepository.getMainDepartmentIdMapByStaffIds(staffIds).stream()
				.collect(Collectors.toMap(row -> (UUID) row[0], row -> (UUID) row[1]));
	}

	private Map<UUID, List<UUID>> getStaffDepartments(List<UUID> staffIds) {
		List<Object[]> rows = hRDepartmentRepository.getDepartmentIdMapByStaffIds(staffIds);
		return rows.stream().collect(Collectors.groupingBy(row -> (UUID) row[0], // staffId
				Collectors.mapping(row -> (UUID) row[1], Collectors.toList()) // list of deptIds
		));
	}

	private Map<UUID, List<UUID>> getShiftDepartments(List<UUID> shiftIds) {
		return hRDepartmentRepository.getShiftWorkAndDepartmentPairs(shiftIds).stream().collect(Collectors
				.groupingBy(row -> (UUID) row[0], Collectors.mapping(row -> (UUID) row[1], Collectors.toList())));
	}

	@Override
	public List<StaffWorkScheduleDto> getSchedulesInDayOfStaff(SearchStaffWorkScheduleDto dto) {
		List<StaffWorkScheduleDto> response = new ArrayList<>();

		if (dto == null || dto.getWorkingDate() == null || dto.getStaffId() == null)
			return response;

		List<StaffWorkSchedule> availableSchedules = staffWorkScheduleRepository
				.getByStaffAndWorkingDate(dto.getStaffId(), dto.getWorkingDate());

		if (availableSchedules == null || availableSchedules.isEmpty()) {
			return response;
		}

		for (StaffWorkSchedule schedule : availableSchedules) {
			StaffWorkScheduleDto responseItem = new StaffWorkScheduleDto(schedule);

			response.add(responseItem);
		}

		return response;
	}

	@Override
	public ApiResponse<List<StaffWorkScheduleDto>> generateAndMarkSchedulesFromApprovedRequest(UUID leaveRequestId,
			Integer status) {
		List<StaffWorkScheduleDto> response = new ArrayList<>();
		List<StaffWorkSchedule> listSave = new ArrayList<>();

		if (leaveRequestId == null) {
			return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không có mã của bản ghi xin nghỉ phép!", response);
		}

		LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId).orElse(null);

		if (leaveRequest == null || leaveRequest.getFromDate() == null || leaveRequest.getToDate() == null
				|| leaveRequest.getRequestStaff() == null) {
			return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không có dữ liệu xin nghỉ phép hợp lệ!", response);
		}

		leaveRequest.setApprovalStatus(status);
		leaveRequestRepository.saveAndFlush(leaveRequest); // đảm bảo status được cập nhật

		if (leaveRequest.getApprovalStatus().equals(HrConstants.AbsenceRequestApprovalStatus.APPROVED.getValue())) {
			List<LocalDate> dateList = getDatesBetween(leaveRequest.getFromDate(), leaveRequest.getToDate());
			if (!CollectionUtils.isEmpty(dateList)) {
				for (LocalDate date : dateList) {
					Date workingDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
					List<StaffWorkSchedule> staffWorkSchedules = staffWorkScheduleRepository
							.findAllByStaffIdAndWorkingDate(leaveRequest.getRequestStaff().getId(), workingDate,
									workingDate);

					if (!CollectionUtils.isEmpty(staffWorkSchedules)) {
						for (StaffWorkSchedule staffWorkSchedule : staffWorkSchedules) {
							staffWorkSchedule.setLeaveType(leaveRequest.getLeaveType());
							staffWorkScheduleRepository.save(staffWorkSchedule); // Lưu trước, chưa gọi apply
							listSave.add(staffWorkSchedule);
						}
					} else {
						StaffWorkSchedule newSchedule = createNewScheduleAndMarkLeaveType(workingDate,
								leaveRequest.getRequestStaff(), leaveRequest.getLeaveType());
						staffWorkScheduleRepository.save(newSchedule); // Lưu trước
						listSave.add(newSchedule);
					}
				}

				staffWorkScheduleRepository.flush(); // Bắt buộc để tất cả schedule có ID

				// Phase 2: Gán half-day leave sau khi đã flush
				for (StaffWorkSchedule staffWorkSchedule : listSave) {
					applyHalfDayLeave(leaveRequest, staffWorkSchedule, dateList);
				}

				staffWorkScheduleRepository.flush(); // Đảm bảo cập nhật trước khi tính công

				// Phase 3: Tính công
				for (StaffWorkSchedule schedule : listSave) {
					calculateStaffWorkTimeServiceV2.calculateStaffWorkTimeAndSave(schedule.getId());
				}
			}
		}

		return new ApiResponse<>(HttpStatus.SC_OK, "Lưu thành công", response);
	}

	private void applyHalfDayLeave(LeaveRequest leaveRequest, StaffWorkSchedule staffWorkSchedule,
			List<LocalDate> dateList) {
		LocalDate workingDate = staffWorkSchedule.getWorkingDate().toInstant().atZone(ZoneId.systemDefault())
				.toLocalDate();

		boolean isStart = Boolean.TRUE.equals(leaveRequest.getHalfDayLeaveStart())
				&& workingDate.equals(dateList.get(0));
		boolean isEnd = Boolean.TRUE.equals(leaveRequest.getHalfDayLeaveEnd())
				&& workingDate.equals(dateList.get(dateList.size() - 1));

		if (isStart || isEnd) {
			if (staffWorkSchedule.getId() == null) {
				throw new IllegalStateException(
						"StaffWorkSchedule chưa có ID. Vui lòng đảm bảo đã gọi flush trước đó.");
			}

			if (isStart) {
				staffWorkSchedule.setLeaveType(leaveTypeService.getHalfLeaveOfLeaveType(leaveRequest.getLeaveType()));
				StaffWorkScheduleShiftPeriod shiftPeriod = staffWorkScheduleShiftPeriodRepository
						.getStaffWorkScheduleShiftPeriodBy(staffWorkSchedule.getId(),
								leaveRequest.getTimePeriodStart().getId())
						.orElse(new StaffWorkScheduleShiftPeriod());

				shiftPeriod.setSchedule(staffWorkSchedule);
				shiftPeriod.setLeavePeriod(leaveRequest.getTimePeriodStart());
				staffWorkScheduleShiftPeriodRepository.save(shiftPeriod);
			}

			if (isEnd) {
				staffWorkSchedule.setLeaveType(leaveTypeService.getHalfLeaveOfLeaveType(leaveRequest.getLeaveType()));
				StaffWorkScheduleShiftPeriod shiftPeriod = staffWorkScheduleShiftPeriodRepository
						.getStaffWorkScheduleShiftPeriodBy(staffWorkSchedule.getId(),
								leaveRequest.getTimePeriodEnd().getId())
						.orElse(new StaffWorkScheduleShiftPeriod());

				shiftPeriod.setSchedule(staffWorkSchedule);
				shiftPeriod.setLeavePeriod(leaveRequest.getTimePeriodEnd());
				staffWorkScheduleShiftPeriodRepository.save(shiftPeriod);
			}
		}
	}

	@Override
	public TotalStaffWorkScheduleDto getStaffWorkScheduleSummary(SearchStaffWorkScheduleDto dto) {
//
//        String sql = """
//                    SELECT new com.globits.hr.dto.TotalStaffWorkScheduleDto(
//                        null,
//                        null,
//                        SUM(entity.estimatedWorkingHours),
//                        SUM(entity.lateArrivalCount),
//                        SUM(entity.earlyExitCount),
//                        SUM(entity.lateArrivalMinutes),
//                        SUM(entity.earlyExitMinutes),
//                        SUM(entity.earlyArrivalMinutes),
//                        SUM(entity.lateExitMinutes),
//                        SUM(entity.confirmedOTHoursBeforeShift),
//                        SUM(entity.confirmedOTHoursAfterShift),
//                        SUM(entity.totalHours),
//                        SUM(entity.totalValidHours),
//                        SUM(entity.paidLeaveHours),
//                        SUM(entity.unpaidLeaveHours),
//                        SUM(entity.convertedWorkingHours),
//                        SUM(entity.totalPaidWork),
//                        SUM(entity.unpaidLeaveWorkRatio),
//                        SUM(entity.paidLeaveWorkRatio))
//                    FROM StaffWorkSchedule entity
//                """;
//
//        String whereClause = " WHERE (1=1) ";
//        String orderBy = " ";
//        if (dto.getOrderBy() != null && StringUtils.hasLength(dto.getOrderBy()))
//            orderBy = " ORDER BY entity." + dto.getOrderBy() + " ASC ";
//        else {
//            orderBy = " order by entity.workingDate desc, entity.modifyDate desc ";
//        }
//        if (dto.getFromDate() != null) {
//            whereClause += " AND entity.workingDate >= :fromDate";
//        }
//        if (dto.getToDate() != null) {
//            whereClause += " AND entity.workingDate <= :toDate";
//        }
//
//        if (dto.getStaffId() != null) {
//            whereClause += " AND entity.staff.id = :staffId";
//        }
//
//        if (dto.getDepartmentId() != null) {
//            whereClause += " AND entity.staff.department.id = :departmentId";
//        }
//
//        if (dto.getOrganizationId() != null) {
//            whereClause += " AND entity.staff.department.organization.id = :organizationId";
//        }
//
//        if (dto.getPositionId() != null) {
//            if (dto.getStaffId() != null) {
//                whereClause += " AND EXISTS (SELECT p FROM Position p WHERE p.staff.id = entity.staff.id AND entity.staff.id = :staffId AND p.id = :positionId AND p.isMain = true)";
//            } else {
//                whereClause += " AND EXISTS (SELECT p FROM Position p WHERE p.staff.id = entity.staff.id AND p.id = :positionId AND p.isMain = true)";
//            }
//        }
//
//        if (dto.getShiftWorkId() != null) {
//            whereClause += " AND entity.shiftWork.id = :shiftWorkId";
//        }
//
//        if (dto.getLeaveTypeId() != null) {
//            whereClause += " AND entity.leaveType.id = :leaveTypeId";
//        }
//
//        sql += whereClause;
//
//        Query q = manager.createQuery(sql, TotalStaffWorkScheduleDto.class);
//
//        if (dto.getFromDate() != null) {
//            q.setParameter("fromDate", dto.getFromDate());
//        }
//        if (dto.getToDate() != null) {
//            q.setParameter("toDate", dto.getToDate());
//        }
//        if (dto.getPositionId() != null) {
//            q.setParameter("positionId", dto.getPositionId());
//        }
//        if (dto.getShiftWorkId() != null) {
//            q.setParameter("shiftWorkId", dto.getShiftWorkId());
//        }
//        if (dto.getLeaveTypeId() != null) {
//            q.setParameter("leaveTypeId", dto.getLeaveTypeId());
//        }
//        if (dto.getStaffId() != null) {
//            q.setParameter("staffId", dto.getStaffId());
//        }
//        if (dto.getDepartmentId() != null) {
//            q.setParameter("departmentId", dto.getDepartmentId());
//        }
//        if (dto.getOrganizationId() != null) {
//            q.setParameter("organizationId", dto.getOrganizationId());
//        }
//
//        try {
//            List<TotalStaffWorkScheduleDto> result = q.getResultList();
//            return result.isEmpty() ? null : result.get(0);
//        } catch (NoResultException e) {
//            return null;
//        }

		List<StaffWorkScheduleDto> entities = this.searchByPage(dto).getContent();
		return this.calculateTotalFromList(entities);

	}

	public TotalStaffWorkScheduleDto calculateTotalFromList(List<StaffWorkScheduleDto> list) {
		TotalStaffWorkScheduleDto total = new TotalStaffWorkScheduleDto();
		if (list != null && !list.isEmpty()) {
			for (StaffWorkScheduleDto item : list) {
				total.setEstimatedWorkingHours(add(total.getEstimatedWorkingHours(), item.getEstimatedWorkingHours()));
				if (item.getLateArrivalCount() != null) {
					total.setLateArrivalCount(
							add(total.getLateArrivalCount(), Long.valueOf(item.getLateArrivalCount())));
				} else {
					total.setLateArrivalCount(add(total.getLateArrivalCount(), 0L));
				}

				if (item.getEarlyExitCount() != null) {
					total.setEarlyExitCount(add(total.getEarlyExitCount(), Long.valueOf(item.getEarlyExitCount())));
				} else {
					total.setEarlyExitCount(add(total.getEarlyExitCount(), 0L));
				}
				if (item.getLateArrivalMinutes() != null) {
					total.setLateArrivalMinutes(
							add(total.getLateArrivalMinutes(), Long.valueOf(item.getLateArrivalMinutes())));
				} else {
					total.setLateArrivalMinutes(add(total.getLateArrivalMinutes(), 0L));
				}
				if (item.getEarlyExitMinutes() != null) {
					total.setEarlyExitMinutes(
							add(total.getEarlyExitMinutes(), Long.valueOf(item.getEarlyExitMinutes())));
				} else {
					total.setEarlyExitMinutes(add(total.getEarlyExitMinutes(), 0L));
				}
				if (item.getEarlyArrivalMinutes() != null) {
					total.setEarlyArrivalMinutes(
							add(total.getEarlyArrivalMinutes(), Long.valueOf(item.getEarlyArrivalMinutes())));
				} else {
					total.setEarlyArrivalMinutes(add(total.getEarlyArrivalMinutes(), 0L));
				}
				if (item.getLateExitMinutes() != null) {
					total.setLateExitMinutes(add(total.getLateExitMinutes(), Long.valueOf(item.getLateExitMinutes())));
				} else {
					total.setLateExitMinutes(add(total.getLateExitMinutes(), 0L));
				}
				total.setConfirmedOTHoursBeforeShift(
						add(total.getConfirmedOTHoursBeforeShift(), item.getConfirmedOTHoursBeforeShift()));
				total.setConfirmedOTHoursAfterShift(
						add(total.getConfirmedOTHoursAfterShift(), item.getConfirmedOTHoursAfterShift()));
				total.setTotalHours(add(total.getTotalHours(), item.getTotalHours()));
				total.setTotalValidHours(add(total.getTotalValidHours(), item.getTotalValidHours()));
				total.setPaidLeaveHours(add(total.getPaidLeaveHours(), item.getPaidLeaveHours()));
				total.setUnpaidLeaveHours(add(total.getUnpaidLeaveHours(), item.getUnpaidLeaveHours()));
				total.setConvertedWorkingHours(add(total.getConvertedWorkingHours(), item.getConvertedWorkingHours()));
				total.setTotalPaidWork(add(total.getTotalPaidWork(), item.getTotalPaidWork()));
				total.setUnpaidLeaveWorkRatio(add(total.getUnpaidLeaveWorkRatio(), item.getUnpaidLeaveWorkRatio()));
				total.setPaidLeaveWorkRatio(add(total.getPaidLeaveWorkRatio(), item.getPaidLeaveWorkRatio()));
			}
		}

		total.setTotalConfirmedOTHours(
				add(total.getConfirmedOTHoursAfterShift(), total.getConfirmedOTHoursBeforeShift()));
		return total;
	}

	private Double add(Double a, Double b) {
		if (a == null)
			a = 0.0;
		if (b == null)
			b = 0.0;
		return a + b;
	}

	private Long add(Long a, Long b) {
		if (a == null)
			a = 0L;
		if (b == null)
			b = 0L;
		return a + b;
	}

	public static List<LocalDate> getDatesBetween(Date fromDate, Date toDate) {
		LocalDate start = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate end = toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		return Stream.iterate(start, date -> date.plusDays(1)).limit(ChronoUnit.DAYS.between(start, end) + 1)
				.collect(Collectors.toList());
	}

	private StaffWorkSchedule createNewScheduleAndMarkLeaveType(Date workingDate, Staff staff, LeaveType leaveType) {
		StaffWorkSchedule schedule = new StaffWorkSchedule();
		schedule.setEstimatedWorkingHours(0D);
		schedule.setStaff(staff);
		schedule.setWorkingDate(workingDate);
		schedule.setLeaveType(leaveType);
		return schedule;
	}

	private Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	public static boolean isGreaterDate(Date date1, Date date2) {
		if (date1 == null || date2 == null)
			return false;

		LocalDate localDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		LocalDate localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		return localDate1.isAfter(localDate2);
	}

	@Override
	public List<StaffWorkScheduleDto> generateFixSchedulesInRangeTimeForStaff(UUID staffId, Date fromDate,
			Date toDate) {
		if (staffId == null || fromDate == null || toDate == null || toDate.before(fromDate))
			return null;

		Staff staff = staffRepository.findById(staffId).orElse(null);
		if (staff == null || staff.getStaffWorkShiftType() == null
				|| !staff.getStaffWorkShiftType().equals(HrConstants.StaffWorkShiftType.FIXED.getValue())
				|| staff.getStaffLeaveShiftType() == null
				|| !staff.getStaffLeaveShiftType().equals(HrConstants.StaffLeaveShiftType.FIXED.getValue())
				|| (staff.getFixLeaveWeekDay() == null && staff.getFixLeaveWeekDay2() == null))
			return null;

		ShiftWork shiftWork = staff.getFixShiftWork();
		if (shiftWork == null)
			return null;

		// Các ngày nghỉ lễ trong khoảng thời gian phân ca
		Set<Date> holidayDates = publicHolidayDateService.getHolidaysInRangeTime(fromDate, toDate);

		// Loại nghỉ lễ
		LeaveType leaveType = null;
		if (!holidayDates.isEmpty()) {

			List<LeaveType> availableLeaveTypes = leaveTypeRepository
					.findByCode(HrConstants.LeaveTypeCode.PUBLIC_HOLIDAY.getCode());
			if (availableLeaveTypes != null && !availableLeaveTypes.isEmpty()) {
				leaveType = availableLeaveTypes.get(0);
			}
		}

		// Ngày nhân viên bắt đầu vào làm
		Date recruitmentDate = staff.getRecruitmentDate();

		List<StaffWorkSchedule> onSaveSchedules = new ArrayList<>();

		for (Date onLoopDate = new Date(fromDate.getTime()); !onLoopDate.after(toDate); onLoopDate = addDays(onLoopDate,
				1)) {

			if (recruitmentDate == null || isGreaterDate(recruitmentDate, onLoopDate))
				continue;

			// Không tạo lịch cố định vào ngày nghỉ
			if (staff.getFixLeaveWeekDay() != null && staff.getFixLeaveWeekDay().equals(detectWeekDayValue(onLoopDate)))
				continue;
			if (staff.getFixLeaveWeekDay2() != null
					&& staff.getFixLeaveWeekDay2().equals(detectWeekDayValue(onLoopDate)))
				continue;

			List<StaffWorkSchedule> availableSchedules = staffWorkScheduleRepository.getByStaffAndWorkingDate(staffId,
					onLoopDate);

			// Tạo mới ngày làm việc
			if (availableSchedules == null || availableSchedules.isEmpty()) {
				StaffWorkSchedule newSchedule = new StaffWorkSchedule();

				newSchedule.setShiftWork(shiftWork);
				newSchedule.setStaff(staff);
				newSchedule.setWorkingDate(onLoopDate);
				newSchedule.setAllowOneEntryOnly(true);
				newSchedule.setTimekeepingCalculationType(
						HrConstants.TimekeepingCalculationType.FIRST_IN_LAST_OUT.getValue());

				if (DateTimeUtil.isContainsDate(holidayDates, onLoopDate)) {
					newSchedule.setLeaveType(leaveType);
				}

				staffMaternityHistoryService.handleSetDuringPregnancyStatus(newSchedule);

				onSaveSchedules.add(newSchedule);
			}
			// Nếu đã có ngày làm việc, kiểm tra liệu ngày đó có phải ngày nghỉ phép
			// (StaffWorkSchedule) trống hay không
			else {
				for (StaffWorkSchedule existedSchedule : availableSchedules) {
					// là 1 ngày nghỉ phép => gán ca làm việc
					if (existedSchedule.getLeaveType() != null && existedSchedule.getShiftWork() == null) {
						existedSchedule.setShiftWork(shiftWork);
					}

					staffMaternityHistoryService.handleSetDuringPregnancyStatus(existedSchedule);
					if (DateTimeUtil.isContainsDate(holidayDates, onLoopDate)) {
						existedSchedule.setLeaveType(leaveType);
					}

					onSaveSchedules.add(existedSchedule);

				}
			}
		}

		onSaveSchedules = staffWorkScheduleRepository.saveAllAndFlush(onSaveSchedules);

		List<UUID> savedScheduleIds = new ArrayList<>();

		for (StaffWorkSchedule savedSchedule : onSaveSchedules) {
			savedScheduleIds.add(savedSchedule.getId());
		}

		entityManager.flush();
		entityManager.clear();

		List<StaffWorkScheduleDto> response = new ArrayList<>();

		// Thống kê ngay lập tức chỉ số cho các ca làm việc
		for (UUID scheduleId : savedScheduleIds) {
			// Map các lần chấm công
			StaffWorkScheduleDto mappedTSDSchedule = calculateStaffWorkTimeServiceV2
					.mapOrphanedTimesheetDetailInDayToSchedule(scheduleId);

			StaffWorkScheduleDto responseItem = calculateStaffWorkTimeServiceV2
					.calculateStaffWorkTimeAndSave(scheduleId);

			response.add(responseItem);
		}

		entityManager.flush();
		entityManager.clear();

		return response;
	}

	public static int detectWeekDayValue(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 1 = Sunday, 2 = Monday, ..., 7 = Saturday

		switch (dayOfWeek) {
		case Calendar.MONDAY:
			return HrConstants.WeekDays.MON.getValue();
		case Calendar.TUESDAY:
			return HrConstants.WeekDays.TUE.getValue();
		case Calendar.WEDNESDAY:
			return HrConstants.WeekDays.WED.getValue();
		case Calendar.THURSDAY:
			return HrConstants.WeekDays.THU.getValue();
		case Calendar.FRIDAY:
			return HrConstants.WeekDays.FRI.getValue();
		case Calendar.SATURDAY:
			return HrConstants.WeekDays.SAT.getValue();
		case Calendar.SUNDAY:
			return HrConstants.WeekDays.SUN.getValue();
		default:
			return 0;
		}
	}

	@Override
	public List<UUID> lockSchedules(List<UUID> scheduleIds) {
		List<UUID> response = new ArrayList<>();

		if (scheduleIds == null || scheduleIds.isEmpty()) {
			return response;
		}

		List<StaffWorkSchedule> needSaveScheudles = new ArrayList<>();

		for (UUID scheduleId : scheduleIds) {
			StaffWorkSchedule schedule = staffWorkScheduleRepository.findById(scheduleId).orElse(null);

			if (schedule == null)
				continue;

			schedule.setIsLocked(true);

			needSaveScheudles.add(schedule);
		}

		List<StaffWorkSchedule> savedSchedules = staffWorkScheduleRepository.saveAllAndFlush(needSaveScheudles);

		if (savedSchedules.isEmpty())
			return response;

		for (StaffWorkSchedule savedSchedule : savedSchedules) {
			response.add(savedSchedule.getId());
		}

		return response;
	}

	@Override
	@Transactional
	public StaffWorkSchedule generateScheduleFromTimesheetDetailDto(TimeSheetDetailDto dto,
			TimeSheetDetail timeSheetDetail) {
		// 1. Validate đầu vào
		if (dto == null || dto.getEmployee() == null || dto.getEmployee().getId() == null
				|| dto.getStaffWorkSchedule() == null || dto.getStaffWorkSchedule().getShiftWork() == null
				|| dto.getWorkingDate() == null || timeSheetDetail == null) {
			return null;
		}

		StaffWorkSchedule schedule;
		// 2. Tìm StaffWorkSchedule đã tồn tại (ưu tiên ID nếu có)
		UUID scheduleId = dto.getStaffWorkSchedule().getId();
		if (scheduleId != null) {
			schedule = staffWorkScheduleRepository.findById(scheduleId).orElse(new StaffWorkSchedule());
		} else {
			schedule = staffWorkScheduleRepository
					.getByStaffIdAndShiftWorkIdAndWorkingDate(dto.getEmployee().getId(),
							dto.getStaffWorkSchedule().getShiftWork().getId(), dto.getWorkingDate())
					.stream().findFirst().orElse(new StaffWorkSchedule());
		}

		// 3. Load và validate ShiftWork
		ShiftWorkDto swDto = dto.getStaffWorkSchedule().getShiftWork();
		ShiftWork shiftWork = null;
		if (swDto.getId() != null) {
			shiftWork = shiftWorkRepository.findById(swDto.getId()).orElse(null);
		} else if (swDto.getCode() != null) {
			shiftWork = shiftWorkRepository.findByCode(swDto.getCode()).stream().findFirst().orElse(null);
		}
		if (shiftWork == null) {
			return null;
		}

		// 4. Load và validate Staff
		Staff staff;
		UUID staffId = dto.getEmployee().getId();
		staff = staffRepository.findById(staffId).orElse(null);
		if (staff == null) {
			staff = staffRepository.findByCode(dto.getEmployee().getStaffCode()).stream().findFirst().orElse(null);
		}
		if (staff == null) {
			return null;
		}

		// 5. Thiết lập các quan hệ và thuộc tính
		schedule.setShiftWork(shiftWork);
		schedule.setStaff(staff);
		schedule.setWorkingDate(dto.getWorkingDate());
		schedule.setAllowOneEntryOnly(true);
		schedule.setTimekeepingCalculationType(HrConstants.TimekeepingCalculationType.FIRST_IN_LAST_OUT.getValue());
		staffMaternityHistoryService.handleSetDuringPregnancyStatus(schedule);

		// 6. Thêm TimeSheetDetail vào StaffWorkSchedule
		if (schedule.getTimesheetDetails() == null) {
			schedule.setTimesheetDetails(new HashSet<>());
		}
		// Đồng bộ hai chiều
		timeSheetDetail.setStaffWorkSchedule(schedule);
		schedule.getTimesheetDetails().add(timeSheetDetail);

		// 7. Lưu và trả về
		return staffWorkScheduleRepository.saveAndFlush(schedule);
	}

	@Override
	public StaffWorkSchedule generateScheduleFromTimeSheetStaffDto(TimeSheetStaffDto dto) {
		if (dto == null || dto.getStaffWorkSchedule() == null || dto.getStaffWorkSchedule().getShiftWork() == null
				|| dto.getWorkingDate() == null)
			return null;

		StaffWorkSchedule entity = null;

		if (dto.getStaff() != null && dto.getStaff().getId() != null
				&& dto.getStaffWorkSchedule().getShiftWork() != null
				&& dto.getStaffWorkSchedule().getShiftWork().getId() != null) {
			List<StaffWorkSchedule> availableSchedules = staffWorkScheduleRepository
					.getByStaffIdAndShiftWorkIdAndWorkingDate(dto.getStaff().getId(),
							dto.getStaffWorkSchedule().getShiftWork().getId(), dto.getWorkingDate());
			if (availableSchedules != null && !availableSchedules.isEmpty()) {
				entity = availableSchedules.get(0);
			}
		}

		if (entity == null && dto.getStaff() != null && dto.getStaff().getId() != null
				&& dto.getStaffWorkSchedule().getShiftWork() != null
				&& dto.getStaffWorkSchedule().getShiftWork().getCode() != null) {
			List<StaffWorkSchedule> availableSchedules = staffWorkScheduleRepository
					.getByStaffIdAndShiftWorkCodeAndWorkingDate(dto.getStaff().getId(),
							dto.getStaffWorkSchedule().getShiftWork().getCode(), dto.getWorkingDate());
			if (availableSchedules != null && !availableSchedules.isEmpty()) {
				entity = availableSchedules.get(0);
			}
		}

		if (entity == null) {
			entity = new StaffWorkSchedule();
		}

		ShiftWork shiftWork = null;
		if (dto.getStaffWorkSchedule() != null && dto.getStaffWorkSchedule().getShiftWork() != null) {
			if (dto.getStaffWorkSchedule().getShiftWork().getId() != null) {
				shiftWork = shiftWorkRepository.findById(dto.getStaffWorkSchedule().getShiftWork().getId())
						.orElse(null);
			} else if (dto.getStaffWorkSchedule().getShiftWork().getCode() != null) {
				List<ShiftWork> availableShiftWorks = shiftWorkRepository
						.findByCode(dto.getStaffWorkSchedule().getShiftWork().getCode());
				if (availableShiftWorks != null && !availableShiftWorks.isEmpty()) {
					shiftWork = availableShiftWorks.get(0);
				}
			}
		}

		if (shiftWork == null)
			return null;

		entity.setShiftWork(shiftWork);

		Staff staff = null;
		if (dto.getStaffId() != null) {
			staff = staffRepository.findById(dto.getStaffId()).orElse(null);
		}
		if (staff == null && dto.getStaff() != null && dto.getStaff().getId() != null) {
			staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
		}
		if (staff == null && dto.getStaff() != null && dto.getStaff().getStaffCode() != null) {
			List<Staff> availableStaffs = staffRepository.findByCode(dto.getStaff().getStaffCode());
			staff = availableStaffs.get(0);
		}

		if (staff == null)
			return null;

		entity.setStaff(staff);
		entity.setWorkingDate(dto.getWorkingDate());

		Integer timekeepingCalculateType = HrConstants.TimekeepingCalculationType.FIRST_IN_LAST_OUT.getValue();

		staffMaternityHistoryService.handleSetDuringPregnancyStatus(entity);
		entity.setAllowOneEntryOnly(true);
		entity.setTimekeepingCalculationType(timekeepingCalculateType);
//        entity.setallow

		entity = staffWorkScheduleRepository.saveAndFlush(entity);

//        StaffWorkScheduleDto response = calculateStaffWorkTimeServiceV2.calculateStaffWorkTimeAndSave(entity.getId());

		return entity;
	}
}
