package com.globits.hr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;

@Table(name = "tbl_state_management_Level")
@Entity	
public class StateManagementLevel extends BaseObject {
	    private static final long serialVersionUID = 1L;
	    @Column(name = "code")
	    private String code;
	    @Column(name = "name")
	    private String name;
	    @Column(name = "level")
		private String level;
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

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
}
