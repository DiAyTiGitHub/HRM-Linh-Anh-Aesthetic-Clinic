package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;

// vị trí tuyển trong đợt tuyển dụng
@Table(name = "tbl_recruitment_item")
@Entity
public class RecruitmentItem extends BaseObject {
    private static final long serialVersionUID = 991992518344617174L;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recruitment_id")
    private Recruitment recruitment; // thuộc đợt tuyển dụng nào

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_title_id")
    private PositionTitle positionTitle; // Chức danh cần tuyển dụng

    @Column(name = "quantity")
    private Integer quantity; // Số lượng tuyển


    public Recruitment getRecruitment() {
        return recruitment;
    }

    public void setRecruitment(Recruitment recruitment) {
        this.recruitment = recruitment;
    }

    public PositionTitle getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(PositionTitle positionTitle) {
        this.positionTitle = positionTitle;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

