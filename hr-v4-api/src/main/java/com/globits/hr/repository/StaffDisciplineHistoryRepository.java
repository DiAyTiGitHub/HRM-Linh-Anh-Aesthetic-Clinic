package com.globits.hr.repository;

import com.globits.hr.domain.StaffDisciplineHistory;
import com.globits.timesheet.domain.AbsenceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffDisciplineHistoryRepository extends JpaRepository<StaffDisciplineHistory, UUID> {
//	@Query("Select entity from AbsenceRequest entity " +
//			" JOIN entity.workSchedule workSchedule " +
//            " where workSchedule.staff.id = :staffId " +
//			" and workSchedule.shiftWork.id = :shiftWorkId " +
//            " and entity.absenceType = :absenceType" +
//            " and entity.approvalStatus = :approvalStatus")
//	List<AbsenceRequest> getListAbsenceRequest(
//            @Param("staffId") UUID staffId, @Param("shiftWorkId") UUID shiftWorkId,
//            @Param("absenceType") Integer absenceType, @Param("approvalStatus") Integer approvalStatus);
//
//	@Query("Select entity from AbsenceRequest entity " +
//			" where entity.workSchedule.id = :scheduleId ")
//	List<AbsenceRequest> getByScheduleId(
//			@Param("scheduleId") UUID scheduleId);
}
