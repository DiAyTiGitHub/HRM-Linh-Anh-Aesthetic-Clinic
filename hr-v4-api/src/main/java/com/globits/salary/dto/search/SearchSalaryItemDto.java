package com.globits.salary.dto.search;

import com.globits.hr.dto.search.SearchDto;

public class SearchSalaryItemDto extends SearchDto {
	private Integer type; // Tính chất của thành phần lương: HrConstants.SalaryItemType
	private Boolean isTaxable; // Thành phần lương này có chịu thuế hay không
	private Boolean isInsurable; // Thành phần lương này có tính BHXH hay không
	private Boolean isActive; // Đang có hiệu lực hay không. VD: = false => Không thể chọn sử dụng thành phần
								// này cho bảng lương mới nữa

	private Integer calculationType; // Cách tính giá trị của thành phần lương này:
										// HrConstants.SalaryItemCalculationType

	public SearchSalaryItemDto() {

	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Boolean getIsTaxable() {
		return isTaxable;
	}

	public void setIsTaxable(Boolean isTaxable) {
		this.isTaxable = isTaxable;
	}

	public Boolean getIsInsurable() {
		return isInsurable;
	}

	public void setIsInsurable(Boolean isInsurable) {
		this.isInsurable = isInsurable;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Integer getCalculationType() {
		return calculationType;
	}

	public void setCalculationType(Integer calculationType) {
		this.calculationType = calculationType;
	}

}
