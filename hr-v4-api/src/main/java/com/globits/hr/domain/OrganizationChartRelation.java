package com.globits.hr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
/*
 * Học hàm  - học vị (Giáo sư, phó giáo sư, ...)
 */
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.UUID;

import com.globits.core.domain.BaseObject;
import com.globits.core.domain.BaseObjectEx;


@Table(name = "tbl_organization_chart_relation")
@Entity
public class OrganizationChartRelation extends BaseObject {
    private static final long serialVersionUID = 1L;

    @Column(name = "relation_type",nullable = false)
    private Integer relationType;

    @Column(name = "relation_icon")
    private String relationIcon;

    @Column(name = "relation_description")
    private String relationDescription;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_org_id",nullable = false)
    private OrganizationChart sourceOrg;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_org_id",nullable = false)
    private OrganizationChart targetOrg;

	@ManyToOne
	@JoinColumn(name="org_chart_data_id")
	private OrgChartData orgChartData;

	public OrganizationChartRelation() {
		super();
	}

	public OrganizationChartRelation(BaseObject object) {
		super(object);
	}

	public Integer getRelationType() {
		return relationType;
	}
	public void setRelationType(Integer relationType) {
		this.relationType = relationType;
	}
	public String getRelationIcon() {
		return relationIcon;
	}
	public void setRelationIcon(String relationIcon) {
		this.relationIcon = relationIcon;
	}
	public String getRelationDescription() {
		return relationDescription;
	}
	public void setRelationDescription(String relationDescription) {
		this.relationDescription = relationDescription;
	}
	public OrganizationChart getSourceOrg() {
		return sourceOrg;
	}
	public void setSourceOrg(OrganizationChart sourceOrg) {
		this.sourceOrg = sourceOrg;
	}
	public OrganizationChart getTargetOrg() {
		return targetOrg;
	}
	public void setTargetOrg(OrganizationChart targetOrg) {
		this.targetOrg = targetOrg;
	}

	public OrgChartData getOrgChartData() {
		return orgChartData;
	}

	public void setOrgChartData(OrgChartData orgChartData) {
		this.orgChartData = orgChartData;
	}
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		OrganizationChartRelation that = (OrganizationChartRelation) o;
		return Objects.equals(relationType, that.relationType) &&
				Objects.equals(sourceOrg.getId(), that.sourceOrg.getId()) &&
				Objects.equals(targetOrg.getId(), that.targetOrg.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(relationType, sourceOrg, targetOrg);
	}

}
