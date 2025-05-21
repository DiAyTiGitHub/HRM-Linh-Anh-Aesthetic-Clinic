package com.globits.timesheet.dto.search;

import com.globits.hr.dto.search.SearchDto;

import java.util.Date;
import java.util.UUID;

public class SearchShiftWorkTimePeriodDto extends SearchDto {
    private UUID shiftWorkId;

	public UUID getShiftWorkId() {
		return shiftWorkId;
	}

	public void setShiftWorkId(UUID shiftWorkId) {
		this.shiftWorkId = shiftWorkId;
	}
}
