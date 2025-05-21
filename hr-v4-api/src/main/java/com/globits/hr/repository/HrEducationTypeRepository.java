
package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.HrEducationType;

@Repository
public interface HrEducationTypeRepository extends JpaRepository<HrEducationType, UUID> {
    @Query("select d from HrEducationType d where d.code = ?1")
    List<HrEducationType> findByCode(String code);

    @Query("select count(entity.id) from HrEducationType entity where entity.code =?1 and (entity.id <> ?2 or ?2 is null) ")
    Long checkCode(String code, UUID id);

    @Query("select entity from HrEducationType entity where entity.name =?1")
    List<HrEducationType> findByName(String name);
}
