package com.globits.hr.repository;

import com.globits.hr.domain.AllowancePolicy;
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
public interface AllowancePolicyRepository extends JpaRepository<AllowancePolicy , UUID> {
    @Query("select entity from AllowancePolicy entity where entity.name =?1")
    List<AllowancePolicy> findByName(String name);
    @Query("select entity from AllowancePolicy entity where entity.code =?1")
    List<AllowancePolicy> findByCode(String code);
}
