package com.globits.hr.dto;

import java.util.UUID;

public class UserExtRoleDto {
    private boolean roleSuperAdmin = false;
    private boolean roleAdmin = false;
    private boolean roleUser = false;
    private boolean roleHrManager = false;
    private boolean roleRecruitment = false;
    private boolean roleInsuranceManager = false;
    private boolean roleSuperHr=false;
    private UUID staffId;

    public boolean isRoleAdmin() {
        return roleAdmin;
    }

    public void setRoleAdmin(boolean roleAdmin) {
        this.roleAdmin = roleAdmin;
    }

    public boolean isRoleUser() {
        return roleUser;
    }

    public void setRoleUser(boolean roleUser) {
        this.roleUser = roleUser;
    }

    public boolean isRoleHrManager() {
        return roleHrManager;
    }

    public void setRoleHrManager(boolean roleHrManager) {
        this.roleHrManager = roleHrManager;
    }

    public boolean isRoleRecruitment() {
        return roleRecruitment;
    }

    public void setRoleRecruitment(boolean roleRecruitment) {
        this.roleRecruitment = roleRecruitment;
    }

    public boolean isRoleInsuranceManager() {
        return roleInsuranceManager;
    }

    public void setRoleInsuranceManager(boolean roleInsuranceManager) {
        this.roleInsuranceManager = roleInsuranceManager;
    }

    public boolean isRoleSuperAdmin() {
        return roleSuperAdmin;
    }

    public void setRoleSuperAdmin(boolean roleSuperAdmin) {
        this.roleSuperAdmin = roleSuperAdmin;
    }

	public boolean isRoleSuperHr() {
		return roleSuperHr;
	}

	public void setRoleSuperHr(boolean roleSuperHr) {
		this.roleSuperHr = roleSuperHr;
	}

	public UUID getStaffId() {
		return staffId;
	}

	public void setStaffId(UUID staffId) {
		this.staffId = staffId;
	}

}
