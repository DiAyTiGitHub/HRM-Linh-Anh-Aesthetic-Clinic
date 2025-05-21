package com.globits.hr.dto.search;

import java.util.UUID;

public class SearchHrDocumentItemDto extends SearchDto {
    private UUID documentTemplateId;

    public SearchHrDocumentItemDto() {
    }

    public UUID getDocumentTemplateId() {
        return documentTemplateId;
    }

    public void setDocumentTemplateId(UUID documentTemplateId) {
        this.documentTemplateId = documentTemplateId;
    }
}
