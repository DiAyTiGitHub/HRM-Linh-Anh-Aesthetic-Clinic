package com.globits.hr.domain;
import com.globits.core.domain.BaseObject;
import com.globits.hr.utils.Const;
import jakarta.persistence.*;
//đây là bảng phân quyền cho thằng nào nhìn thấy y/c tuyển dụng
@Entity
@Table(name = "tbl_recruitment_position")
public class RecruitmentRequestPosition extends BaseObject {
    @ManyToOne
    @JoinColumn(name = "recruitment_id")
    private RecruitmentRequest recruitment;
    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    public RecruitmentRequest getRecruitment() {
        return recruitment;
    }

    public void setRecruitment(RecruitmentRequest recruitment) {
        this.recruitment = recruitment;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public RecruitmentRequestPosition() {
    }
}
