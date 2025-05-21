package com.globits.budget.domain;

import com.globits.core.domain.BaseObject;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "tbl_voucher_item")
public class VoucherItem extends BaseObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "budget_category_id")
    private BudgetCategory budgetCategory;//Loai Khoan

    @Column(name = "amount")
    private Double amount;//so tien thu - chi
    @Column(name = "note")
    private String note;//Ghi chu
    @Column(name = "voucher_type")
    private Integer voucherType;//1= thu, -1=chi

    public VoucherItem() {
        // TODO Auto-generated constructor stub
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public BudgetCategory getBudgetCategory() {
        return budgetCategory;
    }

    public void setBudgetCategory(BudgetCategory budgetCategory) {
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
