package com.globits.hr.repository;

import com.globits.hr.domain.HrDocumentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface HrDocumentTemplateRepository extends JpaRepository<HrDocumentTemplate, UUID> {
    @Query("select h from HrDocumentTemplate h where h.code = ?1")
    List<HrDocumentTemplate> findByCode(String code);
    
    @Query(value = """
    	    SELECT 
			    s.id AS staffId,
			    CASE 
			        WHEN s.document_template_id IS NULL THEN 'Không'
			        ELSE 'Có'
			    END AS hasEmployeeProfile,
			    COALESCE(d.hasA34, NULL) AS hasA34,
			    COALESCE(d.hasCCCD, NULL) AS hasCCCD,
			    COALESCE(d.hasDUT, NULL) AS hasDUT,
			    COALESCE(d.hasSYLL, NULL) AS hasSYLL,
			    COALESCE(d.hasBC, NULL) AS hasBC,
			    COALESCE(d.hasCCLQ, NULL) AS hasCCLQ,
			    COALESCE(d.hasGKSK, NULL) AS hasGKSK,
			    COALESCE(d.hasSHK, NULL) AS hasSHK,
			    COALESCE(d.hasHSK, NULL) AS hasHSK,
			    COALESCE(d.hasPTTCN, NULL) AS hasPTTCN,
			    COALESCE(d.hasCKBMTT, NULL) AS hasCKBMTT,
			    COALESCE(d.hasCKBMTTTN, NULL) AS hasCKBMTTTN,
			    COALESCE(d.hasCKTN, NULL) AS hasCKTN,
			    COALESCE(d.hasHDTV, NULL) AS hasHDTV
			FROM tbl_staff s
			LEFT JOIN (
			    SELECT 
			        staffItem.staff_id,
			        MAX(CASE WHEN item.code = 'A34' THEN 'X' END) AS hasA34,
			        MAX(CASE WHEN item.code = 'CCCD' THEN 'X' END) AS hasCCCD,
			        MAX(CASE WHEN item.code = 'DUT' THEN 'X' END) AS hasDUT,
			        MAX(CASE WHEN item.code = 'SYLL' THEN 'X' END) AS hasSYLL,
			        MAX(CASE WHEN item.code = 'BC' THEN 'X' END) AS hasBC,
			        MAX(CASE WHEN item.code = 'CCLQ' THEN 'X' END) AS hasCCLQ,
			        MAX(CASE WHEN item.code = 'GKSK' THEN 'X' END) AS hasGKSK,
			        MAX(CASE WHEN item.code = 'SHK' THEN 'X' END) AS hasSHK,
			        MAX(CASE WHEN item.code = 'HSK' THEN 'X' END) AS hasHSK,
			        MAX(CASE WHEN item.code = 'PTTCN' THEN 'X' END) AS hasPTTCN,
			        MAX(CASE WHEN item.code = 'CKBMTT' THEN 'X' END) AS hasCKBMTT,
			        MAX(CASE WHEN item.code = 'CKBMTTTN' THEN 'X' END) AS hasCKBMTTTN,
			        MAX(CASE WHEN item.code = 'CKTN' THEN 'X' END) AS hasCKTN,
			        MAX(CASE WHEN item.code = 'HDTV' THEN 'X' END) AS hasHDTV
			    FROM tbl_staff_document_item staffItem
			    INNER JOIN tbl_hr_document_item item 
			        ON staffItem.document_item_id = item.id
			        AND item.code IN (:documentItemCodes)
			    INNER JOIN tbl_hr_document_template template 
			        ON item.document_template_id = template.id
			    WHERE staffItem.is_submitted = TRUE
			    GROUP BY staffItem.staff_id
			) d ON s.id = d.staff_id
    	    """, nativeQuery = true)
    	List<Object[]> findDefaultDocumentTemplateItems(@Param("documentItemCodes") List<String> documentItemCodes);


}
