package com.globits.hr.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.globits.hr.domain.MailLog;

@Repository
public interface MailLogRepository extends JpaRepository<MailLog, UUID> {

}
