package com.globits.salary.repository;

import com.globits.salary.domain.StaffAdvancePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffAdvancePaymentRepository extends JpaRepository<StaffAdvancePayment, UUID> {
    @Query("select entity from StaffAdvancePayment entity " +
            "where entity.staff.id = :staffId " +
            "and entity.salaryPeriod.id = :salaryPeriodId " +
            "and entity.approvalStatus = :approvalStatus")
    List<StaffAdvancePayment> findByStaffIdAndSalaryPeriodIdAndApprovalStatus(@Param("staffId") UUID staffId,
                                                                              @Param("salaryPeriodId") UUID salaryPeriodId,
                                                                              @Param("approvalStatus") Integer approvalStatus);
    
    @Query("select COALESCE(SUM(entity.advancedAmount), 0) from StaffAdvancePayment entity " +
            "where entity.staff.id = :staffId " +
            "and entity.salaryPeriod.id = :salaryPeriodId " +
            "and entity.approvalStatus = :approvalStatus")
	Double getTotalAdvancedAmountByStaffAndSalaryPeriod(@Param("staffId") UUID staffId,
											            @Param("salaryPeriodId") UUID salaryPeriodId,
											            @Param("approvalStatus") Integer approvalStatus);
	
}
