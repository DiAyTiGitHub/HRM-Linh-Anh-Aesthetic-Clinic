package com.globits.budget.dto;

import com.globits.budget.domain.VoucherItem;
import com.globits.core.dto.BaseObjectDto;

public class VoucherItemDto extends BaseObjectDto {
    private VoucherDto voucher;
    private BudgetCategoryDto budgetCategory;//Loai Khoan
    private Double amount;//so tien thu - chi
    private String note;//Ghi chu
    private Integer voucherType;//1= thu, -1=chi

    public VoucherItemDto() {
        super();
    }

    public VoucherItemDto(VoucherItem entity, Boolean getFull) {
        super(entity);
        if (entity != null) {
            this.amount = entity.getAmount();
            this.note = entity.getNote();
            this.voucherType = entity.getVoucherType();
            if (entity.getBudgetCategory() != null) {
                this.budgetCategory = new BudgetCategoryDto(entity.getBudgetCategory());
            }
            if (getFull) {
                if (entity.getVoucher() != null) {
                    this.voucher = new VoucherDto(entity.getVoucher(), false);
                }

            }
        }
    }

    public VoucherItemDto(VoucherItem entity) {
        this(entity, true);
    }

    public VoucherDto getVoucher() {
        return voucher;
    }

    public void setVoucher(VoucherDto voucher) {
        this.voucher = voucher;
    }

    public BudgetCategoryDto getBudgetCategory() {
        return budgetCategory;
    }

    public void setBudgetCategory(BudgetCategoryDto budgetCategory) {
        this.budgetCategory = budgetCategory;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(Integer voucherType) {
        this.voucherType = voucherType;
    }
}
