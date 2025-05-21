/*
 * TA va Giang làm
 */

package com.globits.hr.dto;

import com.globits.hr.domain.AcademicTitle;
import com.globits.core.dto.BaseObjectDto;

public class AcademicTitleDto extends BaseObjectDto {

    private String name;
    private String code;
    private String description;
    private Integer level;

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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public AcademicTitleDto() {

    }

    public AcademicTitleDto(AcademicTitle ac) {

        if (ac != null) {
            this.setId(ac.getId());
            this.setCode(ac.getCode());
            this.setName(ac.getName());
            this.level = ac.getLevel();
        }
    }
}
