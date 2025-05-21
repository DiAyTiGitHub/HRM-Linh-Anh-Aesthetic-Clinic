package com.globits.task.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.globits.task.domain.HrTaskStaff;

import java.util.List;
import java.util.UUID;

@Repository
public interface HrTaskStaffRepository extends JpaRepository<HrTaskStaff, UUID> {
    @Query(value = "SELECT COUNT(u) FROM HrTaskStaff u where u.staff.id = :staffId and u.task.id = :taskId")
    Long countByStaffAndTask(UUID staffId, UUID taskId);
    @Query(value = "SELECT COUNT(u) FROM HrTaskStaff u where u.staff.id = :staffId and u.task.id = :taskId and u.id <> :id")
    Long countByStaffAndTaskUpdate(UUID staffId, UUID taskId, UUID id);

    @Query(value = "SELECT u FROM HrTaskStaff u where u.task.id = :taskId")
    List<HrTaskStaff> getAllByTaskId(UUID taskId);

    @Query(value ="select ts from HrTaskStaff ts where ts.task.id = :taskId and ts.staff.id = :staffId")
    public List<HrTaskStaff> getTaskStaffByTaskAndStaffId(@Param("taskId") UUID taskId, @Param("staffId") UUID staffId);
}
