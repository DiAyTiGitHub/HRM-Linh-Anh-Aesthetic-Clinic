package com.globits.hr.dto;

import com.globits.core.domain.GlobalProperty;

public class HrGlobalPropertyDto {
    private String property;
    private String propertyName;
    private String propertyValue;
    private String description;
    private String dataTypeName;

    public HrGlobalPropertyDto() {
    }

    public HrGlobalPropertyDto(GlobalProperty entity) {
        if(entity!=null){
            this.property = entity.getProperty();
            this.propertyName = entity.getPropertyName();
            this.propertyValue = entity.getPropertyValue();
            this.description = entity.getDescription();
            this.dataTypeName = entity.getDataTypeName();
        }
    }

    public GlobalProperty toEntity() {
        GlobalProperty entity = new GlobalProperty();
        entity.setProperty(this.property);
        entity.setPropertyName(this.propertyName);
        entity.setPropertyValue(this.propertyValue);
        entity.setDescription(this.description);
        entity.setDataTypeName(this.dataTypeName);
        return entity;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDataTypeName() {
        return dataTypeName;
    }

    public void setDataTypeName(String dataTypeName) {
        this.dataTypeName = dataTypeName;
    }
}
