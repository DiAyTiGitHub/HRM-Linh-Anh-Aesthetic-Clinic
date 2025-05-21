package com.globits.hr.repository;

import com.globits.hr.domain.Allowance;
import com.globits.hr.domain.StaffSignature;
import com.globits.hr.dto.AllowanceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffSignatureRepository extends JpaRepository<StaffSignature, UUID> {
    @Query("select ss from StaffSignature ss where  ss.code=?1")
    List<StaffSignature> findByCode(String strip);

    @Query("select count(distinct ss.staff.id) from StaffSignature ss")
    Long countDistinctStaffSignature();
}
