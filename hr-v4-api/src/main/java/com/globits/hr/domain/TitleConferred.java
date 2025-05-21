package com.globits.hr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;

@Table(name = "tbl_title_confered")
@Entity
public class TitleConferred extends BaseObject {	
	    private static final long serialVersionUID = 1L;
	    @Column(name = "code")
	    private String code;
	    @Column(name = "name")
	    private String name;
	    @Column(name = "description")
	    private String description;
	    @Column(name = "level")
	    private Integer level;

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

	    public Integer getLevel() {
	        return level;
	    }

	    public void setLevel(Integer level) {
	        this.level = level;
	    }
	}


