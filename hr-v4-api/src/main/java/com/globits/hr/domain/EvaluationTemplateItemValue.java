package com.globits.hr.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.globits.core.domain.BaseObject;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_evaluation_template_item_value")
public class EvaluationTemplateItemValue extends BaseObject {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_template_item_id")
    private EvaluationTemplateItem evaluationTemplateItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_candidate_round_id")
    private EvaluationCandidateRound evaluationCandidateRound;

    private Integer value;

    private String generalValue;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private EvaluationTemplateItemValue parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvaluationTemplateItemValue> children = new ArrayList<>();

    public EvaluationTemplateItemValue() {
    }

    public EvaluationTemplateItemValue getParent() {
        return parent;
    }

    public void setParent(EvaluationTemplateItemValue parent) {
        this.parent = parent;
    }

    public List<EvaluationTemplateItemValue> getChildren() {
        return children;
    }

    public void setChildren(List<EvaluationTemplateItemValue> children) {
        this.children = children;
    }

    public String getGeneralValue() {
        return generalValue;
    }

    public void setGeneralValue(String generalValue) {
        this.generalValue = generalValue;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public EvaluationCandidateRound getEvaluationCandidateRound() {
        return evaluationCandidateRound;
    }

    public void setEvaluationCandidateRound(EvaluationCandidateRound evaluationCandidateRound) {
        this.evaluationCandidateRound = evaluationCandidateRound;
    }

    public EvaluationTemplateItem getEvaluationTemplateItem() {
        return evaluationTemplateItem;
    }

    public void setEvaluationTemplateItem(EvaluationTemplateItem evaluationTemplateItem) {
        this.evaluationTemplateItem = evaluationTemplateItem;
    }
}
