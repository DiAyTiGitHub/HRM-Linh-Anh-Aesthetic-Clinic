package com.globits.hr.repository;

import com.globits.hr.domain.Position;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffWorkingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface StaffWorkingHistoryRepository extends JpaRepository<StaffWorkingHistory, UUID> {
//    @Query("select u from StaffWorkingHistory u " +
//            " where u.staff.id=?1 and u.position.id = ?2 and u.department.id = ?3")
//    List<StaffWorkingHistory> findBy(UUID staffId, UUID positionId, UUID departmentId);
//
//    @Query("select distinct(u.staff) from StaffWorkingHistory u " +
//            "where u.department.id in ?1")
//    List<Staff> findDistinctStaffByDepartment(List<UUID> departmentIds);
//
//    @Query("select u from StaffWorkingHistory u where u.staff.id=?1")
//    List<StaffWorkingHistory> findStaffWorkingHistoryByStaff(UUID staffId);

    @Query("SELECT swh FROM StaffWorkingHistory swh WHERE swh.staff.id = ?1 ORDER BY swh.createDate DESC")
    List<StaffWorkingHistory> findByStaffIdOrderByCreateDateDesc(UUID staffId);

    @Query(value = """
            SELECT
                    swh.staff_id AS staffId,
                    swh.start_date AS startDate,
                    swh.end_date AS endDate,
                    swh.note AS reason
                FROM tbl_staff_working_history swh
                INNER JOIN (
                    SELECT
                        staff_id,
                        MAX(start_date) AS max_start_date
                    FROM tbl_staff_working_history
                    WHERE (voided IS NULL OR voided = FALSE)
                      AND transfer_type = :transferType
                    GROUP BY staff_id
                ) latest ON swh.staff_id = latest.staff_id
                        AND swh.start_date = latest.max_start_date
                WHERE (swh.voided IS NULL OR swh.voided = FALSE)
                  AND swh.staff_id IS NOT NULL
                  AND swh.transfer_type = :transferType
            """, nativeQuery = true)
    List<Object[]> findLatestStaffWorkingHistory(Integer transferType);


    @Query("SELECT swh FROM StaffWorkingHistory swh " +
            "WHERE swh.staff.id = :staffId " +
            "and date(swh.startDate) = date(:startDate) " +
            "and swh.transferType = :transferType " +
            "ORDER BY swh.createDate DESC ")
    List<StaffWorkingHistory> findByTransferTypeStartDateOfStaff(@Param("transferType") Integer transferType,
                                                                           @Param("startDate") Date startDate,
                                                                           @Param("staffId") UUID staffId);

    @Query("SELECT swh FROM StaffWorkingHistory swh " +
            "WHERE swh.staff.id = :staffId " +
            "and date(swh.startDate) = date(:startDate) " +
            "and swh.fromPosition.id = :fromPositionId " +
            "and swh.transferType = :transferType " +
            "ORDER BY swh.createDate DESC ")
    List<StaffWorkingHistory> findByTransferTypeStartDateFromPositionIdOfStaff(@Param("transferType") Integer transferType,
                                                                               @Param("startDate") Date startDate,
                                                                               @Param("fromPositionId") UUID fromPositionId,
                                                                               @Param("staffId") UUID staffId);

    @Query("SELECT swh FROM StaffWorkingHistory swh " +
            "WHERE swh.staff.id = :staffId " +
            "AND swh.toPosition.id = :positionId " +
            "AND swh.endDate IS NULL " +
            "ORDER BY swh.createDate DESC")
    List<StaffWorkingHistory> findByStaffAndToPositionAndEndDateIsNull(
            @Param("staffId") UUID staffId,
            @Param("positionId") UUID positionId);
}
