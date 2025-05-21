package com.globits.salary.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.*;

// Nhóm thành phần => Nhóm các cột trên bảng hiển thị
@Table(name = "tbl_salary_template_item_group")
@Entity
public class SalaryTemplateItemGroup extends BaseObject  {
    private static final long serialVersionUID = 1L;

    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_template_id")
    private SalaryTemplate salaryTemplate;

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

    public SalaryTemplate getSalaryTemplate() {
        return salaryTemplate;
    }

    public void setSalaryTemplate(SalaryTemplate salaryTemplate) {
        this.salaryTemplate = salaryTemplate;
    }
}
