package com.globits.task.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.globits.task.domain.HrTask;

import java.util.List;
import java.util.UUID;

@Repository
public interface HrTaskRepository extends JpaRepository<HrTask, UUID> {
    @Query("select task from HrTask task where task.project.id = :projectId order by task.createDate desc")
    public List<HrTask> getTasksInProject(@Param("projectId") UUID projectId, Pageable pageable);

    @Query("select count(task.id) from HrTask task where task.project.id = :projectId")
    public Object countTasksInProject(@Param("projectId") UUID projectId);

    @Query("select task from HrTask task where task.activity.id = :projectActivityId order by task.createDate desc")
    public List<HrTask> getByProjectActivity(@Param("projectActivityId") UUID projectActivityId);

}
