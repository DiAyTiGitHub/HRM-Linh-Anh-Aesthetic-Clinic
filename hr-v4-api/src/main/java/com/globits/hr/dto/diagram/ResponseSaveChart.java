package com.globits.hr.dto.diagram;

import com.globits.hr.domain.OrganizationChart;
import com.globits.hr.domain.OrganizationChartRelation;
import com.globits.hr.dto.OrganizationChartDto;
import com.globits.hr.dto.OrganizationChartRelationDto;

import java.util.ArrayList;
import java.util.List;

public class ResponseSaveChart {
    OrganizationChartDto node;
    List<OrganizationChartRelationDto> edges;


    public ResponseSaveChart() {
    }

    public ResponseSaveChart(OrganizationChartDto node, List<OrganizationChartRelationDto> edges) {
        this.node = node;
        this.edges = edges;
    }

    public OrganizationChartDto getNode() {
        return node;
    }

    public void setNode(OrganizationChartDto node) {
        this.node = node;
    }

    public List<OrganizationChartRelationDto> getEdges() {
        return edges;
    }

    public void setEdges(List<OrganizationChartRelationDto> edges) {
        this.edges = edges;
    }
}
