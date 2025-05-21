package com.globits.hr.repository;

import com.globits.hr.domain.LeavingJobReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeavingJobReasonRepository extends JpaRepository<LeavingJobReason, UUID> {

    @Query("select e FROM LeavingJobReason e where e.code = ?1")
    List<LeavingJobReason> findByCode(String code);

}
