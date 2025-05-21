package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.*;

import java.util.Date;

// Vị trí cần tuyển trong kế hoạch tuyển dụng
@Table(name = "tbl_recruitment_plan_item")
@Entity
public class RecruitmentPlanItem extends BaseObject {
    private static final long serialVersionUID = 991992518344617174L;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recruitment_plan_id")
    private RecruitmentPlan recruitmentPlan; // yeu cau

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_title_id")
    private PositionTitle positionTitle; // Vị trí cần tuyển

    @Column(name = "quantity")
    private Integer quantity;


    public RecruitmentPlan getRecruitmentPlan() {
        return recruitmentPlan;
    }

    public void setRecruitmentPlan(RecruitmentPlan recruitmentPlan) {
        this.recruitmentPlan = recruitmentPlan;
    }

    public PositionTitle getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(PositionTitle positionTitle) {
        this.positionTitle = positionTitle;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

