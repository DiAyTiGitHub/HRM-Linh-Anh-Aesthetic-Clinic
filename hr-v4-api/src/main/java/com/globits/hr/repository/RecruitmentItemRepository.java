package com.globits.hr.repository;

import com.globits.hr.domain.RecruitmentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RecruitmentItemRepository extends JpaRepository<RecruitmentItem, UUID> {
}
