package com.globits.salary.repository;

import com.globits.hr.domain.CivilServantType;
import com.globits.salary.domain.SalaryUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SalaryUnitRepository extends JpaRepository<SalaryUnit, UUID> {
    @Query("select count(entity.id) from SalaryUnit entity where entity.code =?1 and (entity.id <> ?2 or ?2 is null) ")
    Long checkCode(String code, UUID id);

    @Query("select su FROM SalaryUnit su where su.code = ?1")
    List<SalaryUnit> findByCode(String code);
}
