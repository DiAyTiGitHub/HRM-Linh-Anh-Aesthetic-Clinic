package com.globits.salary.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.*;

// Nhóm thành phần lương hiển thị trên bảng lương
// Nhóm thành phần => Nhóm các cột trên bảng hiển thị
@Table(name = "tbl_salary_result_item_group")
@Entity
public class SalaryResultItemGroup extends BaseObject  {
    private static final long serialVersionUID = 1L;

    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_result_id")
    private SalaryResult salaryResult;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "copied_template_item_group_id")
    private SalaryTemplateItemGroup copiedTemplateItemGroup; // được copy từ nhóm thành phần lương nào

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

    public SalaryResult getSalaryResult() {
        return salaryResult;
    }

    public void setSalaryResult(SalaryResult salaryResult) {
        this.salaryResult = salaryResult;
    }

    public SalaryTemplateItemGroup getCopiedTemplateItemGroup() {
        return copiedTemplateItemGroup;
    }

    public void setCopiedTemplateItemGroup(SalaryTemplateItemGroup copiedTemplateItemGroup) {
        this.copiedTemplateItemGroup = copiedTemplateItemGroup;
    }
}
