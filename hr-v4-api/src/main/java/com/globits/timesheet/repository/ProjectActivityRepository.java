package com.globits.timesheet.repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.globits.timesheet.domain.ProjectActivity;
import com.globits.timesheet.dto.ProjectActivityDto;

@Repository
public interface ProjectActivityRepository extends JpaRepository<ProjectActivity, UUID> {
    @Query("select count(entity.id) from ProjectActivity entity where entity.code =?1 and (entity.id <> ?2 or ?2 is null) ")
    Long checkCode(String code, UUID id);

    @Query("select count(entity.id) from ProjectActivity entity where entity.name =?2 and (entity.id <> ?1 or ?1 is null) ")
    Long checkName(UUID id, String name);

    @Query("select new com.globits.timesheet.dto.ProjectActivityDto(p) from ProjectActivity p")
    Page<ProjectActivityDto> getListPage(Pageable pageable);

    @Query("select new com.globits.timesheet.dto.ProjectActivityDto(entity) from ProjectActivity entity where entity.name =?1")
    List<ProjectActivityDto> findByName(String name);

    @Query("select min(entity.startTime) from ProjectActivity entity WHERE entity.project.id=?1 and entity.parent.id is not null and entity.duration <>0")
    public Date findByMinStartDate(UUID id);

    @Query("select max(entity.endTime) from ProjectActivity entity WHERE entity.project.id=?1 and entity.parent.id is not null and entity.duration <>0")
    public Date findByMaxEndDate(UUID id);

    @Query("select new com.globits.timesheet.dto.ProjectActivityDto(p, true) from ProjectActivity p where p.project.id = ?1 AND p.parent is null order by p.code asc ")
    List<ProjectActivityDto> getListByProjectId(UUID id);

    @Query("select count(p.id) from ProjectActivity p where p.code = ?1 AND p.project.id = ?2 AND p.parent is null ")
    Long checkCodeByParentNull(String code, UUID projectId);

    @Query("select count(p.id) from ProjectActivity p where p.code = ?1 AND p.parent is not null AND p.parent.id = ?2")
    Long checkCodeByParentNotNull(String code, UUID parentId);

    //get list children activity ids by parent acvitity id
    @Query("select distinct pa.id from ProjectActivity pa " +
            "where pa.parent.id = :parentId and (pa.voided is null or pa.voided = false)")
    List<UUID> getListChildrenActivityIdsByParentId(@Param("parentId") UUID parentId);
}
