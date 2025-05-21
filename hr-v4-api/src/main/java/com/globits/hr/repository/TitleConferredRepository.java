package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.globits.hr.domain.TitleConferred;
import com.globits.hr.dto.TitleConferredDto;
@Repository
public interface TitleConferredRepository extends JpaRepository<TitleConferred, UUID> {
	 @Query("select new com.globits.hr.dto.TitleConferredDto(s) from TitleConferred s")
	    Page<TitleConferredDto> getListPage(Pageable pageable);

	    @Query("select p from AcademicTitle p where p.code = ?1")
	    List<TitleConferred> findByCode(String code);

	    @Query("select count(entity.id) from TitleConferred entity where entity.code =?1 and (entity.id <> ?2 or ?2 is null) ")
	    Long checkCode(String code, UUID id);

	    @Query("select entity from TitleConferred entity where entity.name =?1")
	    List<TitleConferred> findByName(String name);
}
