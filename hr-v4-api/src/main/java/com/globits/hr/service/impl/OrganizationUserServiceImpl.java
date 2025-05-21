package com.globits.hr.service.impl;

import com.globits.core.domain.Organization;
import com.globits.core.domain.OrganizationUser;
import com.globits.core.dto.OrganizationDto;
import com.globits.core.repository.OrganizationRepository;
import com.globits.core.repository.OrganizationUserRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.OrganizationExtService;
import com.globits.hr.service.OrganizationUserService;
import com.globits.security.repository.UserRepository;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrganizationUserServiceImpl extends GenericServiceImpl<OrganizationUser, UUID> implements OrganizationUserService {
    @Autowired
    private OrganizationRepository organizationRepository;

   @Autowired
    private UserRepository userRepository;

   @Autowired
    private OrganizationUserRepository organizationUserRepository;

    @Override
    public List<OrganizationUser> findEntityByOrgIdAndUsrId(UUID orgId, Long userId) {
        if (orgId == null || userId == null) {
            return null;
        }

        String whereClause = " where entity.organization.id = :orgId and entity.user.id = :userId ";
        String sql = "select distinct entity from OrganizationUser as entity ";

        sql += whereClause;

        Query query = manager.createQuery(sql, OrganizationUser.class);
        query.setParameter("orgId", orgId);
        query.setParameter("userId", userId);

        List<OrganizationUser> entities = query.getResultList();

        return entities;
    }
}
