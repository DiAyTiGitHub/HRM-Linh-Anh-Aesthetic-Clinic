package com.globits.hr.dto;

import com.globits.core.domain.BaseObject;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.EvaluationItem;

import java.util.List;

public class EvaluationItemDto extends BaseObject {
    private String name;
    private String description;
    private String code;
    private List<EvaluationTemplateDto> items;
    public EvaluationItemDto() {
    }

    public EvaluationItemDto(EvaluationItem item) {
        super(item);
        this.name = item.getName();
        this.code = item.getCode();
        this.description = item.getDescription();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
