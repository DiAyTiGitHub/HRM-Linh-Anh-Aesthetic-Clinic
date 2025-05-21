package com.globits.hr.repository;

import com.globits.hr.domain.Allowance;
import com.globits.hr.dto.AllowanceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AllowanceRepository extends JpaRepository<Allowance , UUID> {
    @Query("select entity from Allowance entity where entity.name =?1")
    List<Allowance> findByName(String name);
    @Query("select entity from Allowance entity where entity.code =?1")
    List<Allowance> findByCode(String code);
    @Query("select new com.globits.hr.dto.AllowanceDto(s) from Allowance s")
    Page<AllowanceDto> getListPage(Pageable pageable);
}
