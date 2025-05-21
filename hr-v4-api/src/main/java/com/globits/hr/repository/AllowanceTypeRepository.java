package com.globits.hr.repository;

import com.globits.hr.domain.AllowanceType;
import com.globits.hr.dto.AllowanceTypeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AllowanceTypeRepository extends JpaRepository<AllowanceType , UUID> {
    @Query("select entity from AllowanceType entity where entity.name =?1")
    List<AllowanceType> findByName(String name);
    @Query("select entity from AllowanceType entity where entity.code =?1")
    List<AllowanceType> findByCode(String code);
    @Query("select new com.globits.hr.dto.AllowanceTypeDto(s) from AllowanceType s")
    Page<AllowanceTypeDto> getListPage(Pageable pageable);
}
