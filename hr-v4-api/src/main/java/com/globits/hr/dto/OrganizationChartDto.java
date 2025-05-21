package com.globits.hr.dto;

import java.util.UUID;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.OrganizationChart;
import jakarta.persistence.Column;

public class OrganizationChartDto extends BaseObjectDto {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private UUID objectId; // id của thuc thẻ sẽ position

    private Integer orgType;

    private String name;

	private String title;

    private String code;

    private String description;

    private String orgIcon;

    private String orgShape;
    
    private UUID orgChartDataId;

	private Double x;

	private Double y;

	private Integer level;

	private Boolean highlight = false;

	public OrganizationChartDto() {
		// TODO Auto-generated constructor stub
	}
	public OrganizationChartDto(OrganizationChart entity) {
		super(entity);
		this.objectId = entity.getObjectId();
		this.orgType=entity.getOrgType();
		this.name=entity.getName();
		this.title=entity.getTitle();
		this.code=entity.getCode();
		this.description=entity.getDescription();
		this.orgIcon=entity.getOrgIcon();
		this.orgShape=entity.getOrgShape();
		this.x=entity.getX();
		this.y=entity.getY();
		this.highlight = entity.getHighlight();
		if (entity.getOrgChartData() != null) {
			this.orgChartDataId = entity.getOrgChartData().getId();
		}
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
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
	public UUID getOrgChartDataId() {
		return orgChartDataId;
	}
	public void setOrgChartDataId(UUID orgChartDataId) {
		this.orgChartDataId = orgChartDataId;
	}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

	public Boolean getHighlight() {
		return highlight;
	}

	public void setHighlight(Boolean highlight) {
		this.highlight = highlight;
	}
}
