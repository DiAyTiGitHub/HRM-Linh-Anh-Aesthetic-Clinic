package com.globits.salary.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.domain.SalaryItem;
import com.globits.salary.domain.StaffSalaryItemValue;
import com.globits.salary.dto.*;
import com.globits.salary.service.StaffSalaryItemValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/staff-salary-item-value")
public class RestStaffSalaryItemValueController {
    @Autowired
    private StaffSalaryItemValueService staffSalaryItemValueService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, value = "/update")
    public ResponseEntity<StaffSalaryItemValueDto> saveOrUpdateStaffSalaryItemValue(@RequestBody StaffSalaryItemValueDto dto) {
        // Lưu thẳng giá trị mới
//        StaffSalaryItemValueDto result = staffSalaryItemValueService.saveOrUpdateStaffSalaryItemValue(dto);
        // Tạo lịch sử thay đổi
        StaffSalaryItemValueDto result = staffSalaryItemValueService.preHandleHistoryAndSaveOrUpdate(dto);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    public ResponseEntity<StaffSalaryItemValueDto> save(@RequestBody StaffSalaryItemValueDto dto) {
        // Lưu thẳng giá trị mới
//        StaffSalaryItemValueDto result = staffSalaryItemValueService.saveOrUpdateStaffSalaryItemValue(dto);
        // Tạo lịch sử thay đổi
        StaffSalaryItemValueDto result = staffSalaryItemValueService.save(dto);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<StaffSalaryItemValueDto> getStaffSalaryItemValueById(@PathVariable UUID id) {
        StaffSalaryItemValueDto result = staffSalaryItemValueService.getById(id);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/salary-value-histories/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<StaffSalaryItemValueDto>> getSalaryValueHistories(@PathVariable UUID id) {
        List<StaffSalaryItemValueDto> result = staffSalaryItemValueService.getSalaryValueHistories(id);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/staff/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<MapStaffSalaryItemValueDto>> getByStaffId(@PathVariable UUID id) {
        List<MapStaffSalaryItemValueDto> result = staffSalaryItemValueService.getByStaffId(id);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteStaffSalaryItemValue(@PathVariable UUID id) {
        Boolean result = staffSalaryItemValueService.deleteStaffSalaryItemValue(id);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/paging", method = RequestMethod.POST)
    public ResponseEntity<Page<StaffSalaryItemValueDto>> pagingSalaryType(@RequestBody SearchDto searchDto) {
        Page<StaffSalaryItemValueDto> page = staffSalaryItemValueService.pagingStaffSalaryItemValue(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/get-by-staff-template-item")
    public ResponseEntity<StaffSalaryItemValueListDto> getStaffSalaryTemplateItemValues(@RequestBody RequestSalaryValueDto dto) {
        StaffSalaryItemValueListDto response = staffSalaryItemValueService.getBySalaryTemplateItem(dto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/tax-bhxh/{staffId}", method = RequestMethod.GET)
    public ResponseEntity<StaffSalaryItemValueDto> getTaxBHXHByStaffId(@PathVariable UUID staffId) {
        StaffSalaryItemValueDto result = staffSalaryItemValueService.getTaxBHXHByStaffId(staffId);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, value = "/save-list-staff-salary-item-value")
    public ResponseEntity<Integer> saveStaffSalaryItemValueList(@RequestBody StaffSalaryItemValueListDto dto) {
        Integer result = staffSalaryItemValueService.saveStaffSalaryItemValueList(dto);
        if (result == null || result == 0) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
