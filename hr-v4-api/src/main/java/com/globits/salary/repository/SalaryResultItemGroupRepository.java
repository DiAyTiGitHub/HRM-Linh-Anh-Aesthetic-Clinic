package com.globits.salary.repository;

import com.globits.salary.domain.SalaryResultItemGroup;
import com.globits.salary.domain.SalaryTemplateItemGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SalaryResultItemGroupRepository extends JpaRepository<SalaryResultItemGroup, UUID> {
    @Query("select rig from SalaryResultItemGroup rig " +
            "where rig.salaryResult.id = :salaryResultId " +
            "and rig.copiedTemplateItemGroup.id = :templateItemGroupId")
    List<SalaryResultItemGroup> getBySalaryResultIdAndTemplateItemGroupId(@Param("salaryResultId") UUID salaryResultId,
                                                                          @Param("templateItemGroupId") UUID templateItemGroupId);
}
