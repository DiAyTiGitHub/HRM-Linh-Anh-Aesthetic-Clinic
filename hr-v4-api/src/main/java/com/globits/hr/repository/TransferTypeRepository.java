package com.globits.hr.repository;

import com.globits.hr.domain.TransferType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransferTypeRepository extends JpaRepository<TransferType, UUID> {

    @Query("select e FROM TransferType e where e.code = ?1")
    List<TransferType> findByCode(String code);

}
