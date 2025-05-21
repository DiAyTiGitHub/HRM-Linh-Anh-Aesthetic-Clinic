package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.RecruitmentItem;

import java.util.UUID;

public class RecruitmentItemDto extends BaseObjectDto {
    private UUID recruitmentId; // thuộc đợt tuyển dụng nào
    private RecruitmentDto recruitment; // thuộc đợt tuyển dụng nào
    private PositionTitleDto positionTitle; // Chức danh cần tuyển dụng
    private Integer quantity; // Số lượng tuyển

    public RecruitmentItemDto() {
    }

    public RecruitmentItemDto(RecruitmentItem entity, Boolean isGetFull) {
        super(entity);
        if (entity != null) {
            this.quantity = entity.getQuantity();
            if (entity.getRecruitment() != null) {
                this.recruitmentId = entity.getRecruitment().getId();
            }
            if (entity.getPositionTitle() != null) {
                this.positionTitle = new PositionTitleDto(entity.getPositionTitle());
            }
            if (isGetFull) {
                if (entity.getRecruitment() != null) {
                    this.recruitment = new RecruitmentDto(entity.getRecruitment());
                }
            }
        }
    }

    public RecruitmentItemDto(RecruitmentItem entity) {
        this(entity, true);
    }

    public UUID getRecruitmentId() {
        return recruitmentId;
    }

    public void setRecruitmentId(UUID recruitmentId) {
        this.recruitmentId = recruitmentId;
    }

    public RecruitmentDto getRecruitment() {
        return recruitment;
    }

    public void setRecruitment(RecruitmentDto recruitment) {
        this.recruitment = recruitment;
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
