package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.HrDocumentItem;
import com.globits.hr.domain.HrDocumentTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HrDocumentTemplateDto extends BaseObjectDto {
    private String code; // mã bộ tài liệu
    private String name; // tên bộ tài liệu
    private String description; // mô tả tài liệu
    private List<HrDocumentItemDto> documentItems; // các tài liệu trong bộ hồ sơ/tài liệu

    public HrDocumentTemplateDto() {
    }

    public HrDocumentTemplateDto(HrDocumentTemplate entity, Boolean isGetFull) {
        super(entity);
        if (entity != null) {
            this.code = entity.getCode();
            this.name = entity.getName();
            this.description = entity.getDescription();
            if (Boolean.TRUE.equals(isGetFull) && entity.getDocumentItems() != null) {
                this.documentItems = new ArrayList<>();
                for (HrDocumentItem item : entity.getDocumentItems()) {
                    this.documentItems.add(new HrDocumentItemDto(item, false));
                }

                // Sắp xếp danh sách theo displayOrder, đặt null xuống cuối
                this.documentItems.sort(Comparator.comparing(
                        HrDocumentItemDto::getDisplayOrder, 
                        Comparator.nullsLast(Integer::compare))
                );
            }
        }
    }

    public HrDocumentTemplateDto(HrDocumentTemplate entity) {
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

    public List<HrDocumentItemDto> getDocumentItems() {
        return documentItems;
    }

    public void setDocumentItems(List<HrDocumentItemDto> documentItems) {
        this.documentItems = documentItems;
    }
}
