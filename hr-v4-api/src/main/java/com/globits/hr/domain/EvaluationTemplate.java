package com.globits.hr.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.globits.core.domain.BaseObject;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "tbl_evaluation_template")
public class EvaluationTemplate extends BaseObject {
    @Column(name = "name")
    private String name;
    @Column(name = "code")
    private String code;
    @Column(name = "description")
    private String description;
    @OneToMany(mappedBy = "template", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<EvaluationTemplateItem> evaluationTemplateItems;

    public EvaluationTemplate() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<EvaluationTemplateItem> getEvaluationTemplateItems() {
        return evaluationTemplateItems;
    }

    public void setEvaluationTemplateItems(Set<EvaluationTemplateItem> evaluationTemplateItems) {
        this.evaluationTemplateItems = evaluationTemplateItems;
    }
}
