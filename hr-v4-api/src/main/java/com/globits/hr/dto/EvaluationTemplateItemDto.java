package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.EvaluationTemplateItem;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EvaluationTemplateItemDto extends BaseObjectDto {
    private UUID templateId;
    private EvaluationItemDto item;
    private Integer numberOrder;
    private UUID parentId;
    private HrConstants.EvaluationTemplateItemContentType contentType;
    private List<EvaluationTemplateItemDto> items;

    public EvaluationTemplateItemDto() {
    }

    public EvaluationTemplateItemDto(EvaluationTemplateItem entity) {
        super(entity); // nếu BaseObjectDto chứa id, createdDate, etc.
        this.templateId = entity.getTemplate() != null ? entity.getTemplate().getId() : null;
        this.item = entity.getItem() != null ? new EvaluationItemDto(entity.getItem()) : null;
        this.numberOrder = entity.getOrder();
        this.parentId = entity.getParent() != null ? entity.getParent().getId() : null;
        this.contentType = entity.getContentType();
        this.items = entity.getChildren().stream()
                .map(EvaluationTemplateItemDto::new)
                .sorted(Comparator.comparing(EvaluationTemplateItemDto::getNumberOrder))
                .collect(Collectors.toList());
    }

    public EvaluationTemplateItemDto(EvaluationTemplateItem entity, boolean simple) {
        super(entity); // nếu BaseObjectDto chứa id, createdDate, etc.
        this.templateId = entity.getTemplate() != null ? entity.getTemplate().getId() : null;
        this.item = entity.getItem() != null ? new EvaluationItemDto(entity.getItem()) : null;
        this.numberOrder = entity.getOrder();
        this.parentId = entity.getParent() != null ? entity.getParent().getId() : null;
        this.contentType = entity.getContentType();
    }


    public EvaluationItemDto getItem() {
        return item;
    }

    public void setItem(EvaluationItemDto item) {
        this.item = item;
    }

    public UUID getTemplateId() {
        return templateId;
    }

    public void setTemplateId(UUID templateId) {
        this.templateId = templateId;
    }

    public Integer getNumberOrder() {
        return numberOrder;
    }

    public void setNumberOrder(Integer numberOrder) {
        this.numberOrder = numberOrder;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public List<EvaluationTemplateItemDto> getItems() {
        return items;
    }

    public void setItems(List<EvaluationTemplateItemDto> items) {
        this.items = items;
    }

    public HrConstants.EvaluationTemplateItemContentType getContentType() {
        return contentType;
    }

    public void setContentType(HrConstants.EvaluationTemplateItemContentType contentType) {
        this.contentType = contentType;
    }
}
