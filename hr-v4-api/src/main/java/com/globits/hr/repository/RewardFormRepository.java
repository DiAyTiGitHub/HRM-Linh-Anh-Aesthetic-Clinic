package com.globits.hr.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.globits.hr.domain.RewardForm;

public interface RewardFormRepository extends JpaRepository<RewardForm, UUID> {

    @Query("select c FROM RewardForm c where c.name = ?1 ")
    List<RewardForm> findByName(String name);

    @Query("select count(entity.id) from RewardForm entity where entity.code =?2 and (entity.id <> ?1 or ?1 is null )")
    Long checkCode(UUID id, String code);

    @Query("select c FROM RewardForm c where c.code = ?1")
    List<RewardForm> findByCode(String code);
}
