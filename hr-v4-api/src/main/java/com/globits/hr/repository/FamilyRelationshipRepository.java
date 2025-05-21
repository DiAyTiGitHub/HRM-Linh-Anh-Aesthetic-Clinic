/*
 * TA va Giang làm
 */

package com.globits.hr.repository;

import com.globits.hr.domain.FamilyRelationship;
import com.globits.hr.dto.FamilyRelationshipDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FamilyRelationshipRepository extends JpaRepository<FamilyRelationship, UUID> {
    @Query("select new com.globits.hr.dto.FamilyRelationshipDto(fr) from FamilyRelationship fr")
    Page<FamilyRelationshipDto> getListPage(Pageable pageable);

    @Query("select new com.globits.hr.dto.FamilyRelationshipDto(fr) from FamilyRelationship fr where fr.name like ?1 or fr.code like ?2")
    Page<FamilyRelationshipDto> searchByPage(String name, String code, Pageable pageable);
    //

    @Query("select entity from FamilyRelationship entity where entity.name =?1")
    List<FamilyRelationship> findByName(String name);

    @Query("select d from FamilyRelationship d where d.code = ?1")
    List<FamilyRelationship> findByCode(String code);

    @Query("select count(entity.id) from FamilyRelationship entity where entity.code =?1 and (entity.id <> ?2 or ?2 is null) ")
    Long checkCode(String code, UUID id);

}
