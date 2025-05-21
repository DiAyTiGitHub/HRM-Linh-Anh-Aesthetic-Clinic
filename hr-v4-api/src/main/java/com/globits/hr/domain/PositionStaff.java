package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.*;

import java.util.Date;

/*
 * Bảng lịch sử chức vụ nhân viên
 */

@Table(name = "tbl_position_staff")
@Entity
public class PositionStaff extends BaseObject {
    private static final long serialVersionUID = 5402903435794913458L;

    @Column(name = "from_date")
    private Date fromDate;

    @Column(name = "to_date")
    private Date toDate;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "position_id")
    private Position position;

    /*
     * Vị trí cho đơn vị cụ thể nào đó - nếu chỉ có vị trí nhưng không thuộc đơn vị nào
     * thì trường này bằng null
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hrdepartment_id")
    private HRDepartment hrDepartment; // phòng ban

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "supervisor_id")
    private Staff supervisor;//người quản lý

    @Column(name = "relationship_type")
    private Integer relationshipType; // HRConstants.RelationshipType

    @Column(name = "main_position")
    private Boolean mainPosition;

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public HRDepartment getHrDepartment() {
        return hrDepartment;
    }

    public void setHrDepartment(HRDepartment hrDepartment) {
        this.hrDepartment = hrDepartment;
    }

    public Staff getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Staff supervisor) {
        this.supervisor = supervisor;
    }

    public Integer getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(Integer relationshipType) {
        this.relationshipType = relationshipType;
    }

    public Boolean getMainPosition() {
        return mainPosition;
    }

    public void setMainPosition(Boolean mainPosition) {
        this.mainPosition = mainPosition;
    }
}
