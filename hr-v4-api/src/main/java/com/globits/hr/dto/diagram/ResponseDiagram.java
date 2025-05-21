package com.globits.hr.dto.diagram;

import java.util.List;

public class ResponseDiagram {
    private List<EdgeDto> edges;
    private List<NodeDto> nodes;

    public ResponseDiagram() {
    }

    public ResponseDiagram(List<EdgeDto> edges, List<NodeDto> nodes) {
        this.edges = edges;
        this.nodes = nodes;
    }

    public List<EdgeDto> getEdges() {
        return edges;
    }

    public void setEdges(List<EdgeDto> edges) {
        this.edges = edges;
    }

    public List<NodeDto> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeDto> nodes) {
        this.nodes = nodes;
    }

}
