package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/*
 * Ngân hàng
 */
@Entity
@Table(name = "tbl_bank")
public class Bank extends BaseObject {
    private static final long serialVersionUID = 6014783475303579207L;

    @Column(name = "code")
    private String code; // Mã ngân hàng

    @Column(name = "name")
    private String name; // Tên ngân hàng

    @Column(name = "description", length = 4000)
    private String description; // Mô tả


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
}
