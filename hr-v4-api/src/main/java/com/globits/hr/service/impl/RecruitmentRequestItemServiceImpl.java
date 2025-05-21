package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.PositionTitle;
import com.globits.hr.domain.RecruitmentRequest;
import com.globits.hr.domain.RecruitmentRequestItem;
import com.globits.hr.dto.PositionTitleDto;
import com.globits.hr.dto.RecruitmentRequestDto;
import com.globits.hr.dto.RecruitmentRequestItemDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchRecruitmentRequestItemDto;
import com.globits.hr.repository.PositionTitleRepository;
import com.globits.hr.repository.RecruitmentRequestItemRepository;
import com.globits.hr.repository.RecruitmentRequestRepository;
import com.globits.hr.service.RecruitmentRequestItemService;
import jakarta.persistence.Query;
import org.checkerframework.checker.optional.qual.Present;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class RecruitmentRequestItemServiceImpl extends GenericServiceImpl<RecruitmentRequestItem, UUID>
        implements RecruitmentRequestItemService {

    @Autowired
    private RecruitmentRequestItemRepository recruitmentRequestItemRepository;

    @Autowired
    private RecruitmentRequestRepository recruitmentRequestRepository;

    @Autowired
    private PositionTitleRepository positiontitleRepository;

    @Override
    public Page<RecruitmentRequestItemDto> searchByPage(SearchRecruitmentRequestItemDto dto) {
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

        String sqlCount = "select count(distinct entity.id) from RecruitmentRequestItem as entity ";
        String sql = "select distinct new com.globits.hr.dto.RecruitmentRequestItemDto(entity) from RecruitmentRequestItem as entity ";

        String whereClause = " where (1=1) and (entity.voided = false or entity.voided is null)  ";
        String orderBy = " ORDER BY entity.modifyDate desc ";

//        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
//            whereClause += " AND ( entity.code LIKE :text OR entity.name LIKE :text ) ";
//        }


        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, RecruitmentRequestDto.class);
        Query qCount = manager.createQuery(sqlCount);

//        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
//            query.setParameter("text", '%' + dto.getKeyword() + '%');
//            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
//        }

        long count = (long) qCount.getSingleResult();

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<RecruitmentRequestItemDto> entities = query.getResultList();
        Page<RecruitmentRequestItemDto> result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    @Override
    public RecruitmentRequestItemDto getById(UUID id) {
        RecruitmentRequestItem entity = recruitmentRequestItemRepository.findById(id).orElse(null);
        if (entity == null)
            return null;
        return new RecruitmentRequestItemDto(entity);
    }

    @Override
    public RecruitmentRequestItemDto saveOrUpdate(RecruitmentRequestItemDto dto) {
        if (dto == null) {
            return null;
        }

        RecruitmentRequestItem entity = new RecruitmentRequestItem();
        if (dto.getId() != null)
            entity = recruitmentRequestItemRepository.findById(dto.getId()).orElse(null);
        if (entity == null) {
            entity = new RecruitmentRequestItem();
        }

        entity.setInPlanQuantity(dto.getInPlanQuantity());
        entity.setExtraQuantity(dto.getExtraQuantity());
        entity.setAnnouncementQuantity(dto.getAnnouncementQuantity());

        if (dto.getRecruitmentRequest() != null) {
            RecruitmentRequest recruitmentRequest = recruitmentRequestRepository.findById(dto.getRecruitmentRequest().getId()).orElse(null);
            entity.setRecruitmentRequest(recruitmentRequest);
        } else {
            entity.setRecruitmentRequest(null);
        }

        if (dto.getPositionTitle() != null) {
            PositionTitle positionTitle = positiontitleRepository.findById(dto.getPositionTitle().getId()).orElse(null);
            entity.setPositionTitle(positionTitle);
        } else {
            entity.setPositionTitle(null);
        }

        entity = recruitmentRequestItemRepository.save(entity);

        return new RecruitmentRequestItemDto(entity);
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteById(UUID id) {
        if (id == null)
            return false;

        RecruitmentRequestItem hrResourcePlan = recruitmentRequestItemRepository.findById(id).orElse(null);
        if (hrResourcePlan == null)
            return false;

        recruitmentRequestItemRepository.delete(hrResourcePlan);

        return true;
    }

    @Override
    @Modifying
    public Boolean deleteMultiple(List<UUID> ids) {
        if (ids == null)
            return false;
        boolean isValid = true;
        for (UUID id : ids) {
            boolean deleteRes = this.deleteById(id);
            if (!deleteRes)
                isValid = false;
        }
        return isValid;
    }

}
