package com.globits.budget.domain;

import jakarta.persistence.*;

import java.util.Set;

/**
 * Loại khoản.
 * Ví dụ: dọn vệ sinh, mua sắm công cụ dụng cụ, đăng ký bản quyền...
 *
 * @author 91hai
 */
@Entity
@Table(name = "tbl_budget_category")
public class BudgetCategory extends BaseNameCodeObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Column(name = "code", unique = true)
    private String code;//Mã loại khoản

    @Column(name = "icon")
    private String icon;//Icon loại khoản, nếu cần thiết

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}
