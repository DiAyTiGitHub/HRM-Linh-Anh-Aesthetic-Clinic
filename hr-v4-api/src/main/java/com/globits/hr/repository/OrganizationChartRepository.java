/*
 * TA va Giang làm
 */

package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.OrganizationChart;

@Repository
public interface OrganizationChartRepository extends JpaRepository<OrganizationChart, UUID> {
	@Query("select entity from OrganizationChart entity "
			+ "where (entity.voided is null or entity.voided = false) and entity.code = :code ")
	OrganizationChart findByCode(String code);

	@Query("select entity from OrganizationChart entity "
			+ "where (entity.voided is null or entity.voided = false) "
			+ "and entity.code = ?1 "
			+ "and entity.orgChartData.id = ?2")
	List<OrganizationChart> findByCodeAndOrgChartDataId(String code, UUID orgChartDataId);

	@Query("select entity from OrganizationChart entity "
			+ "where (entity.voided is null or entity.voided = false) "
			+ "and entity.objectId = ?1 "
			+ "and entity.orgChartData.id = ?2")
	List<OrganizationChart> findByObjectIdAndOrgChartDataId(UUID id, UUID orgChartDataId);
}
