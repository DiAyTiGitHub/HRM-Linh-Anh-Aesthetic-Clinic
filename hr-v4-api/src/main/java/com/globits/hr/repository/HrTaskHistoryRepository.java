package com.globits.hr.repository;

import com.globits.hr.domain.HrTaskHistory;
import com.globits.task.domain.HrTask;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HrTaskHistoryRepository extends JpaRepository<HrTaskHistory, UUID> {
    @Query("select history from HrTaskHistory history where history.task.id = :taskId and history.event like %:firstHistorySign order by history.createDate")
    public List<HrTaskHistory> getFirstCreatedHistoryOfTask(@Param("taskId") UUID taskId, @Param("firstHistorySign") String firstHistorySign);
}
