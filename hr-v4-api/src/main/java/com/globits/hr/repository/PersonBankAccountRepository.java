package com.globits.hr.repository;

import com.globits.hr.domain.PersonBankAccount;
import com.globits.hr.domain.RankTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PersonBankAccountRepository extends JpaRepository<PersonBankAccount, UUID> {

    @Query("select pbc FROM PersonBankAccount pbc where pbc.person.id = :personId")
    List<PersonBankAccount> findByPersonId(@Param("personId") UUID personId);

    @Query("select pbc FROM PersonBankAccount pbc " +
            "where pbc.person.id = :personId and pbc.bank.id = :bankId ")
    List<PersonBankAccount> findByPersonIdAndBankId(@Param("personId") UUID personId, @Param("bankId") UUID bankId);

    @Query("select pbc FROM PersonBankAccount pbc " +
            "where pbc.person.id = :personId and pbc.isMain = true")
    List<PersonBankAccount> findMainByPersonId(@Param("personId") UUID personId);


}
