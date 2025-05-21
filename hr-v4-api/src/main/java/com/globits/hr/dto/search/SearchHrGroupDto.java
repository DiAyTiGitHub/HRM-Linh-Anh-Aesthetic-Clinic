package com.globits.hr.dto.search;

import java.util.UUID;

public class SearchHrGroupDto extends SearchDto{
    protected UUID hrGroupId;
    public UUID getHrGroupId() {return hrGroupId;}
    public void setHrGroupId(UUID hrGroupId) {this.hrGroupId = hrGroupId;}
}
