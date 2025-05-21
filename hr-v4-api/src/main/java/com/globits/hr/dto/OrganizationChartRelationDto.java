package com.globits.hr.dto;

import java.util.UUID;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.OrganizationChartRelation;

public class OrganizationChartRelationDto extends BaseObjectDto {

	// 0 = truc tiep; 1 = gian tiep
	private Integer relationType;
    private String relationIcon;
    private String relationDescription;
    
    private OrganizationChartDto sourceOrg;
    private UUID sourceOrgId;
    private OrganizationChartDto targetOrg;
    private UUID targetOrgId;
    
	public OrganizationChartRelationDto() {
		// TODO Auto-generated constructor stub
	}
	
	public OrganizationChartRelationDto(OrganizationChartRelation entity ) {
		super(entity);
		if(entity!=null) {
			this.relationType=entity.getRelationType();
			this.relationIcon=entity.getRelationIcon();
			this.relationDescription=entity.getRelationDescription();
		    if(entity.getSourceOrg()!=null) {
		    	this.sourceOrg = new OrganizationChartDto(entity.getSourceOrg());
		    	this.sourceOrgId=entity.getSourceOrg().getId();
		    }
		    if(entity.getTargetOrg()!=null) {
		    	this.targetOrg = new OrganizationChartDto(entity.getTargetOrg());
				this.targetOrgId = entity.getTargetOrg().getId();
		    }
		}
	}
	
	public OrganizationChartRelationDto(OrganizationChartRelation entity,boolean simple ) {
		super(entity);
		if(entity!=null) {
			this.relationType=entity.getRelationType();
			this.relationIcon=entity.getRelationIcon();
			this.relationDescription=entity.getRelationDescription();
		    if(entity.getSourceOrg()!=null) {
		    	if(simple) {
		    		this.sourceOrg = new OrganizationChartDto(entity.getSourceOrg());
		    	}		    	
		    	this.sourceOrgId=entity.getSourceOrg().getId();
		    }
		    if(entity.getTargetOrg()!=null) {
		    	if(simple) {
		    		this.targetOrg = new OrganizationChartDto(entity.getTargetOrg());
		    	}		    	
				this.targetOrgId = entity.getTargetOrg().getId();
		    }
		}
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

	public OrganizationChartDto getSourceOrg() {
		return sourceOrg;
	}

	public void setSourceOrg(OrganizationChartDto sourceOrg) {
		this.sourceOrg = sourceOrg;
	}

	public UUID getSourceOrgId() {
		return sourceOrgId;
	}

	public void setSourceOrgId(UUID sourceOrgId) {
		this.sourceOrgId = sourceOrgId;
	}

	public OrganizationChartDto getTargetOrg() {
		return targetOrg;
	}

	public void setTargetOrg(OrganizationChartDto targetOrg) {
		this.targetOrg = targetOrg;
	}

	public UUID getTargetOrgId() {
		return targetOrgId;
	}

	public void setTargetOrgId(UUID targetOrgId) {
		this.targetOrgId = targetOrgId;
	}

}
