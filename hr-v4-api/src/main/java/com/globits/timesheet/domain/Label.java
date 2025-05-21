package com.globits.timesheet.domain;

import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


@Table(name = "tbl_label")
@Entity
public class Label extends BaseObject{

    @ManyToOne
	@JoinColumn(name="project_id")
	private Project project;

    @Column(name = "name")
    private String name;

    @Column(name = "color")
    private String color;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String code) {
        this.color = code;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
