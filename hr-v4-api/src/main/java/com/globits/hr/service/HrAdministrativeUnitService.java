package com.globits.hr.service;

import com.globits.core.domain.AdministrativeUnit;
import com.globits.core.dto.AdministrativeUnitDto;
import com.globits.core.service.GenericService;
import com.globits.hr.dto.HrAdministrativeUnitDto;
import com.globits.hr.dto.search.SearchAdministrativeUnitDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface HrAdministrativeUnitService extends GenericService<AdministrativeUnit, UUID> {
    Page<HrAdministrativeUnitDto> searchByPage(SearchAdministrativeUnitDto dto);

    AdministrativeUnitDto saveAdministrativeUnit(AdministrativeUnitDto dto);

    Integer saveListImportExcel(List<HrAdministrativeUnitDto> dtos);

    void remove(UUID id);

    Boolean checkCode(UUID id, String code);

    AdministrativeUnitDto updateAdministrativeUnit(AdministrativeUnitDto administrativeUnit, UUID administrativeUnitId);

    int deleteAdministrativeUnits(List<AdministrativeUnitDto> dtos);

    AdministrativeUnitDto deleteAdministrativeUnit(UUID id);

    List<AdministrativeUnitDto> getAllByLevel(Integer level);

    List<AdministrativeUnitDto> getAllChildByParentId(UUID parentId);

    List<UUID> getAllAdministrativeIdByParentId(UUID parentId, Boolean isCommunes);
//    Page<AdministrativeUnitDto> searchWardByDto(SearchDto dto);

	List<AdministrativeUnit> getAdministrativeUnitByCodeAndLevel(String code, Integer level);
}
