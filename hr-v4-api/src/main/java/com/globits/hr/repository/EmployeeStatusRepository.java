package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.EmployeeStatus;
import com.globits.hr.dto.EmployeeStatusDto;

@Repository
public interface EmployeeStatusRepository extends JpaRepository<EmployeeStatus, UUID> {
    @Query("select new com.globits.hr.dto.EmployeeStatusDto(s) from EmployeeStatus s")
    Page<EmployeeStatusDto> getListPage(Pageable pageable);

    @Query("select c FROM EmployeeStatus c where c.name = ?1 ")
    List<EmployeeStatus> findByName(String name);

    @Query("select count(entity.id) from EmployeeStatus entity where entity.code =?2 and (entity.id <> ?1 or ?1 is null )")
    Long checkCode(UUID id, String code);

    @Query("select c FROM EmployeeStatus c where c.code = ?1")
    List<EmployeeStatus> findByCode(String code);

    @Query("select new com.globits.hr.dto.EmployeeStatusDto(c) FROM EmployeeStatus c where c.name = ?1 ")
    List<EmployeeStatusDto> getByName(String name);

    @Query("select new com.globits.hr.dto.EmployeeStatusDto(c) FROM EmployeeStatus c ")
    List<EmployeeStatusDto> getAllEmployeeStatus();

    @Query("delete from EmployeeStatus e where e.id in ?1")
    @Modifying
    @Transactional
    void deleteByIds(List<UUID> ids);
}
