package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.HrDepartmentIp;
import com.globits.hr.domain.HrOrganization;

public class HrDepartmentIpDto extends BaseObjectDto {
    private HRDepartmentDto department;
    private String ipAddress;
    private String description;

    public HrDepartmentIpDto() {
    }

    public HrDepartmentIpDto(HrDepartmentIp entity) {
        if (entity != null) {
            this.setId(entity.getId());
            this.ipAddress = entity.getIpAddress();
            this.description = entity.getDescription();

            if (entity.getDepartment() != null) {
                this.department = new HRDepartmentDto();
                this.getDepartment().setId(entity.getDepartment().getId());
                this.getDepartment().setName(entity.getDepartment().getName());
                this.getDepartment().setCode(entity.getDepartment().getCode());
                this.getDepartment().setShortName(entity.getDepartment().getShortName());
                if (entity.getDepartment().getOrganization() != null) {
                	HrOrganization hrOrganization = entity.getDepartment().getOrganization();
                	HrOrganizationDto orgDto = new HrOrganizationDto();
                	orgDto.setId(hrOrganization.getId());
                	orgDto.setName(hrOrganization.getName());
                	orgDto.setCode(hrOrganization.getCode());
                	this.department.setOrganization(orgDto);
                }
                
            }
        }
    }

    public HRDepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartmentDto department) {
        this.department = department;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
