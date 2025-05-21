package com.globits.task.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.globits.task.domain.HrSubTaskItemStaff;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface HrSubTaskItemStaffRepository extends JpaRepository<HrSubTaskItemStaff, UUID> {
    @Query("select u from HrSubTaskItemStaff u where u.subTaskItem.subTask.task.id = ?1")
    List<HrSubTaskItemStaff> getAllByTask(UUID taskId);

    @Query("select stis from HrSubTaskItemStaff stis where stis.subTaskItem.id = :subTaskItemId and stis.staff.id = :staffId ")
    List<HrSubTaskItemStaff> findByStaffIdAndItemId(@Param("staffId") UUID staffId, @Param("subTaskItemId") UUID subTaskItemId);

    @Transactional
    @Modifying
    @Query("delete from HrSubTaskItemStaff stis where stis.subTaskItem.id = :subTaskItemId")
    List<HrSubTaskItemStaff> deleteAllOldItemStaffs(@Param("subTaskItemId") UUID subTaskItemId);
}
