package com.globits.hr.dto;

import java.util.UUID;

import com.globits.hr.dto.search.SearchDto;

public class SearchAllowanceDto extends SearchDto {
	//private AllowanceTypeDto allowanceType;
	private UUID allowanceTypeId;
	
    public SearchAllowanceDto() {
    	
    }
	
	public UUID getAllowanceTypeId() {
		return allowanceTypeId;
	}

	public void setAllowanceTypeId(UUID allowanceTypeId) {
		this.allowanceTypeId = allowanceTypeId;
	}
	
}
