package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Table(name = "tbl_position_relation_ship")
@Entity
public class PositionRelationShip extends BaseObject {
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "position_id")
	private Position position;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "supervisor_id")
	private Position supervisor;
    /*
     * 1. Position quan ly truc tiep phong ban
     * 2. Position quan ly gian tiep phong ban
     * 3. supervisor quan ly truc tiep position
     * 4. supervisor quan ly gian tiep position     
     */
    @Column(name = "relationship_type")
    private Integer relationshipType; // Loại mối quan hệ của các vị trí. HRConstants.PositionRelationshipType

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private HRDepartment department; // phòng ban
    
	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Position getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(Position supervisor) {
		this.supervisor = supervisor;
	}

	public Integer getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(Integer relationshipType) {
		this.relationshipType = relationshipType;
	}

	public HRDepartment getDepartment() {
		return department;
	}

	public void setDepartment(HRDepartment department) {
		this.department = department;
	}
	
	
}
