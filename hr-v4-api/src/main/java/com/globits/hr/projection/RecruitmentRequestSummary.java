package com.globits.hr.projection;

import java.util.Date;

public interface RecruitmentRequestSummary {
    Date getStartDate();

    Date getEndDate();

    String getName();

    Long getTotalCandidates();

    Long getCandidatesWithResultStatus();

    Long getCandidatesSentOfferMail();
}
