package com.globits.timesheet.repository;

import com.globits.timesheet.domain.LeaveRequest;
import com.globits.timesheet.domain.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, UUID> {
    @Query("select entity from LeaveRequest entity where entity.requestStaff.staffCode = :staffCode")
    List<LeaveRequest> findByStaffCode(@Param("staffCode") String staffCode);


    /*
    /.lf  .f  .lt .t
    / .f .lf    .lt .t
    /  .lf .f  .t .lt
    / .f .lf .t .lt
     */
    @Query("SELECT lr.requestStaff.id FROM LeaveRequest lr where (lr.voided is null or lr.voided = false) " +
            "AND lr.requestStaff.id in :staffIds " +
            "AND lr.approvalStatus = 2 " + //HrConstants.AbsenceRequestApprovalStatus.APPROVED
            "AND (" +
            "(:toDate >=  lr.toDate AND :fromDate <= lr.toDate) " +
            "           OR " +
            "(:toDate <= lr.toDate AND :toDate >= lr.fromDate AND :fromDate <= lr.toDate)" +
            ")")
    List<UUID> findLeaveRequestByDate(@Param("staffIds") List<UUID> staffIds, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    @Query("SELECT lr FROM LeaveRequest lr " +
            " where (lr.voided is null or lr.voided = false)" +
            "and lr.requestStaff.id = :staffId " +
            "AND date(lr.fromDate) <= date(:workingDate) " +
            "and date(lr.toDate) >= date(:workingDate) " +
            "AND lr.approvalStatus = :approvalStatus " +
            "order by lr.requestDate desc "
    )
    List<LeaveRequest> findByStaffIdWorkingDateAndApprovalStatus(@Param("staffId") UUID staffId,
                                                                 @Param("workingDate") Date workingDate,
                                                                 @Param("approvalStatus") Integer approvalStatus);

}
