package com.globits.hr.repository;

import com.globits.hr.domain.DepartmentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DepartmentGroupRepository extends JpaRepository<DepartmentGroup, UUID> {

    @Query("select e FROM DepartmentGroup e where e.shortName = ?1")
    List<DepartmentGroup> findByShortName(String shortName);

}
