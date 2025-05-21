package com.globits.hr.repository;

import com.globits.hr.domain.Certificate;
import com.globits.hr.domain.StaffDocumentItem;
import com.globits.hr.dto.CertificateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface StaffDocumentItemRepository extends JpaRepository<StaffDocumentItem, UUID> {
    @Query("select entity from StaffDocumentItem entity " +
            "where entity.staff.id = :staffId " +
            "and entity.documentItem.id = :documentItemId")
    List<StaffDocumentItem> findByStaffIdAndDocumentItemId(@Param("staffId") UUID staffId, @Param("documentItemId") UUID documentItemId);

    @Query("SELECT entity FROM StaffDocumentItem entity WHERE entity.staff.id = ?1 ORDER BY entity.createDate DESC")
    List<StaffDocumentItem> findByStaffId(UUID id);
}
