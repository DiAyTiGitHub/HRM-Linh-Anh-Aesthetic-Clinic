package com.globits.hr.service.impl;

import com.globits.core.domain.AdministrativeUnit;
import com.globits.core.domain.Country;
import com.globits.core.domain.Organization;
import com.globits.core.domain.OrganizationUser;
import com.globits.core.dto.OrganizationDto;
import com.globits.core.dto.OrganizationUserDto;
import com.globits.core.dto.ResultMessageDto;
import com.globits.core.repository.AdministrativeUnitRepository;
import com.globits.core.repository.CountryRepository;
import com.globits.core.repository.OrganizationRepository;
import com.globits.core.repository.OrganizationUserRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.OrganizationBranch;
import com.globits.hr.domain.OrganizationImage;
import com.globits.hr.dto.OrganizationBranchDto;
import com.globits.hr.dto.OrganizationExtDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.OrganizationBranchRepository;
import com.globits.hr.repository.OrganizationImageRepository;
import com.globits.hr.service.OrganizationBranchService;
import com.globits.hr.service.OrganizationExtService;
import com.globits.hr.service.UserExtService;
import com.globits.security.domain.Role;
import com.globits.security.domain.User;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;
import com.globits.security.service.RoleService;
import com.globits.security.service.UserService;
import jakarta.annotation.Resource;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrganizationExtServiceImpl extends GenericServiceImpl<Organization, UUID> implements OrganizationExtService {
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private OrganizationUserRepository organizationUserRepository;
    @Autowired
    private UserExtService userExtService;
    @Autowired
    OrganizationImageRepository organizationImageRepository;


    @Override
    public Page<OrganizationDto> pagingOrganization(SearchDto dto) {
        if (dto == null) {
            return null;
        }

        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }

        String whereClause = " where (1=1) ";
        String orderBy = " ORDER BY entity.createDate ";

        String sqlCount = "select count(distinct entity.id) from Organization as entity ";
//        String sql = "select distinct new com.globits.core.dto.OrganizationDto(entity) from Organization as entity ";
        String sql = "select distinct entity from Organization as entity ";


        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text ) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;


        Query query = manager.createQuery(sql, Organization.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }


        long count = (long) qCount.getSingleResult();
        Page<OrganizationDto> result;

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<Organization> entities = query.getResultList();
        List<OrganizationDto> data = new ArrayList<>();
        for (Organization organization : entities) {
            OrganizationDto item = new OrganizationDto(organization, true);
            item.setWebsite(organization.getWebsite());
            data.add(item);
        }
        result = new PageImpl<>(data, pageable, count);

        return result;
    }


    @Override
    public OrganizationExtDto getById(UUID orgId) {
        if (orgId == null) return null;
        Organization organization = organizationRepository.findById(orgId).orElse(null);
        if (organization == null) return null;

        OrganizationExtDto response = new OrganizationExtDto(organization);
        OrganizationImage orgImage = organizationImageRepository.findByOrganizationId(response.getId());
        if (orgImage != null) {
            response.setImagePath(orgImage.getImagePath());
        }

        return response;
    }


    @Override
    public OrganizationExtDto saveOrganization(OrganizationExtDto dto) {
        if (dto == null) return null;

        Organization entity = null;
        if (dto.getId() != null) {
            entity = organizationRepository.findById(dto.getId()).orElse(null);
            if (entity == null) return null;
        } else {
            entity = new Organization();
        }

        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setLevel(dto.getLevel());
        entity.setOrganizationType(dto.getOrganizationType());
        entity.setWebsite(dto.getWebsite());

        if (dto.getParent() != null && dto.getParent().getId() != null) {
            Organization parentOrganization = organizationRepository.findById(dto.getParent().getId()).orElse(null);
            if (parentOrganization == null) return null;
            entity.setParent(parentOrganization);
        } else {
            entity.setParent(null);
        }

        entity = organizationRepository.save(entity);
        if (entity == null) return null;

        //save image path to OrganizationImage
        OrganizationImage orgImage = organizationImageRepository.findByOrganizationId(entity.getId());
        if (orgImage == null) {
            orgImage = new OrganizationImage();
            orgImage.setOrganization(entity);
        }
        if (dto.getImagePath() != null && dto.getImagePath().length() > 0) {
            orgImage.setImagePath(dto.getImagePath());
        } else {
            orgImage.setImagePath(null);
        }
        // save orgImage
        orgImage = organizationImageRepository.save(orgImage);

        OrganizationExtDto response = new OrganizationExtDto(entity);
        response.setWebsite(entity.getWebsite());
        response.setImagePath(orgImage.getImagePath());

        return response;
    }


    @Override
    @Modifying
    @Transactional
    public Boolean deleteOrganization(UUID orgId) {
        if (orgId == null) return false;
        Organization organization = organizationRepository.findById(orgId).orElse(null);
        if (organization == null) return false;
        organizationRepository.delete(organization);
        return true;

    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteMultipleOrganization(List<UUID> orgIds) {
        if (orgIds != null && !orgIds.isEmpty()) {
            boolean isValid = true;
            for (UUID id : orgIds) {
                boolean statusDelete = this.deleteOrganization(id);
                if (!statusDelete) isValid = true;
            }
            return isValid;
        }
        return false;
    }

    @Override
    public List<Organization> findEntityByCode(String code) {
        if (code == null) {
            return null;
        }

        String whereClause = " where entity.code = :code ";
        String sql = "select distinct entity from Organization as entity ";

        sql += whereClause;

        Query query = manager.createQuery(sql, Organization.class);
        query.setParameter("code", code);

        List<Organization> entities = query.getResultList();

        return entities;
    }

    @Override
    public OrganizationExtDto getCurrentOrganizationOfCurrentUser() {
        User currentUser = null;
        if (userExtService.getCurrentStaff() != null && userExtService.getCurrentStaff().getUser() != null)
            currentUser = userExtService.getCurrentStaffEntity().getUser();
        if (userExtService.getCurrentUser() != null) {
            currentUser = userExtService.getCurrentUserEntity();
        }

        if (currentUser == null || currentUser.getOrg() == null || currentUser.getOrg().getId() == null) return null;

        Organization org = organizationRepository.findById(currentUser.getOrg().getId()).orElse(null);
        if (org == null) return null;

        OrganizationExtDto res = new OrganizationExtDto(org);
        res.setWebsite(org.getWebsite());
        OrganizationImage orgImage = organizationImageRepository.findByOrganizationId(org.getId());
        if (orgImage != null) res.setImagePath(orgImage.getImagePath());

        return res;
    }

}
