/*
 * TA va Giang làm
 */

package com.globits.hr.repository;

import com.globits.hr.domain.StaffInterviewSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StaffInterviewScheduleRepository extends JpaRepository<StaffInterviewSchedule, UUID> {
    
}
