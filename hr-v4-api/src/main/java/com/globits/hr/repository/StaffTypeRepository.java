package com.globits.hr.repository;

import com.globits.hr.domain.StaffType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffTypeRepository extends JpaRepository<StaffType, UUID> {

    @Query("select e FROM StaffType e where e.code = ?1")
    List<StaffType> findByCode(String code);

}
