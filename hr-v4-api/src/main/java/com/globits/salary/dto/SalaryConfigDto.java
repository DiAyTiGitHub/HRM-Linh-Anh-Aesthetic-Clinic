package com.globits.salary.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.globits.core.dto.DepartmentDto;
import com.globits.hr.domain.HRDepartment;
import com.globits.hr.dto.HRDepartmentDto;
import com.globits.salary.domain.SalaryConfig;
import com.globits.salary.domain.SalaryConfigDepartment;
import com.globits.salary.domain.SalaryConfigItem;
import com.globits.core.dto.BaseObjectDto;
import com.globits.salary.domain.SalaryType;

public class SalaryConfigDto extends BaseObjectDto {

    private String name;
    private String otherName;
    private String code;
    private String description;// mo ta
    private Integer defaultValue;// gia tri mac dinh
    private SalaryTypeDto salaryType;// nhom du lieu
    private List<HRDepartmentDto> departments;

    private List<SalaryConfigItemDto> salaryConfigItems;

    private Boolean voided; // trang thai

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDefaultValue(Integer defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setSalaryType(SalaryTypeDto salaryType) {
        this.salaryType = salaryType;
    }


    public String getOtherName() {
        return otherName;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDefaultValue() {
        return defaultValue;
    }

    public SalaryTypeDto getSalaryType() {
        return salaryType;
    }


    public SalaryConfigDto() {

    }

    public List<HRDepartmentDto> getDepartments() {
        return departments;
    }

    public void setDepartments(List<HRDepartmentDto> departments) {
        this.departments = departments;
    }


    public SalaryConfigDto(SalaryConfig s, boolean isDetail) {
        if (s != null) {
            this.setId(s.getId());
            this.setCode(s.getCode());
            this.setName(s.getName());
            this.setDescription(s.getDescription());
            this.setOtherName(s.getOtherName());
            this.setDefaultValue(s.getDefaultValue());
            this.setVoided(s.getVoided());
            if (s.getSalaryType() != null) {
                this.salaryType = new SalaryTypeDto(s.getSalaryType());
            }
            // if (s.getDepartment() != null) {
            //     this.department = new DepartmentDto(s.getDepartment());
            // }
            if (s.getSalaryConfigItems() != null && s.getSalaryConfigItems().size() > 0) {
                this.salaryConfigItems = new ArrayList<SalaryConfigItemDto>();
                for (SalaryConfigItem si : s.getSalaryConfigItems()) {
                    SalaryConfigItemDto siDto = new SalaryConfigItemDto();
                    if (si.getSalaryConfig() != null) {
                        SalaryConfig salaryConfig = si.getSalaryConfig();
                        SalaryConfigDto salaryConfigDto = new SalaryConfigDto();
                        salaryConfigDto.setCode(si.getSalaryConfig().getCode());
                        salaryConfigDto.setName(si.getSalaryConfig().getName());
                        // if (salaryConfig.getDepartment() != null) {
                        //     DepartmentDto depDto = new DepartmentDto();
                        //     depDto.setCode(salaryConfig.getCode());
                        //     depDto.setDepartmentType(salaryConfig.getDepartment().getDepartmentType());
                        //     depDto.setId(salaryConfig.getDepartment().getId());
                        //     depDto.setName(salaryConfig.getDepartment().getName());
                        // }
                        siDto.setSalaryConfig(salaryConfigDto);
                    }
                    siDto.setId(si.getId());
                    if (si.getSalaryItem() != null) {

                        SalaryItemDto salaryItemDto = new SalaryItemDto();
                        salaryItemDto.setCode(si.getSalaryItem().getCode());
                        salaryItemDto.setName(si.getSalaryItem().getName());
                        salaryItemDto.setType(si.getSalaryItem().getType());
                        salaryItemDto.setFormula(si.getSalaryItem().getFormula());
//                        salaryItemDto.setIsDefault(si.getSalaryItem().getIsDefault());
                        salaryItemDto.setIsActive(si.getSalaryItem().getIsActive());
//                        salaryItemDto.setDefaultValue(si.getSalaryItem().getDefaultValue());
                        siDto.setSalaryItem(salaryItemDto);
                    }
                    this.salaryConfigItems.add(siDto);

                }
            }
            if (s.getSalaryType() != null && isDetail) {
                this.setSalaryType(new SalaryTypeDto(s.getSalaryType()));
            }
            if (s.getDepartments() != null && isDetail) {
                List<HRDepartmentDto> HRDepartments = new ArrayList<>();
                for (SalaryConfigDepartment element : s.getDepartments()) {
                    HRDepartmentDto hrDepartment = new HRDepartmentDto(element.getDepartment());
                    HRDepartments.add(hrDepartment);
                }
                this.setDepartments(HRDepartments);
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

    public List<SalaryConfigItemDto> getSalaryConfigItems() {
        return salaryConfigItems;
    }

    public void setSalaryConfigItems(List<SalaryConfigItemDto> salaryConfigItems) {
        this.salaryConfigItems = salaryConfigItems;
    }

    public Boolean getVoided() {
        return voided;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }
}
