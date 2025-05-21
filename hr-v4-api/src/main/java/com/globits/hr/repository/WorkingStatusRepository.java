package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.WorkingStatus;
import com.globits.hr.dto.WorkingStatusDto;

@Repository
public interface WorkingStatusRepository extends JpaRepository<WorkingStatus, UUID> {
    @Query("select new com.globits.hr.dto.WorkingStatusDto(status) from WorkingStatus status")
    Page<WorkingStatusDto> getListPage(Pageable pageable);

    @Query("select count(entity.id) from WorkingStatus entity where entity.code =?1 and (entity.id <> ?2 or ?2 is null) ")
    Long checkCode(String code, UUID id);

    @Query("select new com.globits.hr.dto.WorkingStatusDto(status) from WorkingStatus status where status.code = ?1")
    WorkingStatusDto findByCode(String code);

    @Query("select status from WorkingStatus status where status.code = :code order by status.createDate desc")
    List<WorkingStatus> findEntityByCode(@Param("code") String code);

    @Query("select new com.globits.hr.dto.WorkingStatusDto(status) from WorkingStatus status where status.statusValue is not null")
    List<WorkingStatusDto> getListStatusIsNotNull();
}
