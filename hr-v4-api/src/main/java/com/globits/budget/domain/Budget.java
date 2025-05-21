package com.globits.budget.domain;

import com.globits.core.domain.Organization;

import jakarta.persistence.*;

import java.util.Set;

/**
 * Quỹ
 * Ví du: quỹ hoạt động thường xuyên, quỹ phát triển sản phầm,...
 *
 * @author 91hai
 */
@Entity
@Table(name = "tbl_budget")
public class Budget extends BaseNameCodeObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Column(name = "currency")
    private String currency;//Loại tiền tệ: VND, $, EUR, CNY...
    @Column(name = "opening_balance")
    private Double openingBalance;//Số dư đầu kỳ

    @Column(name = "ending_balance")
    private Double endingBalance;//Số dư cuối kỳ

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(Double openingBalance) {
        this.openingBalance = openingBalance;
    }

    public Double getEndingBalance() {
        return endingBalance;
    }

    public void setEndingBalance(Double endingBalance) {
        this.endingBalance = endingBalance;
    }

}
