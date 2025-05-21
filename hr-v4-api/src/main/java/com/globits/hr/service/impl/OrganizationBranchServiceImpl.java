package com.globits.hr.service.impl;

import com.globits.core.domain.AdministrativeUnit;
import com.globits.core.domain.Country;
import com.globits.core.repository.AdministrativeUnitRepository;
import com.globits.core.repository.CountryRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.OrganizationBranch;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.OrganizationBranchDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchOrganizationBranchDto;
import com.globits.hr.repository.AssetRepository;
import com.globits.hr.repository.OrganizationBranchRepository;
import com.globits.hr.service.OrganizationBranchService;
import com.globits.hr.service.UserExtService;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;
import jakarta.annotation.Resource;
import jakarta.persistence.Query;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrganizationBranchServiceImpl extends GenericServiceImpl<OrganizationBranch, UUID> implements OrganizationBranchService {
    @Autowired
    private UserExtService userExtService;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private AdministrativeUnitRepository administrativeUnitRepository;

    @Resource
    private OrganizationBranchRepository organizationBranchRepository;

    @Override
    public OrganizationBranchDto getById(UUID id) {
        OrganizationBranch optionalOrganizationBranch = organizationBranchRepository.findById(id).orElse(null);
        if (optionalOrganizationBranch != null) {
            return new OrganizationBranchDto(optionalOrganizationBranch);
        }
        return null;
    }

    @Override
    @Modifying
    @Transactional
    public OrganizationBranchDto saveOrganizationBranch(OrganizationBranchDto dto) {
        if (dto == null) {
            return null;
        }

        OrganizationBranch organizationBranch = new OrganizationBranch();
        if (dto.getId() != null) organizationBranch = organizationBranchRepository.findById(dto.getId()).orElse(null);
        if (organizationBranch == null) organizationBranch = new OrganizationBranch();

        organizationBranch.setName(dto.getName());
        organizationBranch.setCode(dto.getCode());

        if (dto.getCountry() != null && dto.getCountry().getId() != null) {
            Country country = countryRepository.findById(dto.getCountry().getId()).orElse(null);
            organizationBranch.setCountry(country);
        } else {
            organizationBranch.setCountry(null);
        }

        if (dto.getProvince() != null && dto.getProvince().getId() != null) {
            AdministrativeUnit province = administrativeUnitRepository.findById(dto.getProvince().getId()).orElse(null);
            organizationBranch.setProvince(province);
        } else {
            organizationBranch.setProvince(null);
        }

        if (dto.getDistrict() != null && dto.getDistrict().getId() != null) {
            AdministrativeUnit district = administrativeUnitRepository.findById(dto.getDistrict().getId()).orElse(null);
            organizationBranch.setDistrict(district);
        } else {
            organizationBranch.setDistrict(null);
        }

        if (dto.getCommune() != null && dto.getCommune().getId() != null) {
            AdministrativeUnit commune = administrativeUnitRepository.findById(dto.getCommune().getId()).orElse(null);
            organizationBranch.setCommune(commune);
        } else {
            organizationBranch.setCommune(null);
        }

        organizationBranch.setAddress(dto.getAddress());
        organizationBranch.setPhoneNumber(dto.getPhoneNumber());
        organizationBranch.setNote(dto.getNote());

        //only set organization when create new
        if (dto.getOrganization() == null && dto.getId() == null && userExtService.getCurrentStaffEntity() != null && userExtService.getCurrentStaffEntity().getUser() != null
                && userExtService.getCurrentStaffEntity().getUser().getOrg() != null) {
            organizationBranch.setOrganization(userExtService.getCurrentStaffEntity().getUser().getOrg());
        }

        organizationBranch = organizationBranchRepository.save(organizationBranch);

        return new OrganizationBranchDto(organizationBranch);
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteOrganizationBranch(UUID id) {
        if (id == null) return false;

        OrganizationBranch organizationBranch = organizationBranchRepository.findById(id).orElse(null);
        if (organizationBranch == null) return false;

        organizationBranchRepository.delete(organizationBranch);
        return true;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteMultipleOrganizationBranches(List<UUID> ids) {
        if (ids == null) return false;
        boolean isValid = true;
        for (UUID organizationBranchId : ids) {
            boolean deleteRes = this.deleteOrganizationBranch(organizationBranchId);
            if (!deleteRes) isValid = false;
        }
        return isValid;
    }

    @Override
    public Page<OrganizationBranchDto> pagingOrganization(SearchOrganizationBranchDto dto) {
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

        String sqlCount = "select count(distinct entity.id) from OrganizationBranch as entity ";
        String sql = "select distinct new com.globits.hr.dto.OrganizationBranchDto(entity) from OrganizationBranch as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.displayName LIKE :text OR entity.staffCode LIKE :text ) ";
        }

        if (dto.getOrganizationId() != null) {
            whereClause += " AND ( entity.organization.id = :orgId) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, OrganizationBranchDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        if (dto.getOrganizationId() != null) {
            query.setParameter("orgId", dto.getOrganizationId());
            qCount.setParameter("orgId", dto.getOrganizationId());
        }

        List<OrganizationBranchDto> entities = new ArrayList<>();
        long count = (long) qCount.getSingleResult();
        Page<OrganizationBranchDto> result;

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        entities = query.getResultList();
        result = new PageImpl<>(entities, pageable, count);

        return result;
    }
}
