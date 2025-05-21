package com.globits.hr.repository;

import com.globits.hr.domain.StaffInsurancePackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StaffInsurancePackageRepository extends JpaRepository<StaffInsurancePackage, UUID> {

}
