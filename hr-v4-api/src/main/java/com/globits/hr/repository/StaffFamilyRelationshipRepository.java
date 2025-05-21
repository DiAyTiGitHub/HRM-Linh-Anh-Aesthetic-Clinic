package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.StaffFamilyRelationship;
import com.globits.hr.dto.StaffFamilyRelationshipDto;

@Repository
public interface StaffFamilyRelationshipRepository extends JpaRepository<StaffFamilyRelationship, UUID> {

    @Query("select new com.globits.hr.dto.StaffFamilyRelationshipDto(family) from StaffFamilyRelationship family")
    Page<StaffFamilyRelationshipDto> getPages(Pageable pageable);

    @Query("select new com.globits.hr.dto.StaffFamilyRelationshipDto(family) from StaffFamilyRelationship family where family.staff.id = ?1")
    List<StaffFamilyRelationshipDto> getAll(UUID id);

    @Query("select new com.globits.hr.dto.StaffFamilyRelationshipDto(family) from StaffFamilyRelationship family where family.id = ?1")
    StaffFamilyRelationshipDto getFamilyById(UUID id);

    @Query("select entity from StaffFamilyRelationship entity where entity.staff.id = :staffId and entity.isDependent = true")
    List<StaffFamilyRelationship> findDependentRelationshipOfStaff(@Param("staffId") UUID staffId);

    @Query("select count(entity) from StaffFamilyRelationship entity where entity.staff.id = :staffId and entity.isDependent = true")
    long countDependentRelationshipOfStaff(@Param("staffId") UUID staffId);


    @Query("select entity from StaffFamilyRelationship entity " +
            "where entity.staff.id = :staffId " +
            "and entity.familyRelationship.id = :relationshipId ")
    List<StaffFamilyRelationship> findByStaffIdAndRelationshipId(@Param("staffId") UUID staffId,
                                                                             @Param("relationshipId") UUID relationshipId);
}
	