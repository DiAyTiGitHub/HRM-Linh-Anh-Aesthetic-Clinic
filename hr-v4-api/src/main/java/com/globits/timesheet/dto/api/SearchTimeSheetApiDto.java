package com.globits.timesheet.dto.api;

import java.util.Date;
import java.util.UUID;

public class SearchTimeSheetApiDto {

    private String fromdate;
    private String todate;
    private String url;
    private Boolean isOneTimeLock;//danh dau cham cong 1 lan/ngay


    public String getFromdate() {
        return fromdate;
    }

    public void setFromdate(String fromdate) {
        this.fromdate = fromdate;
    }

    public String getTodate() {
        return todate;
    }

    public void setTodate(String todate) {
        this.todate = todate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

	public Boolean getIsOneTimeLock() {
		return isOneTimeLock;
	}

	public void setIsOneTimeLock(Boolean isOneTimeLock) {
		this.isOneTimeLock = isOneTimeLock;
	}


}
