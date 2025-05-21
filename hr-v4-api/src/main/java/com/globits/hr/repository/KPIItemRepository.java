package com.globits.hr.repository;

import com.globits.hr.domain.KPIItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface KPIItemRepository extends JpaRepository<KPIItem, UUID> {
}
