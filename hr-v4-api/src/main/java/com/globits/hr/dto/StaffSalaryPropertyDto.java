package com.globits.hr.dto;

import com.globits.hr.domain.StaffSalaryProperty;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.core.dto.BaseObjectDto;

public class StaffSalaryPropertyDto extends BaseObjectDto {
    private String code;
    private String name;
    private String formula;
    private Boolean isDefault;
    private Boolean isActive;
    private Double value;
    private StaffDto staff;
    private SalaryItemDto salaryItem;

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

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public StaffDto getStaff() {
        return staff;
    }

    public void setStaff(StaffDto staff) {
        this.staff = staff;
    }

    public SalaryItemDto getSalaryItem() {
        return salaryItem;
    }

    public void setSalaryItem(SalaryItemDto salaryItem) {
        this.salaryItem = salaryItem;
    }

    public StaffSalaryPropertyDto() {
        super();
    }

    public StaffSalaryPropertyDto(StaffSalaryProperty sp) {
        if (sp != null) {
            this.setValue(sp.getValue());
            if (sp.getSalaryItem() != null) {
                SalaryItemDto si = new SalaryItemDto();
                si.setId(sp.getSalaryItem().getId());
                si.setCode(sp.getSalaryItem().getCode());
//                si.setDefaultValue(sp.getSalaryItem().getDefaultValue());
                si.setName(sp.getSalaryItem().getName());
                si.setFormula(sp.getSalaryItem().getFormula());
                si.setIsActive(sp.getSalaryItem().getIsActive());
                si.setType(sp.getSalaryItem().getType());
                this.salaryItem = si;
            }
            if (sp.getStaff() != null) {
                StaffDto st = new StaffDto();
                st.setId(sp.getStaff().getId());
                st.setBirthDate(sp.getStaff().getBirthDate());
                st.setBirthPlace(sp.getStaff().getBirthPlace());
                st.setDisplayName(sp.getStaff().getDisplayName());
                st.setEmail(sp.getStaff().getEmail());
                st.setPhoneNumber(sp.getStaff().getPhoneNumber());
                st.setPhoto(sp.getStaff().getPhoto());
                this.staff = st;
            }
        }
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
