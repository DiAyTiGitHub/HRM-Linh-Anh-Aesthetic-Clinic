package com.globits.timesheet.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.ShiftWorkTimePeriod;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffWorkSchedule;
import com.globits.hr.dto.*;
import com.globits.hr.dto.importExcel.StaffLAImport;
import com.globits.hr.dto.search.AbsenceRequestSearchDto;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.repository.StaffWorkScheduleRepository;
import com.globits.hr.service.StaffHierarchyService;
import com.globits.hr.service.StaffWorkScheduleService;
import com.globits.hr.service.UserExtService;
import com.globits.hr.utils.ExcelUtils;
import com.globits.hr.utils.RoleUtils;
import com.globits.salary.dto.SalaryPeriodDto;
import com.globits.salary.service.SalaryPeriodService;
import com.globits.security.dto.UserDto;
import com.globits.timesheet.domain.OvertimeRequest;
import com.globits.timesheet.dto.OvertimeRequestDto;
import com.globits.timesheet.dto.TimeSheetDetailDto;
import com.globits.timesheet.dto.TimeSheetStaffDto;
import com.globits.timesheet.dto.importExcel.OvertimeRequestImport;
import com.globits.timesheet.dto.search.SearchOvertimeRequestDto;
import com.globits.timesheet.repository.OvertimeRequestRepository;
import com.globits.timesheet.service.OvertimeRequestService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.sql.ast.tree.expression.Over;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class OvertimeRequestServiceImpl extends GenericServiceImpl<OvertimeRequest, UUID> implements OvertimeRequestService {

    @Autowired
    private OvertimeRequestRepository repository;

    @Autowired
    private StaffWorkScheduleRepository staffWorkScheduleRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserExtService userExtService;

    @Autowired
    private StaffWorkScheduleService staffWorkScheduleService;

    @Autowired
    private StaffHierarchyService staffHierarchyService;

    @Autowired
    private SalaryPeriodService salaryPeriodService;

    @Autowired
    private StaffRepository staffRepository;

    @Override
    public Page<OvertimeRequestDto> pagingOvertimeRequest(SearchOvertimeRequestDto dto) {
        if (dto == null) return null;
        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0) pageIndex--;
        else pageIndex = 0;

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

        String sqlCount = "select count(entity.id) from OvertimeRequest entity ";
        String sql = "select new com.globits.timesheet.dto.OvertimeRequestDto(entity) from OvertimeRequest as entity ";
        String joinPositionStaff = "";
        boolean hasJoinMainPosition = false;
        if (dto.getPositionTitleId() != null || dto.getDepartmentId() != null || dto.getOrganizationId() != null) {
            joinPositionStaff += " JOIN Position pos ON pos.isMain = true AND pos.staff.id = entity.staffWorkSchedule.staff.id ";
            hasJoinMainPosition = true;
        }
        String whereClause = " where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.staffWorkSchedule.staff.staffCode LIKE :text OR entity.staffWorkSchedule.staff.displayName LIKE :text ) ";
        }

        if (dto.getStaffId() != null) {
            whereClause += " and entity.staffWorkSchedule.staff.id = :staffId ";
        }
        if (dto.getStaffIdList() != null && dto.getStaffIdList().size() > 0) {
            whereClause += " and entity.staffWorkSchedule.staff.id IN (:staffIdList) ";
        }

        if (dto.getApprovalStatus() != null) {
            whereClause += " and entity.approvalStatus = :approvalStatus ";
        }
        if (dto.getFromDate() != null) {
            whereClause += " and date(entity.staffWorkSchedule.workingDate) >= date(:fromDate) ";
        }
        if (dto.getToDate() != null) {
            whereClause += " and date(entity.staffWorkSchedule.workingDate) <= date(:toDate) ";
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

        String orderBy = " ORDER BY entity.createDate DESC";

        sql += joinPositionStaff + whereClause + orderBy;
        sqlCount += joinPositionStaff + whereClause;
        Query q = manager.createQuery(sql, OvertimeRequestDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", "%" + dto.getKeyword() + "%");
            qCount.setParameter("text", "%" + dto.getKeyword() + "%");
        }

        if (dto.getStaffId() != null) {
            q.setParameter("staffId", dto.getStaffId());
            qCount.setParameter("staffId", dto.getStaffId());
        }
        if (dto.getStaffIdList() != null && dto.getStaffIdList().size() > 0) {
            q.setParameter("staffIdList", dto.getStaffIdList());
            qCount.setParameter("staffIdList", dto.getStaffIdList());
        }
        if (dto.getApprovalStatus() != null) {
            q.setParameter("approvalStatus", dto.getApprovalStatus());
            qCount.setParameter("approvalStatus", dto.getApprovalStatus());
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


        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<OvertimeRequestDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public OvertimeRequestDto saveOrUpdate(OvertimeRequestDto dto) {
        if (dto == null) return null;
        OvertimeRequest entity = null;

        if (dto.getId() != null) {
            entity = repository.findById(dto.getId()).orElse(null);
        }
        if (dto.getStaffWorkSchedule() != null && dto.getStaffWorkSchedule().getId() != null) {
            List<OvertimeRequest> availableSchedules = repository.findByScheduleId(dto.getStaffWorkSchedule().getId());
            if (availableSchedules != null && !availableSchedules.isEmpty()) {
                entity = availableSchedules.get(0);
            }
        }
        if (entity == null) {
            entity = new OvertimeRequest();
        }

        entity.setRequestOTHoursBeforeShift(dto.getRequestOTHoursBeforeShift());
        entity.setRequestOTHoursAfterShift(dto.getRequestOTHoursAfterShift());
//        entity.setApprovalStatus(dto.getApprovalStatus());

        StaffWorkSchedule staffWorkSchedule = null;
        if (dto.getStaffWorkSchedule() != null && dto.getStaffWorkSchedule().getId() != null) {
            staffWorkSchedule = staffWorkScheduleRepository.findById(dto.getStaffWorkSchedule().getId()).orElse(null);
//            boolean isApproved = dto.getApprovalStatus() != null && dto.getApprovalStatus().equals(HrConstants.AbsenceRequestApprovalStatus.APPROVED.getValue());
//            updateStaffWorkScheduleRequestOTHours(staffWorkSchedule, entity, isApproved);
        }

        entity.setStaffWorkSchedule(staffWorkSchedule);
        OvertimeRequest response = repository.save(entity);
        return new OvertimeRequestDto(response);
    }


    @Override
    public OvertimeRequestDto getById(UUID id) {
        if (id != null) {
            OvertimeRequest entity = repository.findById(id).orElse(null);
            if (entity != null) {
                return new OvertimeRequestDto(entity);
            }
        }
        return null;
    }

    @Override
    public Boolean deleteById(UUID id) {
        if (id == null) {
            return false;
        }

        OvertimeRequest entity = repository.findById(id).orElse(null);
        if (entity == null) {
            return false;
        }

        StaffWorkSchedule staffWorkSchedule = entity.getStaffWorkSchedule();
        if (staffWorkSchedule != null) {
            if (staffWorkSchedule.getConfirmedOTHoursBeforeShift() != null && entity.getRequestOTHoursBeforeShift() != null) {
                staffWorkSchedule.setConfirmedOTHoursBeforeShift(
                        Math.max(0, staffWorkSchedule.getConfirmedOTHoursBeforeShift() - entity.getRequestOTHoursBeforeShift())
                );
            }

            if (staffWorkSchedule.getConfirmedOTHoursAfterShift() != null && entity.getRequestOTHoursAfterShift() != null) {
                staffWorkSchedule.setConfirmedOTHoursAfterShift(
                        Math.max(0, staffWorkSchedule.getConfirmedOTHoursAfterShift() - entity.getRequestOTHoursAfterShift())
                );
            }
            staffWorkScheduleRepository.save(staffWorkSchedule);
        }

        repository.deleteById(id);
        return true;
    }


    @Override
//    @Transactional
    @Modifying
    public Integer updateRequestsApprovalStatus(List<OvertimeRequestDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return null;
        }

        List<OvertimeRequest> onSaveRequests = new ArrayList<>();
        List<OvertimeRequestDto> updatedScheduleOTHoursByOTRequest = new ArrayList<>();

        for (OvertimeRequestDto item : dtos) {
            if (item == null || item.getId() == null || item.getApprovalStatus() == null) continue;

            OvertimeRequest entity = repository.findById(item.getId()).orElse(null);
            if (entity == null) continue;

            entity.setApprovalStatus(item.getApprovalStatus());
            entity.setRequestOTHoursAfterShift(item.getRequestOTHoursAfterShift());
            entity.setRequestOTHoursBeforeShift(item.getRequestOTHoursBeforeShift());


            item.setId(entity.getId());
            if (entity.getStaffWorkSchedule() != null && item.getStaffWorkSchedule() != null) {
                item.getStaffWorkSchedule().setId(entity.getStaffWorkSchedule().getId());
                item.getStaffWorkSchedule().setConfirmedOTHoursAfterShift(item.getStaffWorkSchedule().getConfirmedOTHoursAfterShift());
                item.getStaffWorkSchedule().setConfirmedOTHoursBeforeShift(item.getStaffWorkSchedule().getConfirmedOTHoursBeforeShift());
                updatedScheduleOTHoursByOTRequest.add(item);
            }
            onSaveRequests.add(entity);
        }

        if (onSaveRequests.isEmpty()) {
            return null;
        }

        List<OvertimeRequest> savedRequests = repository.saveAllAndFlush(onSaveRequests);

        if (dtos.get(0).getApprovalStatus().equals(HrConstants.OvertimeRequestApprovalStatus.APPROVED.getValue())) {
            this.updatedScheduleOTHoursByOTRequestV2(updatedScheduleOTHoursByOTRequest);
        }

        return savedRequests.size();
    }


    @Override
    @Transactional
    public Boolean deleteMultiple(List<UUID> ids) {
        if (ids == null)
            return false;
        for (UUID id : ids) {
            this.deleteById(id);
        }
        return true;
    }

    @Override
    public SearchOvertimeRequestDto getInitialFilter() {
        SearchOvertimeRequestDto response = new SearchOvertimeRequestDto();

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


    private List<StaffWorkScheduleDto> updatedScheduleOTHoursByOTRequestV2(List<OvertimeRequestDto> approvedOTRequests) {
        List<StaffWorkScheduleDto> response = new ArrayList<>();

        for (OvertimeRequestDto overtimeRequest : approvedOTRequests) {
            if (overtimeRequest.getStaffWorkSchedule() != null && overtimeRequest.getStaffWorkSchedule().getId() != null) {
                StaffWorkSchedule staffWorkSchedule = staffWorkScheduleRepository.findById(overtimeRequest.getStaffWorkSchedule().getId()).orElse(null);
                StaffWorkScheduleDto scheduleDto = new StaffWorkScheduleDto(staffWorkSchedule);
                if (overtimeRequest.getStaffWorkSchedule() != null) {
                    scheduleDto.setConfirmedOTHoursAfterShift(overtimeRequest.getStaffWorkSchedule().getConfirmedOTHoursAfterShift());
                    scheduleDto.setConfirmedOTHoursBeforeShift(overtimeRequest.getStaffWorkSchedule().getConfirmedOTHoursBeforeShift());

                    StaffWorkScheduleDto updatedSchedule = staffWorkScheduleService.updateScheduleOTHours(scheduleDto);

                    response.add(updatedSchedule);
                }
            }
        }

        return response;
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

    @Override
    public ByteArrayOutputStream importExcelOvertimeRequest(MultipartFile file) throws IOException {
        // Đọc workbook gốc từ file đầu vào
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());

        ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());

        // Lấy danh sách dữ liệu từ file Excel
        List<OvertimeRequestImport> listData = convertExcelRowToImportDto(bis);

        // Lưu và lấy lỗi
        for (OvertimeRequestImport requestImport : listData) {
            if (!requestImport.getErrorMessages().isEmpty()) continue;

            OvertimeRequestDto savedRequest = saveOverTimeRequestImport(requestImport);
        }

        // Tạo sheet mới để ghi kết quả xử lý
        XSSFSheet resultSheet = this.createUniqueSheet(workbook, "Kết quả xử lý");

        // Ghi header
        Row header = resultSheet.createRow(0);

        header.createCell(0).setCellValue("Dòng");
        header.createCell(1).setCellValue("Mã NV");
        header.createCell(2).setCellValue("Nhân viên");
        header.createCell(3).setCellValue("Kết quả");

        for (int i = 0; i < listData.size(); i++) {
            OvertimeRequestImport requestImport = listData.get(i);

            Row row = resultSheet.createRow(i + 1);
            if(requestImport.getImportOrder() != null){
                row.createCell(0).setCellValue(requestImport.getImportOrder().toString());
            }
            if(requestImport.getStaffCode() != null){
                row.createCell(1).setCellValue(requestImport.getStaffCode());
            }
            if(requestImport.getImportOrder() != null){
                row.createCell(2).setCellValue(requestImport.getStaffName());
            }
            if (requestImport.getErrorMessages().isEmpty()) {
                row.createCell(3).setCellValue("Dữ liệu hợp lệ");
            } else {
                String errorMessage = String.join(". ", requestImport.getErrorMessages());
                row.createCell(3).setCellValue(errorMessage);
            }
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        return bos;
    }

    private OvertimeRequestDto saveOverTimeRequestImport(OvertimeRequestImport importData) {
        if (importData == null) return null;

        if (importData.getErrorMessages() == null) {
            importData.setErrorMessages(new ArrayList<>());
        }

        StaffWorkSchedule staffWorkSchedule = null;
        List<StaffWorkSchedule> availableSchedules = staffWorkScheduleRepository.getByStaffCodeShiftWorkCodeAndWorkingDate(importData.getStaffCode(), importData.getShiftWorkCode(), importData.getWorkingDate());
        if (availableSchedules == null || availableSchedules.isEmpty()) {
            importData.getErrorMessages().add("Ca làm việc " + importData.getShiftWorkCode() + " không được phân cho nhân viên " + importData.getStaffCode() + " vào ngày " + ExcelUtils.formatDate(importData.getWorkingDate()));
            return null;
        }

        staffWorkSchedule = availableSchedules.get(0);

        StaffWorkScheduleDto scheduleDto = new StaffWorkScheduleDto(staffWorkSchedule);

        OvertimeRequestDto importItem = new OvertimeRequestDto();
        importItem.setApprovalStatus(importData.getApprovalStatus());
        importItem.setRequestOTHoursAfterShift(importData.getRequestOTHoursAfterShift());
        importItem.setRequestOTHoursBeforeShift(importData.getRequestOTHoursBeforeShift());
        importItem.setStaffWorkSchedule(scheduleDto);

        OvertimeRequestDto savedRequest = this.saveOrUpdate(importItem);
        importItem.setId(savedRequest.getId());

        entityManager.flush();

        if (importItem.getApprovalStatus() != null && importItem.getApprovalStatus().equals(HrConstants.OvertimeRequestApprovalStatus.APPROVED.getValue())) {
            importItem.getStaffWorkSchedule().setConfirmedOTHoursBeforeShift(importData.getConfirmedOTHoursBeforeShift());
            importItem.getStaffWorkSchedule().setConfirmedOTHoursAfterShift(importData.getConfirmedOTHoursAfterShift());
        }

        this.updateRequestsApprovalStatus(List.of(importItem));


        if (importData.getOtEndorserCode() != null) {
            Staff otEndoser = null;
            List<Staff> availableStaffs = staffRepository.findByCode(importData.getOtEndorserCode());
            if (availableStaffs == null || availableStaffs.isEmpty()) {
                importData.getErrorMessages().add("Người phê duyệt không tồn tại");
                return null;
            }
            otEndoser = availableStaffs.get(0);

            staffWorkSchedule.setOtEndorser(otEndoser);

            staffWorkSchedule = staffWorkScheduleRepository.saveAndFlush(staffWorkSchedule);
        }

        entityManager.flush();
        entityManager.clear();

        return savedRequest;
    }


    private List<OvertimeRequestImport> convertExcelRowToImportDto(InputStream is) throws IOException {
        List<OvertimeRequestImport> convertedDtos = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");

        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            Cell currentCell = null;

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                OvertimeRequestImport importItem = new OvertimeRequestImport();


                Row currentRow = sheet.getRow(rowIndex);
                List<String> errorMessages = new ArrayList<>();


                if (currentRow == null)
                    continue;

//                0. STT
                currentCell = currentRow.getCell(0);
                String orderStr = ExcelUtils.parseStringCellValue(currentCell);
                importItem.setImportOrder(ExcelUtils.convertToInteger(orderStr));

//                1. Mã NV yêu cầu
                currentCell = currentRow.getCell(1);
                String staffCode = ExcelUtils.parseStringCellValue(currentCell);
                if (staffCode == null) staffCode = "";
                importItem.setStaffCode(staffCode.trim());
                if (!StringUtils.hasText(staffCode)) {
                    errorMessages.add("Không xác định được MÃ NV yêu cầu");
                }

//                2. Tên NV yêu cầu
                currentCell = currentRow.getCell(2);
                String staffName = ExcelUtils.parseStringCellValue(currentCell);
                if (staffName == null) staffName = "";
                importItem.setStaffName(staffName);

//                3. Ngày làm việc (dd-mm-yyyy)
                currentCell = currentRow.getCell(3);
                Date workingDate = ExcelUtils.parseDateCellValue(currentCell, rowIndex, 3, dateFormat);
                importItem.setWorkingDate(workingDate);
                if (importItem.getWorkingDate() == null) {
                    errorMessages.add("Không xác định được ngày làm việc");
                }


//                4. Mã ca làm việc
                currentCell = currentRow.getCell(4);
                String shiftWorkCode = ExcelUtils.parseStringCellValue(currentCell);
                if (shiftWorkCode == null) shiftWorkCode = "";
                importItem.setShiftWorkCode(shiftWorkCode.trim());
                if (!StringUtils.hasText(shiftWorkCode)) {
                    errorMessages.add("Không xác định được ca làm việc");
                }

//                5. Tên ca làm việc
                currentCell = currentRow.getCell(5);
                String shiftWorkName = ExcelUtils.parseStringCellValue(currentCell);
                if (shiftWorkName == null) shiftWorkName = "";
                importItem.setShiftWorkName(shiftWorkName);

                if (!StringUtils.hasText(orderStr) && !StringUtils.hasText(shiftWorkCode) && !StringUtils.hasText(staffCode)) {
                    continue;
                }

//                6. Số giờ YC tính trước ca
                currentCell = currentRow.getCell(6);
                Double requestOTHoursBeforeShift = ExcelUtils.parseDoubleCellValue(currentCell);
                if (requestOTHoursBeforeShift == null) {
                    requestOTHoursBeforeShift = 0D;
                }
                importItem.setRequestOTHoursBeforeShift(requestOTHoursBeforeShift);

//                7. Số giờ YC tính sau ca
                currentCell = currentRow.getCell(7);
                Double requestOTHoursAfterShift = ExcelUtils.parseDoubleCellValue(currentCell);
                if (requestOTHoursAfterShift == null) {
                    requestOTHoursAfterShift = 0D;
                }
                importItem.setRequestOTHoursAfterShift(requestOTHoursAfterShift);

                if (requestOTHoursAfterShift == 0D && requestOTHoursBeforeShift == 0D) {
                    errorMessages.add("Thời gian yêu cầu xác nhận trước và sau ca không được đồng thời bằng 0");
                }

//                8. Trạng thái phê duyệt YC (Chưa duyệt, Đã duyệt, Không duyệt)
                currentCell = currentRow.getCell(8);
                Integer approvalStatus = this.convertStringCellToApprovalStatus(currentCell);
                importItem.setApprovalStatus(approvalStatus);

//                9. Mã NV phê duyệt
                currentCell = currentRow.getCell(9);
                String otEndorserCode = ExcelUtils.parseStringCellValue(currentCell);
                if (otEndorserCode == null) otEndorserCode = "";
                importItem.setOtEndorserCode(otEndorserCode.trim());

//                10. Tên NV phê duyệt
                currentCell = currentRow.getCell(10);
                String otEndorserName = ExcelUtils.parseStringCellValue(currentCell);
                if (otEndorserName == null) otEndorserName = "";
                importItem.setOtEndorserName(otEndorserName);

                if (!StringUtils.hasText(otEndorserCode) && StringUtils.hasText(otEndorserName)) {
                    errorMessages.add("Chưa xác nhận được nhân viên phê duyệt");
                }

//                11. Số giờ trước ca được duyệt
                currentCell = currentRow.getCell(11);
                Double confirmedOTHoursBeforeShift = ExcelUtils.parseDoubleCellValue(currentCell);
                if (confirmedOTHoursBeforeShift == null) {
                    confirmedOTHoursBeforeShift = 0D;
                }
                importItem.setConfirmedOTHoursBeforeShift(confirmedOTHoursBeforeShift);

//                12. Số giờ sau ca được duyệt
                currentCell = currentRow.getCell(12);
                Double confirmedOTHoursAfterShift = ExcelUtils.parseDoubleCellValue(currentCell);
                if (confirmedOTHoursAfterShift == null) {
                    confirmedOTHoursAfterShift = 0D;
                }
                importItem.setConfirmedOTHoursAfterShift(confirmedOTHoursAfterShift);

                importItem.setErrorMessages(errorMessages);

                convertedDtos.add(importItem);
            }
        }

        return convertedDtos;
    }

    private Integer convertStringCellToApprovalStatus(Cell cell) {
        String approvalStatusStr = ExcelUtils.parseStringCellValue(cell);
        if (!StringUtils.hasText(approvalStatusStr)) return null;

        switch (approvalStatusStr.trim()) {
            case "Chua duyet":
            case "chua duyet":
            case "CHUA DUYET":
            case "Chưa duyệt":
            case "chưa duyệt":
            case "CHƯA DUYỆT":
                return HrConstants.OvertimeRequestApprovalStatus.NOT_APPROVED_YET.getValue();

            case "Da duyet":
            case "da duyet":
            case "DA DUYET":
            case "Đã duyệt":
            case "đã duyệt":
            case "ĐÃ DUYỆT":
                return HrConstants.OvertimeRequestApprovalStatus.APPROVED.getValue();

            case "Khong duyet":
            case "khong duyet":
            case "KHONG DUYET":
            case "Không duyệt":
            case "không duyệt":
            case "KHÔNG DUYỆT":
                return HrConstants.OvertimeRequestApprovalStatus.NOT_APPROVED.getValue();

            default:
                return null;
        }
    }
}