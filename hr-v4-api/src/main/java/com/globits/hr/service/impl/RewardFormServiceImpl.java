package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.RankTitle;
import com.globits.hr.domain.RewardForm;
import com.globits.hr.dto.RewardFormDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.RewardFormRepository;
import com.globits.hr.service.RewardFormService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class RewardFormServiceImpl extends GenericServiceImpl<RewardForm, UUID> implements RewardFormService {

    @Autowired
    private RewardFormRepository rewardFormRepository;

    @Override
    public Page<RewardFormDto> searchByPage(SearchDto dto) {
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
        String whereClause = "";
        String orderBy = " ORDER BY entity.createDate ";
        String sqlCount = "select count(entity.id) from RewardForm as entity where (1=1) ";
        String sql = "select new  com.globits.hr.dto.RewardFormDto(entity) from RewardForm as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text ) ";
        }
        sql += whereClause + orderBy;
        sqlCount += whereClause;
        Query q = manager.createQuery(sql, RewardFormDto.class);
        Query qCount = manager.createQuery(sqlCount);
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<RewardFormDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public RewardFormDto saveOrUpdate(RewardFormDto dto) {
//        Create if id is null, else edit
        RewardForm reward = dto.toEntity();
        if (dto.getId() != null) {
            reward.setCreateDate(LocalDateTime.now());
            reward.setCreatedBy("");
        }
        reward = rewardFormRepository.save(reward);
        return new RewardFormDto(reward);
    }

    @Override
    public void deleteOne(UUID id) {
        RewardForm reward = this.findById(id);
        if (reward != null) {
            rewardFormRepository.delete(reward);
        }
    }

    @Override
    public void delete(List<UUID> ids) {
        if (ids != null) {
            for (UUID id : ids) {
                rewardFormRepository.deleteById(id);
            }
        }
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        if (code == null) return false;
        if (id == null) {
            List<RewardForm> entities = rewardFormRepository.findByCode(code);
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        } else {
            List<RewardForm> entities = rewardFormRepository.findByCode(code);
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (RewardForm entity : entities) {
                if (!entity.getId().equals(id)) return false;
            }
        }
        return true;
    }

    @Override
    public RewardFormDto getOne(UUID id) {
        RewardForm entity = null;
        Optional<RewardForm> optional = rewardFormRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        if (entity != null) {
            return new RewardFormDto(entity);
        }
        return null;
    }

}
