package com.globits.hr.dto.search;

import java.util.Date;

public class SearchBaseDto {
    protected int pageIndex = 1;
    protected int pageSize = 10;
    protected String keyword;
    protected Date fromDate;
    protected Date toDate;

    public SearchBaseDto() {
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getKeyword() {
        if(keyword != null && !keyword.trim().isEmpty()){
            return keyword.trim();
        }
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
}
