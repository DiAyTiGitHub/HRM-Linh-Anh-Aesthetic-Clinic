package com.globits.hr.domain;

import com.globits.core.domain.Department;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("HRDepartment")
public class HRDepartment extends Department {
    private static final long serialVersionUID = 1L;

    @Column(name = "description")
    private String description;

    @Column(name = "dep_function")
    private String function;

    @Column(name = "industry_block")
    private String industryBlock;

    @Column(name = "founded_date")
    private Date foundedDate;

    @Column(name = "founded_number")
    private String foundedNumber;

    @Column(name = "department_display_code")
    private String departmentDisplayCode;// Số hiệu phòng ban

    @Column(name = "establish_decision_code")
    private String establishDecisionCode;// Số quyết định thành lập

    @Column(name = "establish_decision_date")
    private Date establishDecisionDate;// Ngày quyết định thành lập

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hr_department_type_id")
    private DepartmentType hrdepartmentType;// loai phong ban

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hr_department_group_id")
    private DepartmentGroup departmentGroup;// nhom phong ban

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_title_manager_id")
    private PositionTitle positionTitleManager;// chuc danh quan ly

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HRDepartmentPosition> departmentPositions;// cac chuc danh trong phong ban

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Position> positions;// cac chuc danh trong phong ban

    private String timezone; // mui gio
    
    @Column(name = "sort_number")
    private Integer sortNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id")
    private HrOrganization organization;// Đơn vị trực thuộc
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_manager_id")
    private Position positionManager;// vi tri quan ly

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HrDepartmentShiftWork> departmentShiftWorks; // các ca làm việc của phòng ban

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_place_id")
    private Workplace workplace;
    @Column(name = "work_place_name")
    private String workplaceName;

    public Set<HrDepartmentShiftWork> getDepartmentShiftWorks() {
        return departmentShiftWorks;
    }

    public void setDepartmentShiftWorks(Set<HrDepartmentShiftWork> departmentShiftWorks) {
        this.departmentShiftWorks = departmentShiftWorks;
    }

    public Set<Position> getPositions() {
        return positions;
    }

    public void setPositions(Set<Position> positions) {
        this.positions = positions;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getIndustryBlock() {
        return industryBlock;
    }

    public void setIndustryBlock(String industryBlock) {
        this.industryBlock = industryBlock;
    }

    public Date getFoundedDate() {
        return foundedDate;
    }

    public void setFoundedDate(Date foundedDate) {
        this.foundedDate = foundedDate;
    }

    public String getFoundedNumber() {
        return foundedNumber;
    }

    public void setFoundedNumber(String foundedNumber) {
        this.foundedNumber = foundedNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDepartmentDisplayCode() {
        return departmentDisplayCode;
    }

    public void setDepartmentDisplayCode(String departmentDisplayCode) {
        this.departmentDisplayCode = departmentDisplayCode;
    }

    public String getEstablishDecisionCode() {
        return establishDecisionCode;
    }

    public void setEstablishDecisionCode(String establishDecisionCode) {
        this.establishDecisionCode = establishDecisionCode;
    }

    public Date getEstablishDecisionDate() {
        return establishDecisionDate;
    }

    public void setEstablishDecisionDate(Date establishDecisionDate) {
        this.establishDecisionDate = establishDecisionDate;
    }

    public DepartmentType getHrdepartmentType() {
        return hrdepartmentType;
    }

    public void setHrdepartmentType(DepartmentType hrdepartmentType) {
        this.hrdepartmentType = hrdepartmentType;
    }

    public DepartmentGroup getDepartmentGroup() {
        return departmentGroup;
    }

    public void setDepartmentGroup(DepartmentGroup departmentGroup) {
        this.departmentGroup = departmentGroup;
    }

    public PositionTitle getPositionTitleManager() {
        return positionTitleManager;
    }

    public void setPositionTitleManager(PositionTitle positionTitleManager) {
        this.positionTitleManager = positionTitleManager;
    }

    public Set<HRDepartmentPosition> getDepartmentPositions() {
        return departmentPositions;
    }

    public void setDepartmentPositions(Set<HRDepartmentPosition> departmentPositions) {
        this.departmentPositions = departmentPositions;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

	public Integer getSortNumber() {
		return sortNumber;
	}

	public void setSortNumber(Integer sortNumber) {
		this.sortNumber = sortNumber;
	}

    public HrOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(HrOrganization organization) {
        this.organization = organization;
    }

	public Position getPositionManager() {
		return positionManager;
	}

	public void setPositionManager(Position positionManager) {
		this.positionManager = positionManager;
	}

    public Workplace getWorkplace() {
        return workplace;
    }

    public void setWorkplace(Workplace workplace) {
        this.workplace = workplace;
    }

    public String getWorkplaceName() {
        return workplaceName;
    }

    public void setWorkplaceName(String workplaceName) {
        this.workplaceName = workplaceName;
    }
}
