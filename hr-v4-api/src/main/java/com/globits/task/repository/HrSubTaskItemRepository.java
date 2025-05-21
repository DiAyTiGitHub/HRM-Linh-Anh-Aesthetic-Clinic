package com.globits.task.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.globits.task.domain.HrSubTaskItem;

import java.util.List;
import java.util.UUID;

@Repository
public interface HrSubTaskItemRepository extends JpaRepository<HrSubTaskItem, UUID> {
    @Query("select u from HrSubTaskItem u where u.subTask.task.id = ?1")
    List<HrSubTaskItem> findAllByTask(UUID taskId);
}
