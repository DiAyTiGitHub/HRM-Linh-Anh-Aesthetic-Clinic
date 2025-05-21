package com.globits.hr.dto.search;

import java.util.List;
import java.util.UUID;

public class SearchHrDepartmentDto extends SearchDto{
    private List<String> departmentTypeCode;
    private UUID parentId;
    private UUID hrDepartmentTypeId;
    private String hrDepartmentTypeCode;

    public SearchHrDepartmentDto() {
    }

    public UUID getHrDepartmentTypeId() {
        return hrDepartmentTypeId;
    }

    public void setHrDepartmentTypeId(UUID hrDepartmentTypeId) {
        this.hrDepartmentTypeId = hrDepartmentTypeId;
    }

    public String getHrDepartmentTypeCode() {
        return hrDepartmentTypeCode;
    }

    public void setHrDepartmentTypeCode(String hrDepartmentTypeCode) {
        this.hrDepartmentTypeCode = hrDepartmentTypeCode;
    }

    public List<String> getDepartmentTypeCode() {
        return departmentTypeCode;
    }

    public void setDepartmentTypeCode(List<String> departmentTypeCode) {
        this.departmentTypeCode = departmentTypeCode;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }
}
