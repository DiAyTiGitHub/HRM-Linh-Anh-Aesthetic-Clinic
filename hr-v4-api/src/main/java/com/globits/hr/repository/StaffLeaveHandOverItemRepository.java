package com.globits.hr.repository;

import com.globits.hr.domain.StaffLeaveHandOverItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StaffLeaveHandOverItemRepository extends JpaRepository<StaffLeaveHandOverItem, UUID> {

}
