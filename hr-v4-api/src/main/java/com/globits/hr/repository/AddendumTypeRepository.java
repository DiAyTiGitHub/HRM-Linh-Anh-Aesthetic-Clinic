package com.globits.hr.repository;

import com.globits.hr.domain.AddendumType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AddendumTypeRepository extends JpaRepository<AddendumType, UUID> {

    @Query("select e FROM AddendumType e where e.code = ?1")
    List<AddendumType> findByCode(String code);

}
