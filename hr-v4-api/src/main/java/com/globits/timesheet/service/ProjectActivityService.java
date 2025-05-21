package com.globits.timesheet.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.globits.hr.dto.search.ProjectActivitySearchDto;
import com.globits.timesheet.domain.ProjectActivity;
import com.globits.timesheet.dto.ProjectActivityDto;

public interface ProjectActivityService {
    ProjectActivityDto saveOrUpdate(UUID id, ProjectActivityDto dto);

    Boolean delete(UUID id);

    Boolean voidProjectActivity(UUID id);

    ProjectActivityDto getProjectActivity(UUID id);

    ProjectActivity getEntityById(UUID id);

    Page<ProjectActivityDto> searchByPage(ProjectActivitySearchDto dto);

    Boolean checkCode(UUID id, String code);

    Boolean checkName(UUID id, String name);

    List<ProjectActivityDto> getList(ProjectActivitySearchDto dto);

    Boolean exportProject(UUID id, jakarta.servlet.http.HttpServletResponse response);

    String getCreate(String projectId, String parentId);

    Set<UUID> getChildrenActivityIdsByParentActivityId(UUID parentId);
}
