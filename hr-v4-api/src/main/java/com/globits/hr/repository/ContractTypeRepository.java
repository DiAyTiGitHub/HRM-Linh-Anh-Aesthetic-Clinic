package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.globits.hr.domain.ContractType;
import com.globits.hr.dto.ContractTypeDto;

public interface ContractTypeRepository extends JpaRepository<ContractType, UUID> {
    @Query("select c FROM ContractType c where c.name = ?1 ")
    List<ContractType> findByName(String name);

    @Query("select count(entity.id) from ContractType entity where entity.code =?2 and (entity.id <> ?1 or ?1 is null )")
    Long checkCode(UUID id, String code);

    @Query("select c FROM ContractType c where c.code = ?1")
    List<ContractType> findByCode(String code);

    @Query("select new com.globits.hr.dto.ContractTypeDto(c) FROM ContractType c WHERE c.voided is null or c.voided = false ")
    List<ContractTypeDto> getAllContractType();
}
