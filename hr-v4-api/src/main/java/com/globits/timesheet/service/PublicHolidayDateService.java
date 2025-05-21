package com.globits.timesheet.service;

import com.globits.core.service.GenericService;
import com.globits.hr.dto.search.SearchDto;
import com.globits.timesheet.domain.PublicHolidayDate;
import com.globits.timesheet.dto.PublicHolidayDateDto;
import com.globits.timesheet.dto.search.SearchPublicHolidayDateDto;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PublicHolidayDateService extends GenericService<PublicHolidayDate, UUID> {
	PublicHolidayDateDto getPublicHolidayDateById(UUID id);

    Boolean deleteById(UUID id);

    PublicHolidayDateDto saveOrUpdate(PublicHolidayDateDto dto);

    Page<PublicHolidayDateDto> pagingPublicHolidayDate(SearchPublicHolidayDateDto dto);

    Boolean createPublicHolidayDateAutomatic(SearchDto dto);

	Boolean deleteMultiple(List<UUID> ids);

    Set<Date> getHolidaysInRangeTime(Date fromDate, Date toDate);

}
