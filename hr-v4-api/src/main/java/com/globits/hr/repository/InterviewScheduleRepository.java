/*
 * TA va Giang làm
 */

package com.globits.hr.repository;

import com.globits.hr.domain.AcademicTitle;
import com.globits.hr.domain.InterviewSchedule;
import com.globits.hr.dto.AcademicTitleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InterviewScheduleRepository extends JpaRepository<InterviewSchedule, UUID> {

}
