package com.globits.hr.dto;

import java.util.List;

public class ExistingCandidatesDto {
    private List<CandidateDto> listStaff;
    private Boolean status=false;


    public List<CandidateDto> getListStaff() {
        return listStaff;
    }

    public void setListStaff(List<CandidateDto> listStaff) {
        this.listStaff = listStaff;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
