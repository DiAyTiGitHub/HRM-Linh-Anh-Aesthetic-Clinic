package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.*;
import com.globits.hr.dto.function.ImportStaffDto;
import com.globits.hr.dto.function.PositionTitleStaffDto;
import com.globits.hr.dto.importExcel.StaffLAImportResult;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.timesheet.dto.search.SearchStaffWorkScheduleDto;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface StaffService extends GenericService<Staff, UUID> {
    SearchStaffDto getInitialFilter();

    Page<StaffDto> searchByPage(SearchStaffDto dto);

    Page<Staff> searchByPageEntity(SearchStaffDto dto);


    StaffDto dismissAllPositionsOfStaff(StaffDto staff);

    Page<StaffDto> findByPageBasicInfo(int pageIndex, int pageSize);

    StaffDto createStaffAndAccountByCode(StaffDto staff, UUID id);

    StaffDto getStaff(UUID staffId);

    Staff getEntityById(UUID staffId);

    Page<PositionStaffDto> findTeacherByDepartment(UUID departmentId, int pageIndex, int pageSize);

    StaffDto createStaffFromDto(StaffDto staffDto);

    Page<StaffDto> findPageByCode(String textSearch, int pageIndex, int pageSize);

    StaffDto deleteStaff(UUID id);

    List<StaffDto> getAll();

    Boolean deleteMultiple(Staff[] staffs);

    Page<StaffDto> searchStaff(StaffSearchDto dto, int pageSize, int pageIndex);

    int saveListStaff(List<StaffDto> dtos);

    Boolean validateStaffCode(StaffDto staff);

    Boolean validateTaxCode(StaffDto staff);

    Boolean validateSocialInsuranceNumber(StaffDto staff);

    Boolean validateHealthInsuranceNumber(StaffDto staff);


    Boolean validateUserName(String userName, UUID userId);


    List<StaffDto> getListStaff(SearchStaffDto dto);

    List<UUID> getAllDepartmentIdByParentId(UUID parentId);

    StaffDto savePositionStaff(PositionTitleStaffDto dto);

    Staff getByCode(String code);

    StaffDto createStaffSimple(StaffDto staffDto);

    List<StaffDto> saveImportStaff(List<StaffDto> list);

    Boolean checkIdNumber(StaffDto dto);

    StaffDto updateStaffImage(UUID id, String imagePath);

    StaffDto saveStaffWithoutAccount(StaffDto staffDto);

    List<StaffDto> findStaffsHaveBirthDayByMonth(int month);

    List<StaffDto> findBySalaryTemplatePeriod(SearchStaffDto searchDto);

    StaffDto getTotalHasSocialIns(SearchStaffDto dto);

    void exportHICInfoToWord(HttpServletResponse response, UUID staffId) throws IOException;

    void handleSetValueForCurrentInsuranceSalary(UUID staffId);

    Workbook handleExcel(SearchStaffDto dto);

    UserExtRoleDto getCurrentRoleUser();

    List<StaffDto> createUsersForStaff(List<StaffDto> staffs, boolean allowCreate);

    Integer updateAllowExternalIpTimekeeping(List<StaffDto> staffs, boolean status);

    Workbook exportExcelListStaff(SearchStaffDto dto);

    ImportStaffDto importExcelListStaff(InputStream inputStream);

    List<StaffDto> saveListStaffFromExcel(List<StaffDto> list);


    List<Staff> getListStaffByIds(List<UUID> ids);

    HashMap<UUID, PermanentAddressDto> getPermanentAddressMap();

    HashMap<UUID, Integer> getNumberOfDependentsMap();

    ApiResponse<StaffLabourAgreementDto> getLastLabourAgreement(UUID staff);

    Boolean validateCccd(StaffDto staff);

    Boolean validateCmnd(StaffDto staff);

    StaffDto calculateRemainingAnnualLeave(SearchStaffDto dto);
}
