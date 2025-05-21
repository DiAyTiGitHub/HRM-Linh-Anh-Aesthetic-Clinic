package com.globits.hr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
/*
 * Học hàm  - học vị (Giáo sư, phó giáo sư, ...)
 */
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.sql.Types;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.globits.core.domain.BaseObject;
import com.globits.core.domain.BaseObjectEx;


@Table(name = "tbl_organization_chart")
@Entity
public class OrganizationChart extends BaseObject {
    private static final long serialVersionUID = 1L;
    /*
     * Id cua Object tuong ung
     */
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "object_id")
    private UUID objectId;
    /*
     * Staff : 0
     * Department: 1
     * Organization: 2
     * Position: 3
     */
    @Column(name = "org_type")
    private Integer orgType;
	@Column(name = "name")
	private String name;
	@Column(name = "title")
	private String title;
    @Column(name = "code")
    private String code;
    @Column(name = "description")
    private String description;
    @Column(name = "org_icon")
    private String orgIcon;
    @Column(name = "org_shape")
    private String orgShape;
    
    @ManyToOne
    @JoinColumn(name="org_chart_data_id")
    private OrgChartData orgChartData;
    
    @Column(name = "x")
    private Double x; 
    
    @Column(name = "y")
    private Double y;

	private Boolean highlight = false;
    
	public UUID getObjectId() {
		return objectId;
	}
	public void setObjectId(UUID objectId) {
		this.objectId = objectId;
	}
	public Integer getOrgType() {
		return orgType;
	}
	public void setOrgType(Integer orgType) {
		this.orgType = orgType;
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
	public String getOrgIcon() {
		return orgIcon;
	}
	public void setOrgIcon(String orgIcon) {
		this.orgIcon = orgIcon;
	}
	public String getOrgShape() {
		return orgShape;
	}
	public void setOrgShape(String orgShape) {
		this.orgShape = orgShape;
	}
	public OrgChartData getOrgChartData() {
		return orgChartData;
	}
	public void setOrgChartData(OrgChartData orgChartData) {
		this.orgChartData = orgChartData;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	public Boolean getHighlight() {
		return highlight;
	}

	public void setHighlight(Boolean highlight) {
		this.highlight = highlight;
	}

	// Ghi đè equals
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		OrganizationChart that = (OrganizationChart) o;
		return Objects.equals(objectId, that.objectId);
	}

	// Ghi đè hashCode
	@Override
	public int hashCode() {
		return Objects.hash(objectId);
	}
}
