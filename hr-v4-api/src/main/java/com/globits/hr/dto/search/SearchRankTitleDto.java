package com.globits.hr.dto.search;

import java.util.UUID;

public class SearchRankTitleDto  extends SearchDto{
    protected UUID rankTitleId;
    public UUID getRankTitleId() {
        return rankTitleId;
    }
    public void setRankTitleId(UUID rankTitleId) {
        this.rankTitleId = rankTitleId;
    }
}
