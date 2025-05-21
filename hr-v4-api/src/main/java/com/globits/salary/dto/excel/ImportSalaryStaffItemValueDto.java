package com.globits.salary.dto.excel;

public class ImportSalaryStaffItemValueDto {
	private Integer cellIndex;
    private String salaryItemCode;
    private String salaryItemValue;

    public ImportSalaryStaffItemValueDto(){

    }
    
    public ImportSalaryStaffItemValueDto(ImportSalaryStaffItemValueDto item) {
		this.cellIndex = item.getCellIndex();
		this.salaryItemCode = item.getSalaryItemCode();
		this.salaryItemValue = item.getSalaryItemValue();
	}

	public Integer getCellIndex() {
		return cellIndex;
	}

	public void setCellIndex(Integer cellIndex) {
		this.cellIndex = cellIndex;
	}

	public String getSalaryItemCode() {
        return salaryItemCode;
    }

    public void setSalaryItemCode(String salaryItemCode) {
        this.salaryItemCode = salaryItemCode;
    }

    public String getSalaryItemValue() {
        return salaryItemValue;
    }

    public void setSalaryItemValue(String salaryItemValue) {
        this.salaryItemValue = salaryItemValue;
    }
}
