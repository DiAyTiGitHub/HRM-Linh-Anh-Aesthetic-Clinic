package com.globits.hr.domain;

import java.util.HashSet;
import java.util.Set;

import com.globits.core.domain.BaseObject;

import jakarta.persistence.*;

@Table(name = "tbl_Org_chart_data")
@Entity
public class OrgChartData extends BaseObject {

    private String name;
    private String code;


    @Column(name = "base_64_edges", length = 100000)
    private String base64Edges;

    @Column(name = "base_64_nodes", length = 100000)
    private String base64Nodes;

    @OneToMany(mappedBy = "orgChartData", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrganizationChart> nodes;

    @OneToMany(mappedBy = "orgChartData", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrganizationChartRelation> edges = new HashSet<>();

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

    public Set<OrganizationChart> getNodes() {
        return nodes;
    }

    public void setNodes(Set<OrganizationChart> nodes) {
        this.nodes = nodes;
    }

    public Set<OrganizationChartRelation> getEdges() {
        return edges;
    }

    public void setEdges(Set<OrganizationChartRelation> edges) {
        this.edges = edges;
    }
}
