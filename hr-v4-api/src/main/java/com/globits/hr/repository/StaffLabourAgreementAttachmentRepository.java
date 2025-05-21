package com.globits.hr.repository;

import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffLabourAgreement;
import com.globits.hr.domain.StaffLabourAgreementAttachment;
import com.globits.hr.dto.StaffLabourAgreementDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffLabourAgreementAttachmentRepository extends JpaRepository<StaffLabourAgreementAttachment, UUID> {
    @Query("select slaa from StaffLabourAgreementAttachment slaa " +
            "where slaa.file.id = :fileId and slaa.staffLabourAgreement.id = :agreementId")
    List<StaffLabourAgreementAttachment> findByFileIdAndAgreementId(@Param("fileId") UUID fileId, @Param("agreementId") UUID agreementId);
}
