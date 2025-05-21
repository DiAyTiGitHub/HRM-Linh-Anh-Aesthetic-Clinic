package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.hr.utils.Const;
import jakarta.persistence.*;

@Entity
@Table(name = "tbl_staff_evaluation")
public class StaffEvaluation extends BaseObject {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private EvaluationItem item;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id")
    private EvaluationForm form;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "self_evaluate")
    private Const.EVALUATION selfEvaluate;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "management_evaluate")
    private Const.EVALUATION managementEvaluate;
    public StaffEvaluation() {
    }

    public EvaluationItem getItem() {
        return item;
    }

    public void setItem(EvaluationItem item) {
        this.item = item;
    }

    public EvaluationForm getForm() {
        return form;
    }

    public void setForm(EvaluationForm form) {
        this.form = form;
    }

    public Const.EVALUATION getSelfEvaluate() {
        return selfEvaluate;
    }

    public void setSelfEvaluate(Const.EVALUATION selfEvaluate) {
        this.selfEvaluate = selfEvaluate;
    }

    public Const.EVALUATION getManagementEvaluate() {
        return managementEvaluate;
    }

    public void setManagementEvaluate(Const.EVALUATION managementEvaluate) {
        this.managementEvaluate = managementEvaluate;
    }
}
