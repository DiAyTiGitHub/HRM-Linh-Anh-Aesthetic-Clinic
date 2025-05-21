package com.globits.hr.repository;

import com.globits.core.domain.GlobalProperty;
import com.globits.hr.dto.HrGlobalPropertyDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HrGlobalPropertyRepository extends JpaRepository<GlobalProperty, String> {
    @Query("select u from GlobalProperty u where u.property = ?1")
    GlobalProperty findByProperty(String property);

    @Query("select new com.globits.hr.dto.HrGlobalPropertyDto(u) from GlobalProperty u")
    List<HrGlobalPropertyDto> getAll();
}