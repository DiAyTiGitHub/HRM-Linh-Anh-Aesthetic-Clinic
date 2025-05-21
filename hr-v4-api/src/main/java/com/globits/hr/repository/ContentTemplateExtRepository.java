package com.globits.hr.repository;

import com.globits.template.domain.ContentTemplate;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Primary
public interface ContentTemplateExtRepository extends JpaRepository<ContentTemplate, UUID> {

    @Query("select entity from ContentTemplate entity where entity.code = ?1")
    List<ContentTemplate> findByCode(String code);
}
