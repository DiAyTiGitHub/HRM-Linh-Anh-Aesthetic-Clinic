package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.EvaluationTemplate;
import com.globits.hr.utils.DateTimeUtil;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EvaluationTemplateDto extends BaseObjectDto {
    private String name;
    private String code;
    private List<EvaluationTemplateItemDto> items = new ArrayList<>();

    public EvaluationTemplateDto() {
    }

    public EvaluationTemplateDto(EvaluationTemplate entity, Boolean isDetail) {
        super(entity);
        this.name = entity.getName();
        this.code = entity.getCode();
        if (isDetail != null && isDetail.equals(Boolean.TRUE)) {
            if (!CollectionUtils.isEmpty(entity.getEvaluationTemplateItems())) {
                this.items = entity.getEvaluationTemplateItems().stream()
                        .filter(item -> item.getParent() == null) // hoặc !item.getParent() nếu kiểu Boolean
                        .map(EvaluationTemplateItemDto::new)
                        .sorted(Comparator.comparing(EvaluationTemplateItemDto::getNumberOrder))
                        .collect(Collectors.toList());
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<EvaluationTemplateItemDto> getItems() {
        return items;
    }

    public void setItems(List<EvaluationTemplateItemDto> items) {
        this.items = items;
    }
}
