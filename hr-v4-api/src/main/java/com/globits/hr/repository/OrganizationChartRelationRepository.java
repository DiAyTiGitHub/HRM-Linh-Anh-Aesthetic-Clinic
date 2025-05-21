/*
 * TA va Giang làm
 */

package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.AcademicTitle;
import com.globits.hr.domain.OrganizationChart;
import com.globits.hr.domain.OrganizationChartRelation;
import com.globits.hr.dto.AcademicTitleDto;
import com.globits.hr.dto.OrganizationChartRelationDto;

@Repository
public interface OrganizationChartRelationRepository extends JpaRepository<OrganizationChartRelation, UUID> {
	@Query(" SELECT ocr "
			+ "FROM OrganizationChartRelation ocr WHERE ocr.sourceOrg.id=?1 ")
	public List<OrganizationChartRelation> findChildRelation(UUID sourceId);
	
	@Query(" SELECT ocr "
			+ "FROM OrganizationChartRelation ocr WHERE ocr.targetOrg.id=?1 ")
	public List<OrganizationChartRelation> findParentRelation(UUID targetId);
	
	@Modifying
    @Query("DELETE FROM OrganizationChartRelation o WHERE o.sourceOrg.id = :orgChartId or o.targetOrg.id = :orgChartId")
	public void deleteByOrgChartId(@Param("orgChartId") UUID orgChartId);
}
