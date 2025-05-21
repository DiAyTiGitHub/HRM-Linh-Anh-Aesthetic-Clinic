package com.globits.hr.repository;

import com.globits.hr.domain.HrDepartmentIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface HrDepartmentIpRepository extends JpaRepository<HrDepartmentIp, UUID> {

    @Transactional(readOnly = true)
    @Query("SELECT entity FROM HrDepartmentIp entity WHERE entity.department.id IS NOT NULL AND entity.department.id IN :validDepIds")
    List<HrDepartmentIp> getAllValidDepartmentIps(@Param("validDepIds") List<UUID> validDepIds);
}
