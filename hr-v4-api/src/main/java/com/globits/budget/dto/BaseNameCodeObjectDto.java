package com.globits.budget.dto;

import com.globits.budget.domain.BaseNameCodeObject;
import com.globits.core.dto.BaseObjectDto;

public class BaseNameCodeObjectDto extends BaseObjectDto {

    private String name;
    private String code;
    private String description;

    public BaseNameCodeObjectDto() {
        super();
    }

    ;

    public BaseNameCodeObjectDto(BaseNameCodeObject entity) {
        super(entity);
        if (entity == null) {
            return;
        }
        this.name = entity.getName();
        this.code = entity.getCode();
        this.description = entity.getDescription();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
