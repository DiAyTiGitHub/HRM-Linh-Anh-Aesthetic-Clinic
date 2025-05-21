package com.globits.hr.dto.search;

public class ProjectActivitySearchDto extends SearchDto {
    private Boolean includeAll = false;

    public Boolean getIncludeAll() {
        return includeAll;
    }

    public void setIncludeAll(Boolean includeAll) {
        this.includeAll = includeAll;
    }

}
