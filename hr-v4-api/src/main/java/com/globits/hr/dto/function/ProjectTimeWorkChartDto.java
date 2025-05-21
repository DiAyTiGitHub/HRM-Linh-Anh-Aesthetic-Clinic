package com.globits.hr.dto.function;

import java.util.List;

public class ProjectTimeWorkChartDto {
    private List<String> horizontal;
    private List<ProjectTimeWorkTableDto> projectTimeWork;

    public ProjectTimeWorkChartDto(List<String> horizontal, List<ProjectTimeWorkTableDto> projectTimeWork) {
        this.horizontal = horizontal;
        this.projectTimeWork = projectTimeWork;
    }

    public List<String> getHorizontal() {
        return horizontal;
    }

    public void setHorizontal(List<String> horizontal) {
        this.horizontal = horizontal;
    }

    public List<ProjectTimeWorkTableDto> getProjectTimeWork() {
        return projectTimeWork;
    }

    public void setProjectTimeWork(List<ProjectTimeWorkTableDto> projectTimeWork) {
        this.projectTimeWork = projectTimeWork;
    }
}
