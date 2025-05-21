package com.globits.timesheet.service;

import java.util.List;
import java.util.UUID;

import com.globits.hr.dto.search.SearchProjectDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.globits.hr.dto.search.SearchDto;
import com.globits.timesheet.domain.Project;
import com.globits.timesheet.dto.ProjectDto;

@Service
public interface ProjectService {
    ProjectDto saveOrUpdate(UUID id, ProjectDto dto);

    Boolean delete(UUID id);

    ProjectDto getProject(UUID id);

    Project getEntityById(UUID id);

    Page<ProjectDto> searchByPage(SearchProjectDto dto);

    Boolean checkCode(UUID id, String code);

    Boolean checkName(UUID id, String name);


}
