package com.globits.hr.repository;

import com.globits.hr.domain.Bank;
import com.globits.hr.domain.InsurancePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface InsurancePackageRepository extends JpaRepository<InsurancePackage, UUID> {
    @Query("select ip from InsurancePackage ip where ip.code = ?1")
    List<InsurancePackage> findByCode(String code);
}
