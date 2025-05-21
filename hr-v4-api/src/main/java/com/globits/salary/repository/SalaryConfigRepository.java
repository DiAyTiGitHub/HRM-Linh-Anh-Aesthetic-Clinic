package com.globits.salary.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.globits.salary.domain.SalaryConfig;
import com.globits.salary.dto.SalaryConfigDto;

@Repository
public interface SalaryConfigRepository extends JpaRepository<SalaryConfig, UUID> {
    @Query("select new com.globits.salary.dto.SalaryConfigDto(sc) from SalaryConfig sc")
    Page<SalaryConfigDto> getListPage(Pageable pageable);

    @Query("select count(entity.id) from SalaryConfig entity where entity.code =?1 and (entity.id <> ?2 or ?2 is null) ")
    Long checkCode(String code, UUID id);
}
