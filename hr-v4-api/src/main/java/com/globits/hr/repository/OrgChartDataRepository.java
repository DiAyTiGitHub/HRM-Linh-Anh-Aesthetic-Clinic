package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.Candidate;
import com.globits.hr.domain.OrgChartData;

@Repository
public interface OrgChartDataRepository extends JpaRepository<OrgChartData, UUID> {
}
