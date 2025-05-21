package com.globits.hr.repository;

import com.globits.hr.domain.RankTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RankTitleRepository extends JpaRepository<RankTitle, UUID> {

    @Query("select e FROM RankTitle e where e.shortName = ?1")
    List<RankTitle> findByShortName(String shortName);

    @Query(value = "SELECT code FROM tbl_rank_title WHERE code LIKE CONCAT(:prefix, '_%') ORDER BY CAST(SUBSTRING(shortName, LENGTH(:prefix) + :zeroPadding) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String findMaxCodeByPrefix(@Param("prefix") String prefix, @Param("zeroPadding") Integer zeroPadding);
}
