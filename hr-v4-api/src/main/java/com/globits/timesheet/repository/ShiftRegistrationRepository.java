package com.globits.timesheet.repository;

import com.globits.timesheet.domain.Journal;
import com.globits.timesheet.domain.ShiftRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShiftRegistrationRepository extends JpaRepository<ShiftRegistration, UUID> {
}
