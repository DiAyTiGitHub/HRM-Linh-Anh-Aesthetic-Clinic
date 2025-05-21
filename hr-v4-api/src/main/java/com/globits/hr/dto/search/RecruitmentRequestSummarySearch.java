package com.globits.hr.dto.search;

import java.util.Date;
import java.util.UUID;

public class RecruitmentRequestSummarySearch extends SearchBaseDto {
    private String requestId;
    private String plainId;
    private Date fromEndDate;
    private Date toEndDate;
    public RecruitmentRequestSummarySearch() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getPlainId() {
        return plainId;
    }

    public void setPlainId(String plainId) {
        this.plainId = plainId;
    }

    public Date getFromEndDate() {
        return fromEndDate;
    }

    public void setFromEndDate(Date fromEndDate) {
        this.fromEndDate = fromEndDate;
    }

    public Date getToEndDate() {
        return toEndDate;
    }

    public void setToEndDate(Date toEndDate) {
        this.toEndDate = toEndDate;
    }
}
