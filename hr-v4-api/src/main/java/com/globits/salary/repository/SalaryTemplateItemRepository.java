package com.globits.salary.repository;

import com.globits.salary.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SalaryTemplateItemRepository extends JpaRepository<SalaryTemplateItem, UUID> {
    @Query("select sti from SalaryTemplateItem sti " +
            "where sti.salaryTemplate.id = :salaryTemplateId " +
            "order by sti.displayOrder, sti.displayName ")
    List<SalaryTemplateItem> getAllTemplateItemsOfTemplate(@Param("salaryTemplateId") UUID salaryTemplateId);

    @Query("select sti from SalaryTemplateItem sti " +
            "where sti.salaryTemplate.id = :salaryTemplateId " +
            "and sti.salaryItem.id = :salaryItemId ")
    List<SalaryTemplateItem> getBySalaryTemplateIdAndSalaryItemId(@Param("salaryTemplateId") UUID salaryTemplateId, @Param("salaryItemId") UUID salaryItemId);

    @Query("select si from SalaryItem si where si.code = ?1")
    List<SalaryItem> findByCode(String code);

    @Query("select sti from SalaryTemplateItem sti " +
            "where sti.salaryTemplate.id = :salaryTemplateId " +
            "and trim(sti.code) = :salaryItemCode")
    List<SalaryTemplateItem> findSalaryTemplateItemByTemplateIdAndTemplateItemCode(
            @Param("salaryTemplateId") UUID salaryTemplateId,
            @Param("salaryItemCode") String salaryItemCode);



}
