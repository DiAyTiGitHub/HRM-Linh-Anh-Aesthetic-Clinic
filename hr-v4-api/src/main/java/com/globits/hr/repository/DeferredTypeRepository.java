package com.globits.hr.repository;

import com.globits.hr.domain.DeferredType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeferredTypeRepository extends JpaRepository<DeferredType, UUID> {

    @Query("select e FROM DeferredType e where e.code = ?1")
    List<DeferredType> findByCode(String code);

}
