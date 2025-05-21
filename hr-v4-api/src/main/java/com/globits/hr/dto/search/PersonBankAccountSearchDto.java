package com.globits.hr.dto.search;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PersonBankAccountSearchDto extends SearchDto {
    private UUID bankId;

    public UUID getBankId() {
        return bankId;
    }

    public void setBankId(UUID bankId) {
        this.bankId = bankId;
    }
}
