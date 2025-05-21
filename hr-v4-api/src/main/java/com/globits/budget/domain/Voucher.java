package com.globits.budget.domain;

import java.util.Date;
import java.util.Set;

import com.globits.core.domain.BaseObject;

import com.globits.security.domain.User;
import jakarta.persistence.*;

/**
 * Hóa đơn thu - chi
 *
 * @author 91hai
 */
@Entity
@Table(name = "tbl_voucher")
public class Voucher extends BaseObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Column(name = "voucher_date")
    private Date voucherDate;//Ngày hóa đơn

    @Column(name = "total_amount")
    private Double totalAmount;//Tổng số tiền thu - chi

    @Column(name = "voucher_code")
    private String voucherCode;//mã hóa đơn

    @Column(name = "voucher_type")
    private Integer voucherType;//1= thu, -1=chi

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "voucher", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VoucherItem> voucherItems;//Danh sách khoản thu - chi

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "budget_id")
    private Budget budget;
    @OneToOne
    private User spender;

    @OneToOne
    private User approver;

    public Date getVoucherDate() {
        return voucherDate;
    }

    public void setVoucherDate(Date voucherDate) {
        this.voucherDate = voucherDate;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

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


    public Set<VoucherItem> getVoucherItems() {
        return voucherItems;
    }

    public void setVoucherItems(Set<VoucherItem> voucherItems) {
        this.voucherItems = voucherItems;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public User getSpender() {
        return spender;
    }

    public void setSpender(User spender) {
        this.spender = spender;
    }

    public User getApprover() {
        return approver;
    }

    public void setApprover(User approver) {
        this.approver = approver;
    }
}
