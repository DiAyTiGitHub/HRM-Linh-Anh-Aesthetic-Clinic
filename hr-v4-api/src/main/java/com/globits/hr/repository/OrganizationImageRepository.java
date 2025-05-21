package com.globits.hr.repository;

import com.globits.hr.domain.OrganizationImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface OrganizationImageRepository extends JpaRepository<OrganizationImage, UUID> {
    @Query("select entity from OrganizationImage entity where entity.organization.id = :organizationId")
    OrganizationImage findByOrganizationId(@Param("organizationId") UUID organizationId);
}
