package com.globits.timesheet.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.globits.timesheet.domain.ProjectStaff;

@Repository
public interface ProjectStaffRepository extends JpaRepository<ProjectStaff, UUID> {

}
