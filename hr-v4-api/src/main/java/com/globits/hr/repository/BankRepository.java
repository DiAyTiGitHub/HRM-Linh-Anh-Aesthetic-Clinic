package com.globits.hr.repository;

import com.globits.hr.domain.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface BankRepository extends JpaRepository<Bank, UUID> {

    @Query("select b from Bank b where b.code = ?1")
    List<Bank> findByCode(String code);
}
