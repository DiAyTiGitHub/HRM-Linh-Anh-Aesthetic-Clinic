package com.globits.hr.repository;

import com.globits.hr.domain.Position;
import com.globits.hr.domain.RecruitmentRequestItem;
import com.globits.hr.dto.PositionDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecruitmentRequestItemRepository extends JpaRepository<RecruitmentRequestItem, UUID> {
}
