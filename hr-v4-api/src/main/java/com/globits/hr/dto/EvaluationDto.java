package com.globits.hr.dto;

import java.util.UUID;

public class EvaluationDto {
    UUID evaluationValueId;
    UUID itemId;
    Integer value;

    public EvaluationDto() {
    }

    public EvaluationDto(UUID evaluationValueId,UUID itemId, Integer value) {
        this.evaluationValueId = evaluationValueId;
        this.itemId = itemId;
        this.value = value;
    }

    public UUID getEvaluationValueId() {
        return evaluationValueId;
    }

    public void setEvaluationValueId(UUID evaluationValueId) {
        this.evaluationValueId = evaluationValueId;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }
}
