package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.HrDocumentItem;
import com.globits.hr.domain.HrDocumentTemplate;

public class HrDocumentItemDto extends BaseObjectDto {
    private String code; // mã tài liệu
    private String name; // tên tài liệu cần nộp
    private String description; // mô tả tài liệu
    private Integer displayOrder; // thứ tự hiển thị
    private Boolean isRequired; //Cần phải nộp
    private HrDocumentTemplateDto documentTemplate; // Thuộc bộ tài liệu nào

    public HrDocumentItemDto() {
    }

    public HrDocumentItemDto(HrDocumentItem entity, Boolean isGetFull) {
        super(entity);
        if (entity != null) {
            this.code = entity.getCode();
            this.name = entity.getName();
            this.description = entity.getDescription();
            this.displayOrder = entity.getDisplayOrder();
            this.isRequired = entity.getRequired();
            if (isGetFull) {
                if (entity.getDocumentTemplate() != null) {
                    this.documentTemplate = new HrDocumentTemplateDto(entity.getDocumentTemplate(), false);
                }
            }
        }
    }

    public HrDocumentItemDto(HrDocumentItem entity) {
        this(entity, true);
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public HrDocumentTemplateDto getDocumentTemplate() {
        return documentTemplate;
    }

    public void setDocumentTemplate(HrDocumentTemplateDto documentTemplate) {
        this.documentTemplate = documentTemplate;
    }
}
