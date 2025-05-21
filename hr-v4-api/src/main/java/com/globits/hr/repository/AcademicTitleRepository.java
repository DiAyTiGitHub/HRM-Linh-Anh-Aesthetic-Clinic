/*
 * TA va Giang làm
 */

package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.AcademicTitle;
import com.globits.hr.dto.AcademicTitleDto;

@Repository
public interface AcademicTitleRepository extends JpaRepository<AcademicTitle, UUID> {
    @Query("select new com.globits.hr.dto.AcademicTitleDto(s) from AcademicTitle s")
    Page<AcademicTitleDto> getListPage(Pageable pageable);

    @Query("select p from AcademicTitle p where p.code = ?1")
    List<AcademicTitle> findByCode(String code);

    @Query("select count(entity.id) from AcademicTitle entity where entity.code =?1 and (entity.id <> ?2 or ?2 is null)")
    Long checkCode(String code, UUID id);

    @Query("select entity from AcademicTitle entity where entity.name =?1")
    List<AcademicTitle> findByName(String name);
}
