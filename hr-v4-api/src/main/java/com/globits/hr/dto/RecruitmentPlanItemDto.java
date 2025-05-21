package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.RecruitmentPlanItem;

import java.util.UUID;

public class RecruitmentPlanItemDto extends BaseObjectDto {
    private UUID recruitmentPlanId;
    private RecruitmentPlanDto recruitmentPlan; // yeu cau
    private PositionTitleDto positionTitle; // Vị trí cần tuyển
    private Integer quantity;

    public RecruitmentPlanItemDto() {
    }

    public RecruitmentPlanItemDto(RecruitmentPlanItem entity, Boolean isGetFull) {
        super(entity);
        if (entity != null) {
            this.quantity = entity.getQuantity();
            if (entity.getRecruitmentPlan() != null) {
                this.recruitmentPlanId = entity.getRecruitmentPlan().getId();
            }
            if (entity.getPositionTitle() != null) {
                this.positionTitle = new PositionTitleDto(entity.getPositionTitle());
            }
            if (isGetFull) {
                if (entity.getRecruitmentPlan() != null) {
                    this.recruitmentPlan = new RecruitmentPlanDto(entity.getRecruitmentPlan());
                }
            }
        }
    }

    public RecruitmentPlanItemDto(RecruitmentPlanItem entity) {
        this(entity, true);
    }

    public UUID getRecruitmentPlanId() {
        return recruitmentPlanId;
    }

    public void setRecruitmentPlanId(UUID recruitmentPlanId) {
        this.recruitmentPlanId = recruitmentPlanId;
    }

    public RecruitmentPlanDto getRecruitmentPlan() {
        return recruitmentPlan;
    }

    public void setRecruitmentPlan(RecruitmentPlanDto recruitmentPlan) {
        this.recruitmentPlan = recruitmentPlan;
    }

    public PositionTitleDto getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(PositionTitleDto positionTitle) {
        this.positionTitle = positionTitle;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
