package com.globits.salary.repository;

import com.globits.salary.domain.SalaryArea;
import com.globits.salary.domain.SalaryUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SalaryAreaRepository extends JpaRepository<SalaryArea, UUID> {
    @Query("select count(entity.id) from SalaryArea entity where entity.code = :code and (entity.id <> :id or :id is null)")
    Long checkCode(@Param("code") String code, @Param("id") UUID id);

    @Query("select sa FROM SalaryArea sa where sa.code = ?1")
    List<SalaryArea> findByCode(String code);
}
