package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.RankTitle;
import com.globits.hr.domain.SystemConfig;
import com.globits.hr.dto.RankTitleDto;
import com.globits.hr.dto.search.SearchRankTitleDto;
import com.globits.hr.repository.RankTitleRepository;
import com.globits.hr.service.RankTitleService;
import com.globits.hr.service.SystemConfigService;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RankTitleServiceImpl extends GenericServiceImpl<RankTitle, UUID> implements RankTitleService {

    @Resource
    private RankTitleRepository rankTitleRepository;

    @Resource
    private SystemConfigService systemConfigService;

    @Override
    public RankTitleDto saveRankTitle(RankTitleDto dto) {
        if (dto == null) {
            return null;
        }

        RankTitle rankTitle = new RankTitle();
        if (dto.getId() != null) {
            rankTitle = rankTitleRepository.findById(dto.getId()).orElse(null);
        }

        if (rankTitle == null) {
            rankTitle = new RankTitle();
        }

        rankTitle.setName(dto.getName());
        rankTitle.setOtherName(dto.getOtherName());
        rankTitle.setShortName(dto.getShortName());
        rankTitle.setLevel(dto.getLevel());
        rankTitle.setSubLevel(dto.getSubLevel());
        rankTitle.setDescription(dto.getDescription());
        rankTitle.setSocialInsuranceSalary(dto.getSocialInsuranceSalary());
        rankTitle.setReferralFeeLevel(dto.getReferralFeeLevel());

        rankTitle = rankTitleRepository.save(rankTitle);

        return new RankTitleDto(rankTitle);
    }

    @Override
    public Integer saveListRankTitle(List<RankTitleDto> dtos) {
        if (dtos == null || dtos.isEmpty()) return 0;
        List<RankTitle> rankTitleListSave = new ArrayList<RankTitle>();
        for (RankTitleDto dto : dtos) {
            RankTitle rankTitle = null;
            if (dto.getShortName() != null) {
                List<RankTitle> rankTitleList = rankTitleRepository.findByShortName(dto.getShortName());
                if (rankTitleList != null && !rankTitleList.isEmpty()) {
                    rankTitle = rankTitleList.get(0);
                }
            }
            if (rankTitle == null) {
                rankTitle = new RankTitle();
            }
            rankTitle.setName(dto.getName());
            rankTitle.setOtherName(dto.getOtherName());
            rankTitle.setShortName(dto.getShortName());
            rankTitle.setLevel(dto.getLevel());
            rankTitle.setSubLevel(dto.getSubLevel());
            rankTitle.setDescription(dto.getDescription());
            rankTitle.setSocialInsuranceSalary(dto.getSocialInsuranceSalary());
            rankTitle.setReferralFeeLevel(dto.getReferralFeeLevel());

            rankTitleListSave.add(rankTitle);
        }
        List<RankTitle> response = rankTitleRepository.saveAll(rankTitleListSave);

        return response.size();
    }

    @Override
    public RankTitleDto getById(UUID id) {
        RankTitle rankTitle = rankTitleRepository.findById(id).orElse(null);
        if (rankTitle != null) {
            return new RankTitleDto(rankTitle);
        }
        return null;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteRankTitle(UUID id) {
        if (id == null) return false;
        RankTitle rankTitle = rankTitleRepository.findById(id).orElse(null);
        if (rankTitle == null) return false;
        rankTitleRepository.delete(rankTitle);
        return true;
    }

    @Override
    @Modifying
    @Transactional

    public Boolean deleteMultipleRankTitles(List<UUID> ids) {
        if (ids == null) return false;
        boolean isValid = true;
        for (UUID ranktitleId : ids) {
            boolean deleteRes = this.deleteRankTitle(ranktitleId);
            if (!deleteRes) isValid = false;
        }
        return isValid;
    }

    @Override
    public Page<RankTitleDto> pagingRankTitle(SearchRankTitleDto dto) {
        if (dto == null) {
            return Page.empty();
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

        String sqlCount = "select count(distinct entity.id) from RankTitle as entity ";
        String sql = "select distinct new com.globits.hr.dto.RankTitleDto(entity) from RankTitle as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.shortName LIKE :text OR entity.otherName LIKE :text) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, RankTitleDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        long count = (long) qCount.getSingleResult();

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        List<RankTitleDto> entities = query.getResultList();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public RankTitleDto findByShortName(String shortName) {
        if (shortName == null || shortName.isEmpty()) return null;
        List<RankTitle> entities = rankTitleRepository.findByShortName(shortName);
        if (entities != null && !entities.isEmpty()) {
            return new RankTitleDto(entities.get(0));
        }
        return null;
    }

    @Override
    public Boolean checkShortName(RankTitleDto dto) {
        if (dto == null) return false;
        if (dto.getId() == null) {
            List<RankTitle> entities = rankTitleRepository.findByShortName(dto.getShortName());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        } else {
            List<RankTitle> entities = rankTitleRepository.findByShortName(dto.getShortName());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (RankTitle entity : entities) {
                if (!entity.getId().equals(dto.getId())) return false;
            }
        }
        return true;
    }

    @Override
    public String autoGenerateCode(String configKey) {
        SystemConfig systemConfig = systemConfigService.getConfigByConfigValue(configKey);
        if (systemConfig == null) {
            return "";
        } else {
            String maxCode = rankTitleRepository.findMaxCodeByPrefix(systemConfig.getConfigValue(), systemConfig.getNumberOfZero());
            return systemConfigService.generateNextCode(systemConfig.getConfigValue(), systemConfig.getNumberOfZero(), maxCode);
        }
    }

}
