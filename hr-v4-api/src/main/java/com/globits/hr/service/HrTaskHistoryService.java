package com.globits.hr.service;

import com.globits.hr.domain.HrTaskHistory;
import com.globits.hr.dto.HrTaskHistoryDto;
import com.globits.hr.dto.search.SearchTaskHistoryDto;
import com.globits.task.dto.HrTaskDto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface HrTaskHistoryService {
    public HrTaskHistoryDto generateHistoryLog(HrTaskDto oldValues, HrTaskDto newValues, int countSubtaskUpdated, int countSubTaskUnchecked);

    public List<HrTaskHistoryDto> pagingHistoryOfTask(UUID taskId, SearchTaskHistoryDto searchObject);

    public HrTaskHistoryDto getFirstCreatedHistoryOfTask(UUID taskId);

    public HrTaskHistoryDto createHistoryComment(HrTaskDto taskComment);

    public List<HrTaskHistoryDto> getAllHistoryOfTask(UUID taskId);

    public List<HrTaskHistory> findHistoryOfStaffInRangeTime(UUID staffId, Date fromDate, Date toDate);
}
