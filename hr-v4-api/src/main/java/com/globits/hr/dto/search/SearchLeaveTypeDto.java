package com.globits.hr.dto.search;

public class SearchLeaveTypeDto extends SearchDto {
	private Boolean isPaid;
	private Boolean usedForRequest;

	public Boolean getIsPaid() {
		return isPaid;
	}

	public void setIsPaid(Boolean isPaid) {
		this.isPaid = isPaid;
	}

	public Boolean getUsedForRequest() {
		return usedForRequest;
	}

	public void setUsedForRequest(Boolean usedForRequest) {
		this.usedForRequest = usedForRequest;
	}

}
