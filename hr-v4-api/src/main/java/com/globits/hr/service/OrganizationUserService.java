package com.globits.hr.service;

import com.globits.core.domain.OrganizationUser;

import java.util.List;
import java.util.UUID;

public interface OrganizationUserService {
    public List<OrganizationUser> findEntityByOrgIdAndUsrId(UUID orgId, Long userId);
}
