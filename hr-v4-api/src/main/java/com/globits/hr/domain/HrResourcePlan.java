package com.globits.hr.domain;

import com.globits.budget.domain.BaseNameCodeObject;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;

// Định biên nhân sự
@Table(name = "tbl_hr_resource_plan")
@Entity
public class HrResourcePlan extends BaseNameCodeObject {
    @Column(name = "plan_date")
    private Date planDate; // Ngày lập yêu cầu

    @ManyToOne
    @JoinColumn(name = "department_id")
    private HRDepartment department; // Phòng ban yêu cầu

    @OneToMany(mappedBy = "resourcePlan", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HrResourcePlanItem> resourcePlanItems; // Các chức danh định biên trong phòng ban

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_plan_id")
    private HrResourcePlan parentPlan; // Thuộc kế hoạch định biên Tổng hợp nào

    @OneToMany(mappedBy = "parentPlan", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HrResourcePlan> childrenPlans; // Các định biên con được tổng hợp để tạo thành định biên này (đối với định biên toàn Group)

    @ManyToOne
    @JoinColumn(name = "vice_general_director")
    private Staff viceGeneralDirector; // phó tổng giám đốc duyệt

    @Column(name = "vice_general_director_status")
    private Integer viceGeneralDirectorStatus; // Trạng thái phó tổng giám đốc duyệt. Chi tiết: HrConstants.HrResourcePlanApprovalStatus

    @ManyToOne
    @JoinColumn(name = "general_director")
    private Staff generalDirector; // tổng giám đốc duyệt

    @Column(name = "general_director_status")
    private Integer generalDirectorStatus; // Trạng thái tổng giám đốc duyệt. Chi tiết: HrConstants.HrResourcePlanApprovalStatus

    @ManyToOne
    @JoinColumn(name = "requester")
    private Staff requester; // người yêu cầu định biên

    private Integer status;  // trạng thái của yêu cầu

    @Column(name = "eliminate_planNumber")
    private Integer eliminatePlanNumber;


    public Staff getRequester() {
        return requester;
    }

    public void setRequester(Staff requester) {
        this.requester = requester;
    }

    public Date getPlanDate() {
        return planDate;
    }

    public void setPlanDate(Date planDate) {
        this.planDate = planDate;
    }

    public HRDepartment getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartment department) {
        this.department = department;
    }

    public Set<HrResourcePlanItem> getResourcePlanItems() {
        return resourcePlanItems;
    }

    public void setResourcePlanItems(Set<HrResourcePlanItem> resourcePlanItems) {
        this.resourcePlanItems = resourcePlanItems;
    }

    public HrResourcePlan getParentPlan() {
        return parentPlan;
    }

    public void setParentPlan(HrResourcePlan parentPlan) {
        this.parentPlan = parentPlan;
    }

    public Set<HrResourcePlan> getChildrenPlans() {
        return childrenPlans;
    }

    public void setChildrenPlans(Set<HrResourcePlan> childrenPlans) {
        this.childrenPlans = childrenPlans;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Staff getViceGeneralDirector() {
        return viceGeneralDirector;
    }

    public void setViceGeneralDirector(Staff viceGeneralDirector) {
        this.viceGeneralDirector = viceGeneralDirector;
    }

    public Integer getViceGeneralDirectorStatus() {
        return viceGeneralDirectorStatus;
    }

    public void setViceGeneralDirectorStatus(Integer viceGeneralDirectorStatus) {
        this.viceGeneralDirectorStatus = viceGeneralDirectorStatus;
    }

    public Staff getGeneralDirector() {
        return generalDirector;
    }

    public void setGeneralDirector(Staff generalDirector) {
        this.generalDirector = generalDirector;
    }

    public Integer getGeneralDirectorStatus() {
        return generalDirectorStatus;
    }

    public void setGeneralDirectorStatus(Integer generalDirectorStatus) {
        this.generalDirectorStatus = generalDirectorStatus;
    }

    public Integer getEliminatePlanNumber() {
        return eliminatePlanNumber;
    }

    public void setEliminatePlanNumber(Integer eliminatePlanNumber) {
        this.eliminatePlanNumber = eliminatePlanNumber;
    }
}
