package com.globits.hr.dto.diagram;

import java.util.UUID;

public class NodeDataDto {
    private UUID id;
    private String label;
    private String title;

    public NodeDataDto() {
    }

    public NodeDataDto(UUID id, String label, String title) {
        this.id = id;
        this.label = label;
        this.title = title;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
