package com.globits.timesheet.domain;

import java.util.Date;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;

@Table(name = "tbl_project_activity")
@Entity
public class ProjectActivity extends BaseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7727111968196270579L;

	@ManyToOne
	@JoinColumn(name="project_id")
	private Project project;
	
	@Column(name = "code")
	private String code;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "description", columnDefinition = "TEXT")
	private String description;
	
	@ManyToOne
	@JoinColumn(name="parent_id")
	private ProjectActivity parent;

	@Column(name = "start_time")
	private Date startTime;

	@Column(name = "end_time")
	private Date endTime;

	@Column(name = "duration")
	private Double duration;

	@Column(name = "estimate_duration")
	private Double estimateDuration;
	
	@OneToMany(mappedBy="parent",fetch = FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval = true)
	private Set<ProjectActivity> child;
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ProjectActivity getParent() {
		return parent;
	}

	public void setParent(ProjectActivity parent) {
		this.parent = parent;
	}

	public Set<ProjectActivity> getChild() {
		return child;
	}

	public void setChild(Set<ProjectActivity> child) {
		this.child = child;
	}


	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Double getDuration() {
		return duration;
	}

	public void setDuration(Double duration) {
		this.duration = duration;
	}

	public Double getEstimateDuration() {
		return estimateDuration;
	}

	public void setEstimateDuration(Double estimateDuration) {
		this.estimateDuration = estimateDuration;
	}

	
}
