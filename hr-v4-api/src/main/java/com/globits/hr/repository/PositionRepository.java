package com.globits.hr.repository;

import com.globits.hr.domain.Position;
import com.globits.hr.dto.PositionDto;

import com.globits.hr.dto.StaffDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PositionRepository extends JpaRepository<Position, UUID> {
    @Query("select entity from Position entity where entity.code = :code")
    public List<Position> findByCode(@Param("code") String code);

    @Query("select entity from Position entity where entity.department.id = :departmentId")
    List<Position> findByDepartmentId(@Param("departmentId") UUID departmentId);

    @Query("select new com.globits.hr.dto.PositionDto(entity) from Position entity where entity.isMain = true and entity.staff.id = :staffId")
    public List<PositionDto> findMainPositionDtoByStaffId(@Param("staffId") UUID staffId);

    @Query("select entity from Position entity where entity.isMain = true and entity.staff.id = :staffId")
    public List<Position> findMainPositionByStaffId(@Param("staffId") UUID staffId);

    @Query("select count(entity) from Position entity where entity.department.id=:departmentId and entity.title.id=:positionTitleId")
    Long countNumberOfPositionInDepartmentWithPositionTitle(@Param("departmentId") UUID departmentId, @Param("positionTitleId") UUID positionTitleId);

    @Query("select entity from Position entity where entity.department.id=:departmentId and entity.title.id=:positionTitleId")
    List<Position> findByDepartmentIdAndPositionTitleId(@Param("departmentId") UUID departmentId, @Param("positionTitleId") UUID positionTitleId);

    @Query("select entity from Position entity where entity.staff.id = :staffId")
    public List<Position> findByStaffId(@Param("staffId") UUID staffId);

    @Query(value = """
            	SELECT
            	    p.staff_id AS staffId,
            	    p.id AS positionId,
            	    p.name AS positionName,
            	    p.code AS positionCode,
            	    dep.name AS departmentName,
            	    dep.code AS departmentCode,
            	    posTitle.name AS positionTitleName,
            	    posTitle.code AS positionTitleCode,
            	    raTitle.name AS rankTitleName,
            	    sup.name AS supervisorName,
            	    sup.code AS supervisorCode,
            	    supStaff.staff_code AS supervisorStaffCode,
            	    supPerson.display_name AS supervisorDisplayName,
            	    posTitleGroup.code AS positionTitleGroupCode,
            	    posTitleGroup.name AS positionTitleGroupName
            	FROM tbl_position p
            	LEFT JOIN tbl_position_relation_ship rel
            	    ON rel.position_id = p.id AND rel.relationship_type = :relationshipType
            	LEFT JOIN tbl_position sup
            	    ON rel.supervisor_id = sup.id
            	LEFT JOIN tbl_staff supStaff
            	    ON sup.staff_id = supStaff.id
            	LEFT JOIN tbl_person supPerson 
            	    ON supPerson.id = supStaff.id
            	LEFT JOIN tbl_department dep 
            	    ON dep.id = p.department_id
            	LEFT JOIN tbl_position_title posTitle 
            		ON posTitle.id = p.title_id
            	LEFT JOIN tbl_position_title posTitleGroup 
            		ON posTitleGroup.id = posTitle.parent_id
            	LEFT JOIN tbl_rank_title raTitle
            	    ON raTitle.id = posTitle.rank_title_id
            	WHERE p.is_main = TRUE AND p.staff_id IS NOT NULL
            """, nativeQuery = true)
    List<Object[]> findAllPositionMainNative(@Param("relationshipType") Integer relationshipType);

    @Query("SELECT new com.globits.hr.dto.StaffDto(rela.position.staff) FROM Position p " +
            "         JOIN  p.isSupervisedRelationships rela " +
            "where p.staff.user.id = :user and rela.relationshipType =3")
    List<StaffDto> getListStaffUnderManager(Long user);

    @Query("SELECT p FROM Position p " +
            "LEFT JOIN FETCH p.department d " +
            "LEFT JOIN FETCH p.staff s " +
            "WHERE (:keyword IS NULL OR " +
            "LOWER(p.name) LIKE LOWER(concat('%', :keyword, '%')) " +
            "OR LOWER(d.name) LIKE LOWER(concat('%', :keyword, '%')) " +
            "OR (s IS NOT NULL AND LOWER(s.displayName) LIKE LOWER(concat('%', :keyword, '%'))))")
    List<Position> findByKeyword(@Param("keyword") String keyword);

    @Query(value = "SELECT code FROM tbl_position WHERE code LIKE CONCAT(:prefix, '_%') ORDER BY CAST(SUBSTRING(code, LENGTH(:prefix) + :zeroPadding) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String findMaxCodeByPrefix(@Param("prefix") String prefix, @Param("zeroPadding") Integer zeroPadding);
}
