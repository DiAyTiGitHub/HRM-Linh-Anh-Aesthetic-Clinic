package com.globits.hr.repository;

import com.globits.hr.domain.DisciplineReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DisciplineReasonRepository extends JpaRepository<DisciplineReason, UUID> {

    @Query("select e FROM DisciplineReason e where e.code = ?1")
    List<DisciplineReason> findByCode(String code);

}
