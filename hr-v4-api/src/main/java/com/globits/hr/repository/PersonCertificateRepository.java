package com.globits.hr.repository;

import com.globits.hr.domain.PersonCertificate;
import com.globits.hr.dto.PersonCertificateDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PersonCertificateRepository extends JpaRepository<PersonCertificate, UUID> {

    @Query("select new com.globits.hr.dto.PersonCertificateDto(pc) from PersonCertificate pc where pc.person.id = :personId")
    List<PersonCertificateDto> findPersonCertificatesByPersonId(@Param("personId") UUID personId);

}
