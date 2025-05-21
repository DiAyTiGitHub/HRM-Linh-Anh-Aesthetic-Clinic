package com.globits.hr.dto.loginkeycloak;

public class AccessDto {
    private Boolean manageGroupMembership;
    private Boolean view;
    private Boolean mapRoles;
    private Boolean impersonate;
    private Boolean manage;

    public Boolean getManageGroupMembership() {
        return manageGroupMembership;
    }

    public void setManageGroupMembership(Boolean manageGroupMembership) {
        this.manageGroupMembership = manageGroupMembership;
    }

    public Boolean getView() {
        return view;
    }

    public void setView(Boolean view) {
        this.view = view;
    }

    public Boolean getMapRoles() {
        return mapRoles;
    }

    public void setMapRoles(Boolean mapRoles) {
        this.mapRoles = mapRoles;
    }

    public Boolean getImpersonate() {
        return impersonate;
    }

    public void setImpersonate(Boolean impersonate) {
        this.impersonate = impersonate;
    }

    public Boolean getManage() {
        return manage;
    }

    public void setManage(Boolean manage) {
        this.manage = manage;
    }
}
