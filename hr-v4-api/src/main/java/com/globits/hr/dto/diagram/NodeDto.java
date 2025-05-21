package com.globits.hr.dto.diagram;

import java.util.UUID;

public class NodeDto {
    private UUID id;
    private PositionDto position;
    private NodeDataDto data;
    private String type;

    public NodeDto() {
    }

    public NodeDto(UUID id, PositionDto position, NodeDataDto data, String type) {
        this.id = id;
        this.position = position;
        this.data = data;
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public PositionDto getPosition() {
        return position;
    }

    public void setPosition(PositionDto position) {
        this.position = position;
    }

    public NodeDataDto getData() {
        return data;
    }

    public void setData(NodeDataDto data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
