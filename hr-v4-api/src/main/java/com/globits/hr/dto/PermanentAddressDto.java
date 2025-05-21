package com.globits.hr.dto;

import java.util.UUID;

public class PermanentAddressDto {
	 private UUID staffId;
	 private String administrativeUnitCode;
	 private String administrativeUnitValue;
	 private String districtCode;
	 private String districtValue;
	 private String provinceCode;
	 private String provinceValue;
	 
	 public PermanentAddressDto(UUID staffId,
             String administrativeUnitCode,
             String administrativeUnitValue,
             String districtCode,
             String districtValue,
             String provinceCode,
             String provinceValue) {
		this.staffId = staffId;
		this.administrativeUnitCode = administrativeUnitCode;
		this.administrativeUnitValue = administrativeUnitValue;
		this.districtCode = districtCode;
		this.districtValue = districtValue;
		this.provinceCode = provinceCode;
		this.provinceValue = provinceValue;
	}

	public UUID getStaffId() {
		return staffId;
	}

	public void setStaffId(UUID staffId) {
		this.staffId = staffId;
	}

	public String getAdministrativeUnitCode() {
		return administrativeUnitCode;
	}

	public void setAdministrativeUnitCode(String administrativeUnitCode) {
		this.administrativeUnitCode = administrativeUnitCode;
	}

	public String getAdministrativeUnitValue() {
		return administrativeUnitValue;
	}

	public void setAdministrativeUnitValue(String administrativeUnitValue) {
		this.administrativeUnitValue = administrativeUnitValue;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}

	public String getDistrictValue() {
		return districtValue;
	}

	public void setDistrictValue(String districtValue) {
		this.districtValue = districtValue;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getProvinceValue() {
		return provinceValue;
	}

	public void setProvinceValue(String provinceValue) {
		this.provinceValue = provinceValue;
	}
	 
}
