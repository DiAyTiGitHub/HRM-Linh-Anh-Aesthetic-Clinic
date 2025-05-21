package com.globits.hr.repository;

import com.globits.hr.domain.PositionRelationShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PositionRelationShipRepository extends JpaRepository<PositionRelationShip, UUID> {

    @Query("select entity from PositionRelationShip entity where entity.id = :id AND entity.supervisor.id = :supervisorId")
    public Optional<PositionRelationShip> getPositionRelationShipBySupervisor(@Param("id") UUID id, @Param("supervisorId") UUID supervisorId);

    @Query("select entity from PositionRelationShip entity where entity.position.id = :positionId AND entity.supervisor.id = :supervisorId")
    public Optional<PositionRelationShip> getPositionRelationShipBySupervisorAndPosition(@Param("supervisorId") UUID supervisorId, @Param("positionId") UUID positionId);

    @Query("select entity from PositionRelationShip entity where entity.position.id=:positionId and entity.department.id=:departmentId")
    List<PositionRelationShip> findByPositionIdAndDepartmentId(@Param("positionId") UUID positionId, @Param("departmentId") UUID departmentId);

    @Query("select entity from PositionRelationShip entity where entity.supervisor.id=:supervisorId and entity.position.id=:positionId")
    List<PositionRelationShip> findBySupervisorIdAndPositionId(@Param("supervisorId") UUID supervisorId, @Param("positionId") UUID positionId);

    @Query("select entity from PositionRelationShip entity where entity.position.id=:positionId or entity.supervisor.id=:positionId")
    List<PositionRelationShip> findManagerByPositionId(@Param("positionId") UUID positionId);

    @Query("select entity from PositionRelationShip entity where entity.supervisor.id=:supervisorId")
    List<PositionRelationShip> findSubordinatesByManagerId(@Param("supervisorId") UUID supervisorId);

    @Query("select entity from PositionRelationShip entity where entity.department.id=:departmentId")
    List<PositionRelationShip> findByDepartmentId(@Param("departmentId") UUID departmentId);

    @Query("SELECT entity FROM PositionRelationShip entity " +
            "WHERE entity.position.id = :positionId AND entity.relationshipType = :relationshipType ORDER BY entity.createDate DESC")
    List<PositionRelationShip> findByPositionIdAndRelationshipType(@Param("positionId") UUID positionId, @Param("relationshipType") Integer relationshipType);

    @Query("select entity from PositionRelationShip entity " +
            "where entity.position.id = :positionId and entity.supervisor.staff.id = :supervisorPositionStaffId and entity.relationshipType = :relationshipType ")
    List<PositionRelationShip> findByPositionIdSupervisorPositionStaffIdAndRelationshipType(@Param("positionId") UUID positionId,
                                                                                            @Param("supervisorPositionStaffId") UUID supervisorPositionStaffId,
                                                                                            @Param("relationshipType") Integer relationshipType);

}