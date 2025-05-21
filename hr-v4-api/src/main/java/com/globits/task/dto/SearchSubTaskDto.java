package com.globits.task.dto;

import java.util.UUID;

public class SearchSubTaskDto {
    private UUID taskId;

    private String name;

    private String code;

    private String description;

    private UUID subTaskId;

    public SearchSubTaskDto() {
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
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

    public UUID getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(UUID subTaskId) {
        this.subTaskId = subTaskId;
    }
}
