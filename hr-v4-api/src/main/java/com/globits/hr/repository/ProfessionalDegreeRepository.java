package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.globits.hr.domain.ProfessionalDegree;
import com.globits.hr.dto.ProfessionalDegreeDto;

public interface ProfessionalDegreeRepository extends JpaRepository<ProfessionalDegree, UUID> {
    @Query("select new com.globits.hr.dto.ProfessionalDegreeDto(s) from ProfessionalDegree s")
    Page<ProfessionalDegreeDto> getListPage(Pageable pageable);

    @Query("select p from ProfessionalDegree p where p.code = ?1")
    List<ProfessionalDegree> findByCode(String code);

    @Query("select count(entity.id) from ProfessionalDegree entity where entity.code =?1 and (entity.id <> ?2 or ?2 is null) ")
    Long checkCode(String code, UUID id);
}
