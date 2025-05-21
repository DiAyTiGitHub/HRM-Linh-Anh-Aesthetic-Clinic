/*
 * Created by TA2 & Giang on 23/4/2018.
 */

package com.globits.salary.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.salary.domain.SalaryIncrementType;

public class SalaryIncrementTypeDto extends BaseObjectDto {
    private String code;
    private String name;

    public SalaryIncrementTypeDto() {
    }

    public SalaryIncrementTypeDto(SalaryIncrementType sity) {
        if (sity != null) {
            this.setId(sity.getId());
            this.setName(sity.getName());
            this.setCode(sity.getCode());
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
