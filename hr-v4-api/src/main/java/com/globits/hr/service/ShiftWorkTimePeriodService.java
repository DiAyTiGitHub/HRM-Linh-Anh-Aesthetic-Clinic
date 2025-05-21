package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.ShiftWorkTimePeriod;
import com.globits.hr.dto.ShiftWorkTimePeriodDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.timesheet.dto.search.SearchShiftWorkTimePeriodDto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ShiftWorkTimePeriodService extends GenericService<ShiftWorkTimePeriod, UUID> {
    Page<ShiftWorkTimePeriodDto> searchByPage(SearchShiftWorkTimePeriodDto dto);

    ShiftWorkTimePeriodDto saveOne(ShiftWorkTimePeriodDto dto, UUID id);

    void remove(UUID id);

    ShiftWorkTimePeriodDto getById(UUID id);

    List<ShiftWorkTimePeriodDto> getAll();

    List<ShiftWorkTimePeriodDto> getShiftWorkTimePeriodByShiftWorkId(UUID id);
}
