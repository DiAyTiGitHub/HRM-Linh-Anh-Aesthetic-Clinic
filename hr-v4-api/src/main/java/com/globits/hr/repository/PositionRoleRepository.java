package com.globits.hr.repository;

import com.globits.hr.domain.PositionRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PositionRoleRepository extends JpaRepository<PositionRole, UUID> {

    @Query("select e FROM PositionRole e where e.shortName = ?1")
    List<PositionRole> findByShortName(String shortName);

}
