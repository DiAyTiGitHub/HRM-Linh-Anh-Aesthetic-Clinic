package com.globits.hr.dto.search;

import java.util.List;
import java.util.UUID;

public class EvaluationCandidateRoundSearchDto extends SearchDto{
    private UUID roundId;

    public UUID getRoundId() {
        return roundId;
    }

    public void setRoundId(UUID roundId) {
        this.roundId = roundId;
    }
}
