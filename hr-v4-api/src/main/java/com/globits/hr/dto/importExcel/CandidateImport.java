package com.globits.hr.dto.importExcel;

import com.globits.hr.dto.CandidateDto;

import java.util.List;

public class CandidateImport {
    private List<CandidateDto> candidates;
    private List<String> errors;

    public CandidateImport() {
    }

    public List<CandidateDto> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<CandidateDto> candidates) {
        this.candidates = candidates;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
