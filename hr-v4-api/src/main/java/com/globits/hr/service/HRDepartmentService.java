package com.globits.hr.service;

import java.util.List;
import java.util.UUID;

import com.globits.hr.domain.HRDepartment;
import com.globits.hr.dto.diagram.ResponseDiagram;
import com.globits.hr.dto.search.SearchHrDepartmentDto;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.search.SearchDto;

public interface HRDepartmentService {
    HRDepartmentDto saveOrUpdate(HRDepartmentDto dto);

    Integer saveListDepartment(List<HRDepartmentDto> dtos);

    Boolean isValidCode(HRDepartmentDto dto);

    Boolean deleteHRDepartment(UUID id);

    HRDepartmentDto getHRDepartment(UUID id);

    Page<HRDepartmentDto> pagingTreeDepartments(SearchHrDepartmentDto dto);

    Boolean checkCode(UUID id, String code);

    Page<HRDepartmentDto> pagingDepartments(SearchHrDepartmentDto dto);

    HRDepartment toHRDepartment(HRDepartmentDto dto, HRDepartment hRDepartment);

    List<ResponseDiagram> getHRDepartmentDiagram(UUID id);

    Boolean deleteMultiple(List<UUID> ids);

	Page<HRDepartmentDto> pagingDepartmentHierarchy(SearchHrDepartmentDto dto);

    List<HRDepartmentDto> pagingDepartmentHierarchySpreadByLevel(SearchHrDepartmentDto dto);

    Workbook exportExcelDepartment(SearchHrDepartmentDto dto);

    Page<HRDepartmentDto> pagingDepartmentPositionTreeView(SearchDto dto);

    Boolean checkValidParent(SearchDto searchDto);

    String autoGenerateCode(String configValue);

	List<HRDepartment> findAllDescendantsOfDepartment(UUID departmentId, String departmentCode);
}
