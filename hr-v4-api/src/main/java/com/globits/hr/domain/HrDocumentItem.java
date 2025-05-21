package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.*;

import java.util.Set;

// Định nghĩa các Tài liệu
@Entity
@Table(name = "tbl_hr_document_item")
public class HrDocumentItem extends BaseObject {
    private static final long serialVersionUID = -2208752009903206352L;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_template_id")
    private HrDocumentTemplate documentTemplate; // Thuộc bộ tài liệu nào


    @Column(name = "code")
    private String code; // mã tài liệu

    @Column(name = "name")
    private String name; // tên tài liệu cần nộp

    @Column(name = "description")
    private String description; // mô tả tài liệu

    @Column(name = "display_order")
    private Integer displayOrder; // thứ tự hiển thị

    @Column(name = "is_required")
    private Boolean isRequired = false; //Cần phải nộp

    @OneToMany(mappedBy = "documentItem", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StaffDocumentItem> staffDocumentItems;


    public Set<StaffDocumentItem> getStaffDocumentItems() {
        return staffDocumentItems;
    }

    public void setStaffDocumentItems(Set<StaffDocumentItem> staffDocumentItems) {
        this.staffDocumentItems = staffDocumentItems;
    }

    public Boolean getRequired() {
        return isRequired;
    }

    public void setRequired(Boolean required) {
        isRequired = required;
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

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public HrDocumentTemplate getDocumentTemplate() {
        return documentTemplate;
    }

    public void setDocumentTemplate(HrDocumentTemplate documentTemplate) {
        this.documentTemplate = documentTemplate;
    }

}
