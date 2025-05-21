package com.globits.hr.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.StaffSalaryProperty;
import com.globits.hr.dto.StaffSalaryPropertyDto;

@Repository
public interface StaffSalaryPropertyRepository extends JpaRepository<StaffSalaryProperty, UUID> {
    @Query("select new com.globits.hr.dto.StaffSalaryPropertyDto(sp) from StaffSalaryProperty sp")
    Page<StaffSalaryPropertyDto> getListPage(Pageable pageable);
}