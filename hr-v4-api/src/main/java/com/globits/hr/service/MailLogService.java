package com.globits.hr.service;

import java.util.UUID;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.MailLog;
import com.globits.hr.dto.MailInfoDto;

public interface MailLogService extends GenericService<MailLog, UUID> {

	MailInfoDto saveOrUpdate(MailInfoDto dto, UUID id);

}
