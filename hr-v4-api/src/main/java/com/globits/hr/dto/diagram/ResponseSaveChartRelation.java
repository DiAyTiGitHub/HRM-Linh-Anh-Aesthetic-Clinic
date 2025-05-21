package com.globits.hr.dto.diagram;

import com.globits.hr.dto.OrganizationChartDto;
import com.globits.hr.dto.OrganizationChartRelationDto;

import java.util.List;

public class ResponseSaveChartRelation {
    OrganizationChartRelationDto edge;
    List<OrganizationChartRelationDto> edges;


    public ResponseSaveChartRelation() {
    }

    public ResponseSaveChartRelation(OrganizationChartRelationDto node, List<OrganizationChartRelationDto> edges) {
        this.edge = node;
        this.edges = edges;
    }

    public OrganizationChartRelationDto getEdge() {
        return edge;
    }

    public void setEdge(OrganizationChartRelationDto edge) {
        this.edge = edge;
    }

    public List<OrganizationChartRelationDto> getEdges() {
        return edges;
    }

    public void setEdges(List<OrganizationChartRelationDto> edges) {
        this.edges = edges;
    }
}
