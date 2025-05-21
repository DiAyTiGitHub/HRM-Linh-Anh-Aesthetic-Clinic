package com.globits.timesheet.dto.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeSheetResponseDto {
	 @JsonProperty("Table1")
	private List<TimeSheetRecordDto> Table1;

    public List<TimeSheetRecordDto> getTable1() {
        return Table1;
    }

    public void setTable1(List<TimeSheetRecordDto> Table1) {
        this.Table1 = Table1;
    }
	
}
