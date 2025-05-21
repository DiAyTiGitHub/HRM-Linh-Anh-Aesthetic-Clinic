package com.globits.hr.dto.search;

import java.util.UUID;

public class StaffDisciplineHistorySearchDto extends SearchDto {
    private UUID disciplineId;

    public UUID getDisciplineId() {
        return disciplineId;
    }

    public void setDisciplineId(UUID disciplineId) {
        this.disciplineId = disciplineId;
    }
}
