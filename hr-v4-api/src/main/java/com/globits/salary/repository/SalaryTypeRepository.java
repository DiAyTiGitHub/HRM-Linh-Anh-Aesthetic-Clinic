package com.globits.salary.repository;


import com.globits.salary.domain.SalaryType;
import com.globits.salary.domain.SalaryUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SalaryTypeRepository   extends JpaRepository<SalaryType, UUID> {
    @Query("select count(entity.id) from SalaryType entity where entity.name =?1 and (entity.id <> ?2 or ?2 is null) ")
    Long checkCode(String name, UUID id);

    @Query("select su FROM SalaryType su where su.name = ?1")
    List<SalaryType> findByName(String name);
    @Query("select count(st.id) from SalaryType st where st.name = ?1")
    Long countByName(String name);
}
