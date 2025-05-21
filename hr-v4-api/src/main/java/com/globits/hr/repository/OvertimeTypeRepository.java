/*
 * Created by TA & Giang on 22/4/2018.
 */

package com.globits.hr.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.OvertimeType;
import com.globits.hr.dto.OvertimeTypeDto;

@Repository
public interface OvertimeTypeRepository extends JpaRepository<OvertimeType, UUID> {
    @Query("select new com.globits.hr.dto.OvertimeTypeDto(s) from OvertimeType s")
    Page<OvertimeTypeDto> getListPage(Pageable pageable);

    @Query("select count(entity.id) from OvertimeType entity where entity.code =?1 and (entity.id <> ?2 or ?2 is null) ")
    Long checkCode(String code, UUID id);
}
