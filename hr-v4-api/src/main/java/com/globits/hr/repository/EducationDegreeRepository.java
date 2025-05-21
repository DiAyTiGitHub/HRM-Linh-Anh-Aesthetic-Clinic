package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

/*
 * author Giang-Tuan Anh
 */
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.globits.hr.domain.EducationDegree;
import com.globits.hr.dto.EducationDegreeDto;

@Repository
public interface EducationDegreeRepository extends JpaRepository<EducationDegree, UUID> {
    @Query("select new com.globits.hr.dto.EducationDegreeDto(ed) from EducationDegree ed")
    Page<EducationDegreeDto> getListPage(Pageable pageable);

    @Query("select new com.globits.hr.dto.EducationDegreeDto(ed) from EducationDegree ed where ed.name like ?1 or ed.code like ?2")
    Page<EducationDegreeDto> searchByPage(String name, String code, Pageable pageable);

    @Query("select count(entity.id) from EducationDegree entity where entity.code =?2 and (entity.id <> ?1 or ?1 is null )")
    Long checkCode(UUID id, String code);
    
    @Query("select count(entity.id) from EducationDegree entity where entity.name =?2 and (entity.id <> ?1 or ?1 is null )")
    Long checkName(UUID id, String name);

    @Query("select entity from EducationDegree entity where entity.name =?1")
    List<EducationDegree> findByName(String name);

    @Query("select entity from EducationDegree entity where entity.code =?1")
    List<EducationDegree> findByCode(String code);

    @Modifying
    @Transactional
    @Query("delete from EducationDegree e where e.id in ?1")
    Integer deleteByIds(List<UUID> ids);
}
