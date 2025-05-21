package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import com.globits.hr.dto.StaffDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.PositionStaff;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.PositionStaffDto;

@Repository
public interface PositionStaffRepository extends JpaRepository<PositionStaff, UUID> {
    @Query("select new com.globits.hr.dto.PositionStaffDto(u) from PositionStaff u where (u.hrDepartment.id=?1)")
    Page<PositionStaffDto> findTeacherByDepartment(UUID departmentId, Pageable pageable);

    @Query("select u from PositionStaff u  where u.staff.id=?1 and u.position.id = ?2 and u.hrDepartment.id = ?3")
    List<PositionStaff> findBy(UUID staffId, UUID positionId, UUID departmentId);

    @Query("select distinct(u.staff) from PositionStaff u where u.hrDepartment.id in ?1")
    List<Staff> findDistinctStaffByDepartment(List<UUID> departmentIds);

    @Query("select u from PositionStaff u where u.staff.id=?1")
    List<PositionStaff> findPositionStaffByStaff(UUID staffId);

    @Query("select ps from PositionStaff ps where  ps.hrDepartment.id=?1")
    List<PositionStaff> findByDepartmentId(UUID departmentId);

    @Query("select ps from PositionStaff ps where ps.staff.id=?1 or ps.supervisor.id=?1 or ps.hrDepartment.id=?1")
    List<PositionStaff> findByObjectId(UUID objectId);

    @Query("select ps from PositionStaff ps where ps.staff.id=?1 ")
    List<PositionStaff> findByStaffId(UUID objectId);
    @Query("select ps from PositionStaff ps where ps.supervisor.id=?1")
    List<PositionStaff> findBySupervisorId(UUID objectId);
    @Query("select ps from PositionStaff ps where ps.hrDepartment.id=?1")
    List<PositionStaff> findByHrDepartmentId(UUID objectId);

    @Query("""
    SELECT ps FROM PositionStaff ps 
    WHERE (:staffId IS NULL OR ps.staff.id = :staffId) 
      AND (:supervisorId IS NULL OR ps.supervisor.id = :supervisorId) 
      AND (:departmentId IS NULL OR ps.hrDepartment.id = :departmentId)
""")
    List<PositionStaff> findByRelation(@Param("staffId") UUID staffId,
                                       @Param("supervisorId") UUID supervisorId,
                                       @Param("departmentId") UUID departmentId);


}
