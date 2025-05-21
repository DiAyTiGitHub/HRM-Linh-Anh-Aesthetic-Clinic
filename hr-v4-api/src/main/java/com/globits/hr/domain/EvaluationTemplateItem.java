package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.globits.hr.HrConstants;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_evaluation_template_item")
public class EvaluationTemplateItem extends BaseObject {

    @ManyToOne
    @JoinColumn(name = "template_id")
    @JsonBackReference
    private EvaluationTemplate template;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private EvaluationItem item;

    @Column(name = "order_no")
    private Integer order;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonBackReference
    private EvaluationTemplateItem parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<EvaluationTemplateItem> children = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type")
    private HrConstants.EvaluationTemplateItemContentType contentType;

    // Getters and Setters

    public EvaluationTemplate getTemplate() {
        return template;
    }

    public void setTemplate(EvaluationTemplate template) {
        this.template = template;
    }

    public EvaluationItem getItem() {
        return item;
    }

    public void setItem(EvaluationItem item) {
        this.item = item;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public EvaluationTemplateItem getParent() {
        return parent;
    }

    public void setParent(EvaluationTemplateItem parent) {
        this.parent = parent;
    }

    public List<EvaluationTemplateItem> getChildren() {
        return children;
    }

    public void setChildren(List<EvaluationTemplateItem> children) {
        this.children = children;
    }

    public void addChild(EvaluationTemplateItem child) {
        child.setParent(this);
        this.children.add(child);
    }

    public void removeChild(EvaluationTemplateItem child) {
        child.setParent(null);
        this.children.remove(child);
    }

    public HrConstants.EvaluationTemplateItemContentType getContentType() {
        return contentType;
    }

    public void setContentType(HrConstants.EvaluationTemplateItemContentType contentType) {
        this.contentType = contentType;
    }
}
