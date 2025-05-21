package com.globits.hr.domain;

import jakarta.persistence.*;


import com.globits.core.domain.BaseObject;
import com.globits.core.domain.Department;

import java.util.Set;

/*
 * Bảng này là bảng vị trí công tác của đơn vị
 * Trong vị trí công tác sẽ có PositionTitle (chức danh)
 * Và có hệ số phụ cấp
 * 1 nhân sự có thể đảm nhận nhiều vị trí công tác khác nhau
 */

@Table(name = "tbl_position")
@Entity
public class Position extends BaseObject {
    private static final long serialVersionUID = 1L;
    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private int status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "title_id")
    private PositionTitle title; // Chức danh

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private HRDepartment department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_staff_id")
    private Staff previousStaff;

    /*
     * Là position chính của Staff này
     */
    @Column(name = "is_main")
    private Boolean isMain;

    @Column(name = "is_concurrent")
    private Boolean isConcurrent;

    @Column(name = "is_temporary")
    private Boolean isTemporary; // Là tạm thời = tuyển lọc

    @OneToMany(mappedBy = "position", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PositionRelationShip> relationships; // position hiện tại là vị trí cấp dươi

    @OneToMany(mappedBy = "supervisor", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PositionRelationShip> isSupervisedRelationships; // position hiện tại la vi tri cap tren

    @OneToMany(mappedBy = "fromPosition", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StaffWorkingHistory> fromPositionWorkingHistories;

    @OneToMany(mappedBy = "toPosition", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StaffWorkingHistory> toPositionWorkingHistories;


    public Staff getPreviousStaff() {
        return previousStaff;
    }

    public void setPreviousStaff(Staff previousStaff) {
        this.previousStaff = previousStaff;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public PositionTitle getTitle() {
        return title;
    }

    public void setTitle(PositionTitle title) {
        this.title = title;
    }

    public HRDepartment getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartment department) {
        this.department = department;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Boolean getIsMain() {
        return isMain;
    }

    public void setIsMain(Boolean isMain) {
        this.isMain = isMain;
    }

    public Set<PositionRelationShip> getRelationships() {
        return relationships;
    }

    public void setRelationships(Set<PositionRelationShip> relationships) {
        this.relationships = relationships;
    }

    public Boolean getIsTemporary() {
        return isTemporary;
    }

    public void setIsTemporary(Boolean isTemporary) {
        this.isTemporary = isTemporary;
    }

    public Set<PositionRelationShip> getIsSupervisedRelationships() {
        return isSupervisedRelationships;
    }

    public void setIsSupervisedRelationships(Set<PositionRelationShip> isSupervisedRelationships) {
        this.isSupervisedRelationships = isSupervisedRelationships;
    }

    public Set<StaffWorkingHistory> getFromPositionWorkingHistories() {
        return fromPositionWorkingHistories;
    }

    public void setFromPositionWorkingHistories(Set<StaffWorkingHistory> fromPositionWorkingHistories) {
        this.fromPositionWorkingHistories = fromPositionWorkingHistories;
    }

    public Set<StaffWorkingHistory> getToPositionWorkingHistories() {
        return toPositionWorkingHistories;
    }

    public void setToPositionWorkingHistories(Set<StaffWorkingHistory> toPositionWorkingHistories) {
        this.toPositionWorkingHistories = toPositionWorkingHistories;
    }

    public Boolean getIsConcurrent() {
        return isConcurrent;
    }

    public void setIsConcurrent(Boolean isConcurrent) {
        this.isConcurrent = isConcurrent;
    }


}
