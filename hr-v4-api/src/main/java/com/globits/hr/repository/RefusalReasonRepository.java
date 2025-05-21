package com.globits.hr.repository;

import com.globits.hr.domain.RefusalReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RefusalReasonRepository extends JpaRepository<RefusalReason, UUID> {

    @Query("select e FROM RefusalReason e where e.code = ?1")
    List<RefusalReason> findByCode(String code);

}
