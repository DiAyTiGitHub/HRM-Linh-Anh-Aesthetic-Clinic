package com.globits.hr.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.CommonKeyCode;

@Repository
public interface CommonKeyCodeRepository extends JpaRepository<CommonKeyCode, UUID> {
	@Query("from CommonKeyCode a where a.type=?1 AND a.objectId = ?2 ")
	CommonKeyCode getByTypeAndObjectId(Integer type, UUID objectId);
	@Query("from CommonKeyCode a where a.type=?1 AND a.objectId IS NULL ")
	CommonKeyCode getByType(Integer type);

}
