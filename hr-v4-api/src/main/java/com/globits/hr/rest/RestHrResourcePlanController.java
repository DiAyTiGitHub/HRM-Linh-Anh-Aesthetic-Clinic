package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.*;
import com.globits.hr.dto.search.SearchRecruitmentDto;
import com.globits.hr.service.AllowanceService;
import com.globits.hr.service.HrResourcePlanService;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.salary.dto.search.SearchSalaryResultStaffDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/hr-resource-plan")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestHrResourcePlanController {
    @Autowired
    private HrResourcePlanService hrResourcePlanService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/save-or-update")
    public ResponseEntity<HrResourcePlanDto> saveOrUpdate(@RequestBody HrResourcePlanDto dto) {

        Boolean isValidCode = hrResourcePlanService.isValidCode(dto);
        if (isValidCode == null || isValidCode.equals(false)) {
            return new ResponseEntity<>(dto, HttpStatus.CONFLICT);
        }

        HrResourcePlanDto response = hrResourcePlanService.saveOrUpdate(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/search-by-page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<HrResourcePlanDto>> searchByPage(@RequestBody SearchHrResourcePlanDto searchDto) {
        Page<HrResourcePlanDto> page = hrResourcePlanService.searchByPage(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/remove/{id}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("id") UUID id) {
        Boolean res = hrResourcePlanService.deleteById(id);
        if (res == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/get-by-id/{id}", method = RequestMethod.GET)
    public ResponseEntity<HrResourcePlanDto> getById(@PathVariable("id") UUID id) {
        HrResourcePlanDto result = hrResourcePlanService.getById(id);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/delete-multiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> ids) {
        Boolean deleted = hrResourcePlanService.deleteMultiple(ids);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }



    // Phó tổng giám đốc cập nhật trạng thái
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/update-status-by-vice-general-director", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateStatusByViceGeneralDirector(@RequestBody SearchHrResourcePlanDto dto) {
        try {
            Boolean isUpdated = hrResourcePlanService.updateStatusByViceGeneralDirector(dto);
            if (isUpdated != null && isUpdated.equals(true)) {
                return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    // Tổng giám đốc cập nhật trạng thái
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/update-status-by-general-director", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateStatusByGeneralDirector(@RequestBody SearchHrResourcePlanDto dto) {
        try {
            Boolean isUpdated = hrResourcePlanService.updateStatusByGeneralDirector(dto);
            if (isUpdated != null && isUpdated.equals(true)) {
                return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    //Cập nhật trạng thái
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/update-status", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateStatus(@RequestBody SearchHrResourcePlanDto dto) {
        try {
            Boolean isUpdated = hrResourcePlanService.updateStatus(dto);
            if (isUpdated != null && isUpdated.equals(true)) {
                return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/auto-gen-code/{configKey}")
    public ResponseEntity<?> autoGenCode(@PathVariable("configKey") String configKey) {
        return new ResponseEntity<>(hrResourcePlanService.autoGenerateCode(configKey), HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/get-department-resource-plan", method = RequestMethod.POST)
    public ResponseEntity<List<DepartmentResourcePlanDto>> getDepartmentResourcePlan(@RequestBody SearchHrResourcePlanDto searchDto) {
        List<DepartmentResourcePlanDto> page = hrResourcePlanService.getDepartmentResourcePlan(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/get-department-resource-plan-tree", method = RequestMethod.POST)
    public ResponseEntity<List<DepartmentResourcePlanDto>> getDepartmentResourcePlanTree(@RequestBody SearchHrResourcePlanDto searchDto) {
        List<DepartmentResourcePlanDto> page = hrResourcePlanService.getDepartmentResourcePlanTree(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/get-department-resource-plan-tree-by-spread-level", method = RequestMethod.POST)
    public ResponseEntity<List<DepartmentResourcePlanDto>> getDepartmentResourcePlanTreeBySpreadLevel(@RequestBody SearchHrResourcePlanDto searchDto) {
        List<DepartmentResourcePlanDto> page = hrResourcePlanService.getDepartmentResourcePlanTreeBySpreadLevel(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }
}
