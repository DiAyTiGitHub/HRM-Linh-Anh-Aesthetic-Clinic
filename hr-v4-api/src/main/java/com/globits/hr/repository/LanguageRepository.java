package com.globits.hr.repository;

import com.globits.hr.domain.Language;
import com.globits.hr.dto.LanguageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LanguageRepository extends JpaRepository<Language, UUID> {
    @Query("select new com.globits.hr.dto.LanguageDto(o) from Language o")
    Page<LanguageDto>getListPage(Pageable pageable);
    @Query("select o from  Language o where o.code =?1 ")
    List<Language> findByCode(String code);
    @Query("select count (entity.id) from Language entity where entity.code = ?1 and (entity.id <> ?2 or ?2 is null)")
    Long checkCode(String code, UUID id);
    @Query("select entity from Language entity where entity.name = ?1")
    List<Language>findByName(String name);
}
