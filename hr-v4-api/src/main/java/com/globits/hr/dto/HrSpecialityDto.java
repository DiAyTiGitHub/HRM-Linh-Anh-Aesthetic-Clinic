package com.globits.hr.dto;

import com.globits.hr.domain.HrSpeciality;
import com.globits.core.dto.BaseObjectDto;

public class HrSpecialityDto extends BaseObjectDto {
    private String name;
    private String code;
    private String numbericCode;
    private String nameEng;//tên tiếng anh

    public String getNumbericCode() {
        return numbericCode;
    }

    public void setNumbericCode(String numbericCode) {
        this.numbericCode = numbericCode;
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

    public String getNameEng() {
        return nameEng;
    }

    public void setNameEng(String nameEng) {
        this.nameEng = nameEng;
    }

    public HrSpecialityDto() {

    }

    public HrSpecialityDto(HrSpeciality spe) {
        this.id = spe.getId();
        this.code = spe.getCode();
        this.name = spe.getName();
        this.nameEng = spe.getNameEng();
        this.numbericCode = spe.getNumbericCode();
    }

    public HrSpecialityDto(String code, String name, String nameEng) {
        this.code = code;
        this.name = name;
        this.nameEng = nameEng;
    }
}
