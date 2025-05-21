package com.globits.hr.domain;

import com.globits.core.domain.BaseObjectEx;
/*
 * cấu hình hệ thống
 */

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
/*
 *  Cấu hình hệ thống
 */
import jakarta.persistence.Table;

@Table(name = "tbl_system_config")
@Entity
public class SystemConfig extends BaseObjectEx {
    private static final long serialVersionUID = 1L;
    @Column(name = "config_key")
	private String configKey;
	@Column(name = "config_value")
	private String configValue;
	@Column(name = "number_of_zero")
	private Integer numberOfZero;
	@Column(name = "note")
	private String note;// ghi chú


	public String getConfigKey() {
		return configKey;
	}

	public void setConfigKey(String configKey) {
		this.configKey = configKey;
	}

	public String getConfigValue() {
		return configValue;
	}

	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Integer getNumberOfZero() {
		return numberOfZero;
	}

	public void setNumberOfZero(Integer numberOfZero) {
		this.numberOfZero = numberOfZero;
	}

}
