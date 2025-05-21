package com.globits.timesheet.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.globits.timesheet.domain.Project;
import com.globits.timesheet.dto.ProjectDto;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
	@Query("select count(entity.id) from Project entity where entity.code =?1 and (entity.id <> ?2 or ?2 is null) ")
	Long checkCode(String code, UUID id);
	@Query("select count(entity.id) from Project entity where entity.name =?1 and (entity.id <> ?2 or ?2 is null) ")
	Long checkName(String name, UUID id);
	@Query("select new com.globits.timesheet.dto.ProjectDto(p) from Project p")
	Page<ProjectDto> getListPage( Pageable pageable);
	@Query("select new com.globits.timesheet.dto.ProjectDto(entity) from Project entity where entity.name =?1")
	List<ProjectDto> findByName(String name);
}
