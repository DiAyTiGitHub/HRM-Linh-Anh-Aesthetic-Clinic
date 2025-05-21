package com.globits.hr.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.globits.core.domain.Department;
import com.globits.core.dto.BaseObjectDto;

public class DepartmentsTreeDto extends BaseObjectDto {
    private UUID id;
    private String name;
    private String code;
    private DepartmentsTreeDto parent;
    private List<DepartmentsTreeDto> subDepartment;
    private String displayOrder;
    private UUID parentId;
    private List<DepartmentsTreeDto> children = new ArrayList<DepartmentsTreeDto>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DepartmentsTreeDto getParent() {
        return parent;
    }

    public void setParent(DepartmentsTreeDto parent) {
        this.parent = parent;
    }

    public List<DepartmentsTreeDto> getSubDepartment() {
        return subDepartment;
    }

    public void setSubDepartment(List<DepartmentsTreeDto> subDepartment) {
        this.subDepartment = subDepartment;
    }

    public String getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(String displayOrder) {
        this.displayOrder = displayOrder;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public List<DepartmentsTreeDto> getChildren() {
        return children;
    }

    public void setChildren(List<DepartmentsTreeDto> children) {
        this.children = children;
    }

    public DepartmentsTreeDto() {
        super();
    }

    public DepartmentsTreeDto(Department entity, boolean isGetParent) {
        super();
        if (entity != null) {
            this.id = entity.getId();
            this.name = entity.getName();
            this.code = entity.getCode();
            this.displayOrder = entity.getDisplayOrder();
            if (entity.getParent() != null) {
                if (isGetParent) {
                    this.parent = new DepartmentsTreeDto();
                    this.parent.setId(entity.getParent().getId());
                    this.parent.setName(entity.getParent().getName());
                    this.parent.setCode(entity.getParent().getCode());
                    this.parent.setDisplayOrder(entity.getDisplayOrder());
                }
                this.setParentId(this.parent.getId());
            }
            if (entity.getSubDepartments() != null && entity.getSubDepartments().size() > 0) {
                this.children = new ArrayList<DepartmentsTreeDto>();
                for (Department child : entity.getSubDepartments()) {
                    this.children.add(new DepartmentsTreeDto(child, true));
                }
            }

        }
    }

    public DepartmentsTreeDto(Department entity) {
        this(entity, true);
    }
}
