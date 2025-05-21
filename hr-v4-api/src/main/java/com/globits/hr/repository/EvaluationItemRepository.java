package com.globits.hr.repository;

import com.globits.hr.domain.EvaluationItem;
import com.globits.hr.dto.EvaluationItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EvaluationItemRepository extends JpaRepository<EvaluationItem, UUID> {
    @Query("SELECT NEW com.globits.hr.dto.EvaluationItemDto(item) FROM EvaluationItem item " +
            "WHERE (item.voided IS NULL OR item.voided = false) " +
            "AND (:keyword IS NULL OR LOWER(item.name) LIKE LOWER(CONCAT('%', :keyword, '%')))" +
            "AND (:removeId is null  OR item.id != :removeId) ")
    Page<EvaluationItemDto> paging(String keyword, UUID removeId, Pageable pageable);

    @Query(value = "SELECT code FROM tbl_evaluation_item WHERE code LIKE CONCAT(:prefix, '_%') ORDER BY CAST(SUBSTRING(code, LENGTH(:prefix) + :zeroPadding) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String findMaxCodeByPrefix(@Param("prefix") String prefix, @Param("zeroPadding") Integer zeroPadding);
}
