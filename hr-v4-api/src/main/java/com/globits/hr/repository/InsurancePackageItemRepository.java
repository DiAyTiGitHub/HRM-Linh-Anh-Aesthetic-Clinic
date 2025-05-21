package com.globits.hr.repository;

import com.globits.hr.domain.InsurancePackageItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InsurancePackageItemRepository extends JpaRepository<InsurancePackageItem, UUID> {

}
