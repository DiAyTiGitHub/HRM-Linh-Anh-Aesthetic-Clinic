package com.globits.timesheet.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.timesheet.domain.Label;

public class LabelDto extends BaseObjectDto {
    private String name;
    private String color;
    private ProjectDto project;

    public LabelDto() {
    }

    public LabelDto(Label entity){
        if (entity!=null){
            this.id = entity.getId();
            this.name = entity.getName();
            this.color = entity.getColor();
            if (entity.getProject()!=null){
                this.project = new ProjectDto(entity.getProject(),true);
            }
        }
    }
    public LabelDto(Label entity, boolean check){
        if (entity!=null){
            this.id = entity.getId();
            this.name = entity.getName();
            this.color = entity.getColor();
            if (entity.getProject()!=null && check){
                this.project = new ProjectDto(entity.getProject(),true);
            }
        }
    }

    public Label toEntity(LabelDto dto, Label entity) {
        entity.setId(dto.getId());
        entity.setColor(dto.getColor());
        entity.setName(dto.getName());
        return entity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public ProjectDto getProject() {
        return project;
    }

    public void setProject(ProjectDto project) {
        this.project = project;
    }
}
