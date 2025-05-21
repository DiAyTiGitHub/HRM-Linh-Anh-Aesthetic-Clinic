package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.Bank;

public class BankDto extends BaseObjectDto {
    private String name;
    private String code;
    private String description;

    public BankDto() {
    }

    public BankDto(Bank entity) {
        super(entity);

        if (entity == null) return;

        this.code = entity.getCode();
        this.name = entity.getName();
        this.description = entity.getDescription();
    }

    public BankDto(Bank entity, Boolean isDetail) {
        this(entity);

        if (isDetail == null || isDetail.equals(false)) {
            return;
        }

        // other detail...
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
