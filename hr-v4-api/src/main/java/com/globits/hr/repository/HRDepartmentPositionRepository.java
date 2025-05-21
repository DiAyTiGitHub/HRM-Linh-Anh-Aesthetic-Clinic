package com.globits.hr.repository;

import com.globits.hr.domain.HRDepartmentPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HRDepartmentPositionRepository extends JpaRepository<HRDepartmentPosition, UUID> {
    @Query("Select entity from HRDepartmentPosition entity " +
            "where entity.department.id = :idDepartment and entity.positionTitle.id = :idPositionTitle")
    List<HRDepartmentPosition> findByDepartmentIdAndPositionTitleId(
            @Param("idDepartment") UUID idDepartment, @Param("idPositionTitle") UUID idPositionTitle);

    @Query("Select entity from HRDepartmentPosition entity " +
            "where entity.department.code = :codeDepartment and entity.positionTitle.id = :idPositionTitle")
    List<HRDepartmentPosition> findByDepartmentCodeAndPositionTitleId(
            @Param("codeDepartment") String codeDepartment, @Param("idPositionTitle") UUID idPositionTitle);

    @Query("SELECT entity FROM HRDepartmentPosition entity WHERE entity.department.id = :idDepartment AND entity.positionTitle.code = :codePositionTitle")
    List<HRDepartmentPosition> findByDepartmentIdAndPositionTitleCode(
            @Param("idDepartment") UUID idDepartment,
            @Param("codePositionTitle") String codePositionTitle
    );
}
