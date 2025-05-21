package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.StaffWorkingHistory;
import com.globits.hr.dto.*;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchPositionDto;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PositionService extends GenericService<Position, UUID> {
    public Page<PositionDto> pagingPosition(SearchPositionDto dto);

    PositionDto getById(UUID id);

    PositionDto savePosition(PositionDto dto);

    Integer saveListPosition(List<PositionDto> dtos);

    Boolean deletePosition(UUID id);

    Boolean deleteMultiplePositions(List<UUID> ids);

    PositionDto findByCode(String code);

    Boolean isValidCode(PositionDto dto);

    PositionDto setupDataPosition(PositionDto dto);

    List<Position> createTemporaryPositionsFromRecruitmentRequest(UUID recruitmentRequestId);

    PositionDto removeStaffFromPosition(UUID positionId);

    List<PositionDto> assignPositionsForStaff(SearchPositionDto dto);

    Long countNumberOfPositionInDepartmentWithPositionTitle(SearchPositionDto searchDto);

    Set<HrResourcePlanItemDto> getResourcePlanItem(UUID departmentId);

    HashMap<UUID, PositionMainDto> getPositionMainMap();

    List<PositionDto> transferPositions(TransferPositionsDto dto);

    List<PositionDto> transferStaff(TransferStaffDto dto);

    Workbook exportExcelPosition(SearchPositionDto dto);

    public int saveListImportExcel(List<ImportPositionRelationShipDto> dtos);

    PositionDto getByStaffId(UUID staffId);

    StaffWorkingHistory findHistoryAndUpdateFromPosition(Position position);

    String autoGenerateCode(String configKey);
}
