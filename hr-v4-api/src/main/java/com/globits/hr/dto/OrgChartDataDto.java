package com.globits.hr.dto;

import com.globits.core.domain.BaseObject;
import com.globits.hr.domain.OrgChartData;
import com.globits.hr.domain.OrganizationChart;
import com.globits.hr.domain.OrganizationChartRelation;
import jakarta.persistence.OneToMany;

import java.util.HashSet;
import java.util.Set;

public class OrgChartDataDto extends BaseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String code;
	
	private String base64Edges;
	private String base64Nodes;
	private Set<OrganizationChartDto> nodes;
	private Set<OrganizationChartRelationDto> edges;


	public OrgChartDataDto() {

	}

	public OrgChartDataDto(OrgChartData entity) {
		super(entity);
		this.code = entity.getCode();
		this.name = entity.getName();
		this.base64Edges = entity.getBase64Edges();
		this.base64Nodes = entity.getBase64Nodes();

		if(entity.getNodes() != null && !entity.getNodes().isEmpty()) {
			this.nodes = new HashSet<>();
			for(OrganizationChart node : entity.getNodes()) {
				this.nodes.add(new OrganizationChartDto(node));
			}
		}
		if(entity.getNodes() != null && !entity.getEdges().isEmpty()) {
			this.edges = new HashSet<>();
			for(OrganizationChartRelation relation : entity.getEdges()) {
				this.edges.add(new OrganizationChartRelationDto(relation));
			}
		}
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
	public String getBase64Edges() {
		return base64Edges;
	}
	public void setBase64Edges(String base64Edges) {
		this.base64Edges = base64Edges;
	}
	public String getBase64Nodes() {
		return base64Nodes;
	}
	public void setBase64Nodes(String base64Nodes) {
		this.base64Nodes = base64Nodes;
	}

	public Set<OrganizationChartDto> getNodes() {
		return nodes;
	}

	public void setNodes(Set<OrganizationChartDto> nodes) {
		this.nodes = nodes;
	}

	public Set<OrganizationChartRelationDto> getEdges() {
		return edges;
	}

	public void setEdges(Set<OrganizationChartRelationDto> edges) {
		this.edges = edges;
	}

}
