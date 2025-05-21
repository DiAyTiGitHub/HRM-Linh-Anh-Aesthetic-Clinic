package com.globits.hr.service.impl;

import com.globits.core.domain.Country;
import com.globits.core.dto.CountryDto;
import com.globits.core.repository.CountryRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.HrCountryService;

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
public class HrCountryServiceImpl extends GenericServiceImpl<Country, UUID> implements HrCountryService {
    @Autowired
    private CountryRepository countryRepository;

    @Override
    public Page<CountryDto> searchByPage(SearchDto dto) {
        if (dto == null)
            return null;

        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0)
            pageIndex--;
        else
            pageIndex = 0;

        String whereClause = "";

        String orderBy = " ORDER BY entity.name DESC";
        if (dto.getOrderBy() != null && StringUtils.hasLength(dto.getOrderBy()))
            orderBy = " ORDER BY entity." + dto.getOrderBy() + " ASC";

        String sqlCount = "select count(entity.id) from Country as entity where (1=1)";
        String sql = "select new com.globits.core.dto.CountryDto(entity) from Country as entity where (1=1)";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword()))
            whereClause += " AND ( UPPER(entity.name) LIKE UPPER(:text) OR UPPER(entity.code) LIKE UPPER(:text) )";

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, CountryDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<CountryDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public CountryDto saveOne(CountryDto dto, UUID id) {
        if (dto != null) {
            String currentUserName = "Unknown User";
            Country entity = null;
            if (id != null) {
                if (dto.getId() != null && !dto.getId().equals(id)) {
                    return null;
                }
                Optional<Country> optional = countryRepository.findById(id);
                if (optional.isPresent()) {
                    entity = optional.get();
                }
                if (entity != null) {
                    entity.setModifyDate(LocalDateTime.now());
                    entity.setCreatedBy(currentUserName);
                }
            }
            if (entity == null) {
                entity = new Country();
                entity.setCreateDate(LocalDateTime.now());
                entity.setModifyDate(LocalDateTime.now());
                entity.setCreatedBy(currentUserName);
                entity.setModifiedBy(currentUserName);
            }
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());
            entity = countryRepository.save(entity);
            return new CountryDto(entity);
        }
        return null;
    }

    @Override
    public void remove(UUID id) {
        Country entity = null;
        Optional<Country> optional = countryRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        if (entity != null) {
            countryRepository.delete(entity);
        }
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        if (StringUtils.hasText(code)) {
            Country entity = countryRepository.findByCode(code);
            if (entity != null) {
                return id == null || !entity.getId().equals(id);
            }
            return false;
        }
        return null;
    }

    @Override
    public CountryDto findByCode(String code) {
        List<Country> entities = countryRepository.findListByCode(code);
        if (entities == null || entities.size() == 0) return null;
        return new CountryDto(entities.get(0));
    }
}
