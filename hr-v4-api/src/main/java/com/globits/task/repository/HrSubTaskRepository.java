package com.globits.task.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.globits.task.domain.HrSubTask;

import java.util.List;
import java.util.UUID;

@Repository
public interface HrSubTaskRepository extends JpaRepository<HrSubTask, UUID> {
    @Query("select u from HrSubTask u where u.task.id = ?1")
    List<HrSubTask> getAllByTask(UUID taskId);
}
