package com.globits.task.service;

import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchTaskDto;
import com.globits.task.domain.HrTask;
import com.globits.task.dto.HrTaskDto;
import com.globits.task.dto.KanbanDto;

import com.globits.timesheet.dto.search.SearchTimeSheetDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface HrTaskService {
    Boolean delete(UUID id);

    HrTaskDto createOrUpdate(HrTaskDto dto, UUID id);

    HrTaskDto getById(UUID id);

    HrTask getEntityById(UUID id);

    List<HrTaskDto> searchByList(SearchDto dto);

    KanbanDto updateStatusNew(SearchDto dto);

    Page<KanbanDto> pagingListTask(SearchTaskDto dto);

    List<KanbanDto> getListByLimit(SearchTaskDto dto);

    String getUpcomingTaskCodeInProject(UUID projectId);

    List<KanbanDto> exportExcelTaskByFilter(SearchTaskDto searchTaskDto);

    List<KanbanDto> getByProjectActivity(UUID id);

    List<KanbanDto> getAllTaskCreatedUpdatedInRangeFromToDate(SearchTimeSheetDto searchTimeSheetDto);

}
