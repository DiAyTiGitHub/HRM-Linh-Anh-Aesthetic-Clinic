package com.globits.budget.dto;

import com.globits.core.dto.SearchDto;

public class BudgetSearchDto extends SearchDto {
    private String voucherCode;//mã hóa đơn
    private Integer voucherType;//1= thu, -1=chi

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public Integer getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(Integer voucherType) {
        this.voucherType = voucherType;
    }
}
