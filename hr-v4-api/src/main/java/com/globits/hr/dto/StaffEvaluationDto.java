package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.StaffEvaluation;
import com.globits.hr.utils.Const;

import java.util.UUID;

public class StaffEvaluationDto extends BaseObjectDto {
    private UUID itemId;
    private String itemName;
    private UUID staffId;
    private Const.EVALUATION selfEvaluate;
    private Const.EVALUATION managementEvaluate;

    public StaffEvaluationDto(StaffEvaluation staffEvaluation) {
        super(staffEvaluation);
        if(staffEvaluation.getItem() != null) {
            this.itemId = staffEvaluation.getItem().getId();
            this.itemName = staffEvaluation.getItem().getName();
        }
        this.selfEvaluate = staffEvaluation.getSelfEvaluate();
        this.managementEvaluate = staffEvaluation.getManagementEvaluate();
    }

    public StaffEvaluationDto() {
    }

    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    public Const.EVALUATION getSelfEvaluate() {
        return selfEvaluate;
    }

    public void setSelfEvaluate(Const.EVALUATION selfEvaluate) {
        this.selfEvaluate = selfEvaluate;
    }

    public Const.EVALUATION getManagementEvaluate() {
        return managementEvaluate;
    }

    public void setManagementEvaluate(Const.EVALUATION managementEvaluate) {
        this.managementEvaluate = managementEvaluate;
    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
