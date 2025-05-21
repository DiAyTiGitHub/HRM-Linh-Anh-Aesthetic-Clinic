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

import com.globits.hr.domain.LabourAgreementType;
import com.globits.hr.dto.LabourAgreementTypeDto;

@Repository
public interface LabourAgreementTypeRepository extends JpaRepository<LabourAgreementType, UUID> {
    @Query("select new com.globits.hr.dto.LabourAgreementTypeDto(labour) from LabourAgreementType labour")
    Page<LabourAgreementTypeDto> getListPage(Pageable pageable);

    @Query("select new com.globits.hr.dto.LabourAgreementTypeDto(labour) from LabourAgreementType labour where labour.name like ?1 or labour.code like ?2")
    Page<LabourAgreementTypeDto> searchByPage(String name, String code, Pageable pageable);

    @Query("select c FROM LabourAgreementType c where c.code = ?1")
    List<LabourAgreementType> findByCode(String code);

    @Query("select entity from LabourAgreementType entity where entity.code =?1")
    LabourAgreementType checkCode(String code);

    @Query("select entity from LabourAgreementType entity where entity.name =?1")
    List<LabourAgreementType> findByName(String name);

    @Query("select new com.globits.hr.dto.LabourAgreementTypeDto(labour) from LabourAgreementType labour")
    List<LabourAgreementTypeDto> getAllLabourAgreementType();

    @Query("delete from LabourAgreementType l where l.id in ?1")
    @Modifying
    @Transactional
    void deleteByIds(List<UUID> ids);
}

