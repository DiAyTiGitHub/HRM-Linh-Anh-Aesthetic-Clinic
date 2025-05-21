package com.globits.hr.repository;

import com.globits.hr.domain.HrDocumentItem;
import com.globits.hr.domain.Position;
import com.globits.hr.dto.PositionDto;
import com.globits.hr.dto.StaffDocumentItemDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HrDocumentItemRepository extends JpaRepository<HrDocumentItem, UUID> {
    @Query("select entity from HrDocumentItem entity where entity.code = :code")
    List<HrDocumentItem> findByCode(@Param("code") String code);
}