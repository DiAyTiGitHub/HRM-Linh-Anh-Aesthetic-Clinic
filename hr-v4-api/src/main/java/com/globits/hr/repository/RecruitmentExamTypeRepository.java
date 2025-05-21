package com.globits.hr.repository;

import com.globits.hr.domain.RecruitmentExamType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecruitmentExamTypeRepository extends JpaRepository<RecruitmentExamType, UUID> {

    @Query("select e from RecruitmentExamType e where e.code=?1")
    List<RecruitmentExamType> findByCode(String code);

}
