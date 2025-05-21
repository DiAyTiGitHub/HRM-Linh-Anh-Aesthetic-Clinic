package com.globits.hr.domain;

import jakarta.persistence.*;

import com.globits.core.domain.BaseObject;

import java.util.Set;

/**
 * @author dunghq
 * Danh mục chức danh
 * Ví dụ : Hiệu trưởng, hiệu phó,....
 */

@Table(name = "tbl_position_title")
@Entity
public class PositionTitle extends BaseObject {
    private static final long serialVersionUID = 1L;

    @Column(name = "name")
    private String name;// ten chuc danh

    @Column(name = "code")
    private String code;// ma chuc danh

    @Column(name = "short_name")
    private String shortName;// ten viet tat

    @Column(name = "other_name")
    private String otherName;// ten viet tat

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rank_title_id")
    private RankTitle rankTitle;// cap bac

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private PositionTitle parent;// chuc danh cha

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_role_id")
    private PositionRole positionRole;// nhom quyen mac dinh

    @Column(name = "position_coefficient")
    private Double positionCoefficient;// hệ số phụ cấp chức vụ

    @Column(name = "type") // 1= Chính quyền; 2= Đoàn thể
    private Integer type;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description; // Mô tả công việc chức danh

    @Column(name = "recruitment_days")
    private Integer recruitmentDays; // Số ngày tuyển dụng

    @OneToMany(mappedBy = "positionTitle", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HRDepartmentPosition> departmentPositions;// cac chuc danh trong phong ban

    @Column(name = "workday_calculation_type")
    private Integer workDayCalculationType; // Cách tính ngày công chuẩn trong tháng. Chi tiết: HrConstants.PositionTitleWorkdayCalculationType

    // Có giá trị khi workDayCalculationType = PositionTitleWorkdayCalculationType.FIXED
    @Column(name = "estimated_working_days")
    private Double estimatedWorkingDays = 28.0; // Số ngày làm việc được ước tính.


    public PositionTitle() {

    }

    public PositionTitle(PositionTitle title) {
        this.code = title.getCode();
        this.name = title.getName();
        this.type = title.getType();
        this.description = title.getDescription();
    }

    public Double getEstimatedWorkingDays() {
        return estimatedWorkingDays;
    }

    public void setEstimatedWorkingDays(Double estimatedWorkingDays) {
        this.estimatedWorkingDays = estimatedWorkingDays;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPositionCoefficient() {
        return positionCoefficient;
    }

    public void setPositionCoefficient(Double positionCoefficient) {
        this.positionCoefficient = positionCoefficient;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public RankTitle getRankTitle() {
        return rankTitle;
    }

    public void setRankTitle(RankTitle rankTitle) {
        this.rankTitle = rankTitle;
    }

    public PositionTitle getParent() {
        return parent;
    }

    public void setParent(PositionTitle parent) {
        this.parent = parent;
    }

    public PositionRole getPositionRole() {
        return positionRole;
    }

    public void setPositionRole(PositionRole positionRole) {
        this.positionRole = positionRole;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public Integer getRecruitmentDays() {
        return recruitmentDays;
    }

    public void setRecruitmentDays(Integer recruitmentDays) {
        this.recruitmentDays = recruitmentDays;
    }

    public Set<HRDepartmentPosition> getDepartmentPositions() {
        return departmentPositions;
    }

    public void setDepartmentPositions(Set<HRDepartmentPosition> departmentPositions) {
        this.departmentPositions = departmentPositions;
    }

    public Integer getWorkDayCalculationType() {
        return workDayCalculationType;
    }

    public void setWorkDayCalculationType(Integer workDayCalculationType) {
        this.workDayCalculationType = workDayCalculationType;
    }
}
