package com.globits.hr.dto.diagram;

import java.util.UUID;

public class EdgeDto {
    private UUID id;
    private String source;
    private String target;
    private String type;

    public EdgeDto(UUID id, String source, String target, String type) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.type = type;
    }

    public EdgeDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
