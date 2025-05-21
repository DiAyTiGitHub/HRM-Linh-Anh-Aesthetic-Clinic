package com.globits.salary.repository;

import com.globits.salary.domain.SalaryResultItem;
import com.globits.salary.domain.SalaryResultItemGroup;
import com.globits.salary.domain.SalaryTemplateItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SalaryResultItemRepository extends JpaRepository<SalaryResultItem, UUID> {
    @Query("select sri from SalaryResultItem sri " +
            "where sri.salaryResult.id = :salaryResultId " +
            "order by sri.displayOrder, sri.displayName ")
    List<SalaryResultItem> getAllResultItemsOfSalaryResult(@Param("salaryResultId") UUID salaryResultId);

    @Query("select sri from SalaryResultItem sri " +
            "where sri.salaryResult.id = :salaryResultId " +
            "and sri.salaryItem.id = :salaryItemId ")
    List<SalaryResultItem> getBySalaryResultIdAndSalaryItemId(@Param("salaryResultId") UUID salaryResultId, @Param("salaryItemId") UUID salaryItemId);

    @Query("select ri from SalaryResultItem ri " +
            "where ri.salaryResult.id = :salaryResultId " +
            "and ri.copiedTemplateItem.id = :templateItemId")
    List<SalaryResultItem> getBySalaryResultIdAndTemplateItemId(@Param("salaryResultId") UUID salaryResultId, @Param("templateItemId") UUID templateItemId);
}
