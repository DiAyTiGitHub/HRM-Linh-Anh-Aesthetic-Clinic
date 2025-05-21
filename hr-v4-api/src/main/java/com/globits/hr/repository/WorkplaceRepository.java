package com.globits.hr.repository;

import com.globits.hr.domain.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkplaceRepository extends JpaRepository<Workplace , UUID> {
    @Query("select entity from Workplace entity where entity.name =?1")
    List<Workplace> findByName(String name);
    @Query("select entity from Workplace entity where entity.code =?1")
    List<Workplace> findByCode(String code);
    
}
