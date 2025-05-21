package com.globits.hr.repository;

import com.globits.hr.domain.HrResourcePlan;
import com.globits.hr.domain.HrResourcePlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HrResourcePlanItemRepository extends JpaRepository<HrResourcePlanItem, UUID> {
}
