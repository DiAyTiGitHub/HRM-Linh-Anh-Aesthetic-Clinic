package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.SystemConfig;
import jakarta.persistence.Column;

public class SystemConfigDto extends BaseObjectDto {
    private String configKey;
    private String configValue;
    private String note;// ghi chú
    private Integer numberOfZero;
    public SystemConfigDto() {
        super();
    }

    public SystemConfigDto(SystemConfig entity) {
        super(entity);

        if (entity == null) return;

        this.id = entity.getId();
        this.configKey = entity.getConfigKey();
        this.configValue = entity.getConfigValue();
        this.note = entity.getNote();
        this.numberOfZero = entity.getNumberOfZero();
    }

    public SystemConfigDto(String configKey,
                           String configValue,
                           String note) {
        this.configKey = configKey;
        this.configValue = configValue;
        this.note = note;
    }

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
