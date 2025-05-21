package com.globits.timesheet.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

// Loại nghỉ làm việc
@Table(name = "tbl_leave_type")
@Entity
public class LeaveType extends BaseObject {
    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_paid")
    private Boolean isPaid; // Loại nghỉ này có được tính công/lương hay không

    @Column(name = "used_for_request")
    private Boolean usedForRequest; // Có được sử dụng trong yêu cầu nghỉ phép


    public Boolean getUsedForRequest() {
        return usedForRequest;
    }

    public void setUsedForRequest(Boolean usedForRequest) {
        this.usedForRequest = usedForRequest;
    }

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

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean paid) {
        isPaid = paid;
    }
}
