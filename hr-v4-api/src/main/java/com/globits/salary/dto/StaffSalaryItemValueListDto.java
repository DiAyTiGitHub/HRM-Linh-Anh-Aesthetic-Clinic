package com.globits.salary.dto;

import java.util.List;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.dto.StaffDto;
import com.globits.salary.domain.StaffSalaryItemValue;

public class StaffSalaryItemValueListDto extends BaseObjectDto {
	private SalaryTemplateDto salaryTemplate;
    private StaffDto staff;
    private List<StaffSalaryItemValueDto> staffSalaryItemValue;

    public StaffSalaryItemValueListDto() {
    }

    public StaffSalaryItemValueListDto(StaffSalaryItemValue entity, Boolean isDetail) {
        super(entity);
        if (entity != null) {
        }
    }

    public StaffSalaryItemValueListDto(StaffSalaryItemValue entity) {
        this(entity, true);
    }
    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }
    public SalaryTemplateDto getSalaryTemplate() {
        return salaryTemplate;
    }

    public void setSalaryTemplate(SalaryTemplateDto salaryTemplate) {
        this.salaryTemplate = salaryTemplate;
    }

	public List<StaffSalaryItemValueDto> getStaffSalaryItemValue() {
		return staffSalaryItemValue;
	}

	public void setStaffSalaryItemValue(List<StaffSalaryItemValueDto> staffSalaryItemValue) {
		this.staffSalaryItemValue = staffSalaryItemValue;
	}
}
