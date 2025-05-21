package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.*;

// Các vị trí định biên
@Table(name = "tbl_hr_resource_plan_item")
@Entity
public class HrResourcePlanItem extends BaseObject {

    @ManyToOne
    @JoinColumn(name = "resource_plan_id")
    private HrResourcePlan resourcePlan; // Thuộc bảng định biên nhân sự nào

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_title_id")
    private PositionTitle positionTitle; // chức danh tương ứng
    
    @Column(name = "current_position_number")
    private Integer currentPositionNumber; // số lượng định biên (tất cả position của chức danh và phòng ban đó)
    
    @Column(name = "current_staff_number")
    private Integer currentStaffNumber; // số lượng thực tế (tất cả position có staff của chức danh và phòng ban đó)
    
    @Column(name = "eliminate_plan_number")
    private Integer eliminatePlanNumber; // số lượng cần lọc
    
    @Column(name = "additional_number")
    private Integer additionalNumber; //số lượng cần bổ sung (số lượng định biên - số lượng thực tế)


    public HrResourcePlan getResourcePlan() {
        return resourcePlan;
    }

    public void setResourcePlan(HrResourcePlan resourcePlan) {
        this.resourcePlan = resourcePlan;
    }

    public PositionTitle getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(PositionTitle positionTitle) {
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
