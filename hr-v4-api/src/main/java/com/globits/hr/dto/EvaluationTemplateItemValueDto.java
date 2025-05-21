package com.globits.hr.dto;

import com.globits.core.domain.BaseObject;
import com.globits.hr.domain.EvaluationCandidateRound;
import com.globits.hr.domain.EvaluationTemplateItem;
import com.globits.hr.domain.EvaluationTemplateItemValue;
import jakarta.persistence.*;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EvaluationTemplateItemValueDto extends BaseObject {

    private UUID itemId;
    private UUID parentItemId;
    private EvaluationTemplateItemDto item;
    private EvaluationCandidateRoundDto evaluationCandidateRound;
    private Integer numberSortItem;
    private Integer value;
    private String generalValue;
    private List<EvaluationTemplateItemValueDto> children;
    public EvaluationTemplateItemValueDto() {
    }


    public EvaluationTemplateItemValueDto(EvaluationTemplateItemValue entity) {
        super(entity);
        this.value = entity.getValue();
        this.generalValue = entity.getGeneralValue();

        if (entity.getEvaluationTemplateItem() != null) {
            this.item = new EvaluationTemplateItemDto(entity.getEvaluationTemplateItem(), false);
            this.itemId = entity.getEvaluationTemplateItem().getId();
            this.numberSortItem = entity.getEvaluationTemplateItem().getOrder();

            if (entity.getEvaluationTemplateItem().getParent() != null) {
                this.parentItemId = entity.getEvaluationTemplateItem().getParent().getId();
            }

            if (entity.getChildren() != null && !entity.getChildren().isEmpty()) {
                this.children = entity.getChildren().stream()
                        .map(EvaluationTemplateItemValueDto::new)
                        .sorted(Comparator.comparing(EvaluationTemplateItemValueDto::getNumberSortItem))
                        .collect(Collectors.toList());

            }
        }
    }

    public EvaluationTemplateItemValueDto(EvaluationTemplateItemValue entity, Boolean getFull) {
        this(entity);
        if (getFull) {
            if (entity.getEvaluationCandidateRound() != null) {
                this.evaluationCandidateRound = new EvaluationCandidateRoundDto(entity.getEvaluationCandidateRound());
            }
        }
    }

    public List<EvaluationTemplateItemValueDto> getChildren() {
        return children;
    }

    public void setChildren(List<EvaluationTemplateItemValueDto> children) {
        this.children = children;
    }

    public Integer getNumberSortItem() {
        return numberSortItem;
    }

    public void setNumberSortItem(Integer numberSortItem) {
        this.numberSortItem = numberSortItem;
    }

    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    public UUID getParentItemId() {
        return parentItemId;
    }

    public void setParentItemId(UUID parentItemId) {
        this.parentItemId = parentItemId;
    }

    public String getGeneralValue() {
        return generalValue;
    }

    public void setGeneralValue(String generalValue) {
        this.generalValue = generalValue;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public EvaluationTemplateItemDto getItem() {
        return item;
    }

    public void setItem(EvaluationTemplateItemDto item) {
        this.item = item;
    }

    public EvaluationCandidateRoundDto getEvaluationCandidateRound() {
        return evaluationCandidateRound;
    }

    public void setEvaluationCandidateRound(EvaluationCandidateRoundDto evaluationCandidateRound) {
        this.evaluationCandidateRound = evaluationCandidateRound;
    }
}
