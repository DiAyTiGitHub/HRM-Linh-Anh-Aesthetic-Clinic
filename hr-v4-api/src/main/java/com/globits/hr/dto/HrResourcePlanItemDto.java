package com.globits.hr.dto;

import com.globits.core.domain.BaseObject;
import com.globits.hr.domain.HrResourcePlanItem;

import java.util.UUID;

// Các vị trí định biên
public class HrResourcePlanItemDto extends BaseObject {
    private UUID resourcePlanId; // Thuộc bảng định biên nhân sự nào
    private PositionTitleDto positionTitle; // chức danh tương ứng
    private Integer currentPositionNumber; // số lượng định biên (tất cả position của chức danh và phòng ban đó)
    private Integer currentStaffNumber; //số lượng thực tế (tất cả position có staff của chức danh và phòng ban đó)
    private Integer eliminatePlanNumber; //số lượng cần lọc
    private Integer additionalNumber; //số lượng cần bổ sung (số lượng định biên - số lượng thực tế)

    public HrResourcePlanItemDto(HrResourcePlanItem entity) {
        super(entity);

        this.positionTitle = new PositionTitleDto(entity.getPositionTitle());

        if (entity.getResourcePlan() != null) {
            this.resourcePlanId = entity.getResourcePlan().getId();
        }

        this.currentPositionNumber = entity.getCurrentPositionNumber(); // số lượng định biên (tất cả position của chức danh và phòng ban đó)
        this.currentStaffNumber = entity.getCurrentStaffNumber(); //số lượng thực tế (tất cả position có staff của chức danh và phòng ban đó)
        this.eliminatePlanNumber = entity.getEliminatePlanNumber(); //số lượng cần lọc
        this.additionalNumber = entity.getAdditionalNumber(); //số lượng cần bổ sung (số lượng định biên - số lượng thực tế)

    }

    public HrResourcePlanItemDto() {
    }

    public UUID getResourcePlanId() {
        return resourcePlanId;
    }

    public void setResourcePlanId(UUID resourcePlanId) {
        this.resourcePlanId = resourcePlanId;
    }

    public HrResourcePlanItemDto(BaseObject object) {
        super(object);
    }

    public PositionTitleDto getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(PositionTitleDto positionTitle) {
        this.positionTitle = positionTitle;
    }

    public Integer getCurrentPositionNumber() {
        return currentPositionNumber;
    }

    public void setCurrentPositionNumber(Integer currentPositionNumber) {
        this.currentPositionNumber = currentPositionNumber;
    }

    public Integer getCurrentStaffNumber() {
        return currentStaffNumber;
    }

    public void setCurrentStaffNumber(Integer currentStaffNumber) {
        this.currentStaffNumber = currentStaffNumber;
    }

    public Integer getEliminatePlanNumber() {
        return eliminatePlanNumber;
    }

    public void setEliminatePlanNumber(Integer eliminatePlanNumber) {
        this.eliminatePlanNumber = eliminatePlanNumber;
    }

    public Integer getAdditionalNumber() {
        return additionalNumber;
    }

    public void setAdditionalNumber(Integer additionalNumber) {
        this.additionalNumber = additionalNumber;
    }
}
