package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "tbl_hr_group")
public class HrGroup extends BaseObject {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    private String groupType;

    @Lob
    private String description;


    @OneToMany(mappedBy = "hrgroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HrGroupStaff> hrGroupStaffs;

    public HrGroup() {}

    public HrGroup(String name, String code, String groupType, String description, Set<HrGroupStaff> hrGroupStaffs) {
        this.name = name;
        this.code = code;
        this.groupType = groupType;
        this.description = description;
        this.hrGroupStaffs = hrGroupStaffs;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }

    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getGroupType() { return groupType; }

    public void setGroupType(String groupType) { this.groupType = groupType; }

    public Set<HrGroupStaff> getHrGroupStaffs() { return hrGroupStaffs; }

    public void setHrGroupStaffs(Set<HrGroupStaff> hrGroupStaffs) { this.hrGroupStaffs = hrGroupStaffs; }
}

