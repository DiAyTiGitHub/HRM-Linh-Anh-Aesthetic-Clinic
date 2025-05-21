package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.Allowance;
import com.globits.hr.domain.HrResourcePlan;
import com.globits.hr.dto.AllowanceDto;
import com.globits.hr.dto.DepartmentResourcePlanDto;
import com.globits.hr.dto.HrResourcePlanDto;
import com.globits.hr.dto.SearchAllowanceDto;
import com.globits.hr.dto.SearchHrResourcePlanDto;
import com.globits.salary.dto.search.SearchSalaryResultStaffDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface HrResourcePlanService extends GenericService<HrResourcePlan, UUID> {
    HrResourcePlanDto saveOrUpdate(HrResourcePlanDto dto);

    Boolean deleteById(UUID id);

    Boolean deleteMultiple(List<UUID> ids);

    HrResourcePlanDto getById(UUID id);

    Page<HrResourcePlanDto> searchByPage(SearchHrResourcePlanDto dto);

    Boolean isValidCode(HrResourcePlanDto dto);


    // Phó tổng giám đốc cập nhật trạng thái phe duyệt cho định biên
    Boolean updateStatusByViceGeneralDirector(SearchHrResourcePlanDto dto) throws Exception;

    // Tổng giám đốc cập nhật trạng thái phe duyệt cho định biên
    Boolean updateStatusByGeneralDirector(SearchHrResourcePlanDto dto) throws Exception;

    Boolean updateStatus(SearchHrResourcePlanDto dto) throws Exception;

    String autoGenerateCode(String configKey);

    List<DepartmentResourcePlanDto> getDepartmentResourcePlan(SearchHrResourcePlanDto dto);

    List<DepartmentResourcePlanDto> getDepartmentResourcePlanTree(SearchHrResourcePlanDto dto);

    List<DepartmentResourcePlanDto> getDepartmentResourcePlanTreeBySpreadLevel(SearchHrResourcePlanDto dto);


}
