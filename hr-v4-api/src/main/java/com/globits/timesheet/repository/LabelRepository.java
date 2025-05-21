package com.globits.timesheet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.globits.timesheet.domain.Label;
import com.globits.timesheet.dto.LabelDto;

import java.util.List;
import java.util.UUID;

@Repository
public interface LabelRepository extends JpaRepository<Label, UUID> {
    @Query("SELECT new com.globits.timesheet.dto.LabelDto(entity) from Label entity where entity.id = ?1")
    LabelDto getLabelDtoById(UUID id);

    @Query("select count(entity.id) from Label entity where entity.color =?1 and (entity.id <> ?2 or ?2 is null) ")
    Long checkCode(String code, UUID id);

    @Query("SELECT new com.globits.timesheet.dto.LabelDto(entity) from Label entity")
    List<LabelDto> getAllLabel();

    @Query("SELECT new com.globits.timesheet.dto.LabelDto(entity) from Label entity where entity.project.id = ?1")
    List<LabelDto> getAllLabelByProjectId(UUID id);
}
