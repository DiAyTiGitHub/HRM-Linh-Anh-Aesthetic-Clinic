package com.globits.hr.repository;

import com.globits.hr.domain.KPI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KPIRepository extends JpaRepository<KPI, UUID> {
    @Query("select k FROM KPI k where k.code = ?1")
    List<KPI> findByCode(String code);
}
