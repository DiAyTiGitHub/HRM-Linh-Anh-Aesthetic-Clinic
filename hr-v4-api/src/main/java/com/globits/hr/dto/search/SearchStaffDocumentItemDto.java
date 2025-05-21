package com.globits.hr.dto.search;

import java.util.UUID;

public class SearchStaffDocumentItemDto extends SearchDto {
    private UUID hrDocumentTemplateId;
    private UUID hrDocumentItemId;
    private Boolean isSubmitted;

    public UUID getHrDocumentItemId() {
        return hrDocumentItemId;
    }

    public UUID getHrDocumentTemplateId() {
        return hrDocumentTemplateId;
    }

    public void setHrDocumentTemplateId(UUID hrDocumentTemplateId) {
        this.hrDocumentTemplateId = hrDocumentTemplateId;
    }

    public void setHrDocumentItemId(UUID hrDocumentItemId) {
        this.hrDocumentItemId = hrDocumentItemId;
    }

    public Boolean getIsSubmitted() {
        return isSubmitted;
    }

    public void setIsSubmitted(Boolean submitted) {
        isSubmitted = submitted;
    }
}
