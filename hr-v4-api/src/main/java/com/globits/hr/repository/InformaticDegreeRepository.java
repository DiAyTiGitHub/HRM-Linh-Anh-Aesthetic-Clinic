package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import com.globits.hr.domain.InformaticDegree;
import com.globits.hr.dto.InformaticDegreeDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InformaticDegreeRepository extends JpaRepository<InformaticDegree, UUID> {
    @Query("select new com.globits.hr.dto.InformaticDegreeDto(s) from InformaticDegree s")
    Page<InformaticDegreeDto> getListPage(Pageable pageable);

    @Query("select p from InformaticDegree p where p.code = ?1")
    List<InformaticDegree> findByCode(String code);

    @Query("select count(p.id) from InformaticDegree p where p.code = ?1 and (p.id <> ?2 or ?2 is null) ")
    Long countByCode(String code, UUID id);

    @Query("select count(entity.id) from InformaticDegree entity where entity.code =?1 and (entity.id <> ?2 or ?2 is null) ")
    Long checkCode(String code, UUID id);
}
