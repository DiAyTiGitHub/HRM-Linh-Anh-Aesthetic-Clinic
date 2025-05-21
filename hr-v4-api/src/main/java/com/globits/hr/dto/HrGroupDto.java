package com.globits.hr.dto;

import com.globits.hr.domain.HrGroup;
import com.globits.hr.domain.HrGroupStaff;
import com.globits.hr.domain.Staff;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class HrGroupDto {

    private UUID id;
    private String name;
    private String code;
    private String groupType;
    private String description;
    private Set<StaffDto> staffs;

    // Constructors
    public HrGroupDto() {
    }

    public HrGroupDto(UUID id, String name, String code, String groupType, String description, Set<StaffDto> staffs) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.groupType = groupType;
        this.description = description;
        this.staffs = staffs;
    }

    // Constructor from HrGroup entity
    public HrGroupDto(HrGroup hrGroup) {
        this.id = hrGroup.getId();
        this.name = hrGroup.getName();
        this.code = hrGroup.getCode();
        this.groupType = hrGroup.getGroupType();
        this.description = hrGroup.getDescription();
        this.staffs = hrGroup.getHrGroupStaffs().stream()
                .map(hrGroupStaff -> new StaffDto(hrGroupStaff.getStaff()))
                .collect(Collectors.toSet());
    }

    // Getters and Setters
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

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<StaffDto> getStaffs() {
        return staffs;
    }

    public void setStaffs(Set<StaffDto> staffs) {
        this.staffs = staffs;
    }
}
