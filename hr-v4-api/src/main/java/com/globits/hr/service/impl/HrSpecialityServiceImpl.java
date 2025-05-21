package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.HrSpeciality;
import com.globits.hr.domain.SystemConfig;
import com.globits.hr.dto.HrSpecialityDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.HrSpecialityRepository;
import com.globits.hr.service.HrSpecialityService;

import com.globits.hr.service.SystemConfigService;
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
public class HrSpecialityServiceImpl extends GenericServiceImpl<HrSpeciality, UUID> implements HrSpecialityService {
    @Autowired
    HrSpecialityRepository hrSpecialityRepository;

    @Autowired
    SystemConfigService systemConfigService;

    @Override
    public HrSpecialityDto findByCode(String code) {
        List<HrSpeciality> entities = hrSpecialityRepository.findByCode(code);
        if (entities == null || entities.size() == 0) return null;
        return new HrSpecialityDto(entities.get(0));
    }

    @Override
    public String autoGenerateCode(String configKey) {
        SystemConfig systemConfig = systemConfigService.getConfigByConfigValue(configKey);
        if (systemConfig == null) {
            return "";
        } else {
            String maxCode = hrSpecialityRepository.findMaxCodeByPrefix(systemConfig.getConfigValue(), systemConfig.getNumberOfZero());
            return systemConfigService.generateNextCode(systemConfig.getConfigValue(), systemConfig.getNumberOfZero(), maxCode);
        }
    }

    @Override
    public HrSpecialityDto getSpecialityDto(UUID id) {
        HrSpeciality entity = null;
        Optional<HrSpeciality> optional = hrSpecialityRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        if (entity != null) {
            return new HrSpecialityDto(entity);
        }
        return null;
    }

    @Override
    public HrSpecialityDto saveSpeciality(HrSpecialityDto dto, UUID id) {
        if (dto != null && dto.getCode() != null && StringUtils.hasText(dto.getCode())) {
            HrSpeciality entity = null;
            if (dto.getId() != null) {
                if (dto.getId() != null && !dto.getId().equals(id)) {
                    return null;
                }
                Optional<HrSpeciality> optional = hrSpecialityRepository.findById(id);
                if (optional.isPresent()) {
                    entity = optional.get();
                }
                if (entity != null) {
                    entity.setModifyDate(LocalDateTime.now());
                }
            }
            if (entity == null) {
                entity = new HrSpeciality();
                entity.setCreateDate(LocalDateTime.now());
                entity.setModifyDate(LocalDateTime.now());
            }
            entity.setCode(dto.getCode());
            entity.setName(dto.getName());
            entity.setNameEng(dto.getNameEng());
            entity.setNumbericCode(dto.getNumbericCode());

            entity = hrSpecialityRepository.save(entity);
            return new HrSpecialityDto(entity);
        }
        return null;
    }

    @Override
    public Boolean deleteSpeciality(UUID id) {
        if (id != null) {
            hrSpecialityRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        List<HrSpeciality> entities = hrSpecialityRepository.findByCode(code);
        if (entities != null && entities.size() > 0 && entities.get(0) != null && entities.get(0).getId() != null) {
            if (id != null && StringUtils.hasText(id.toString())) {
                return !entities.get(0).getId().equals(id);
            }
            return true;
        }
        return false;
    }

    @Override
    public Page<HrSpecialityDto> searchByPage(SearchDto dto) {
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

        String orderBy = " ORDER BY entity.createDate DESC";

        String sqlCount = "select count(entity.id) from HrSpeciality as entity where (1=1)   ";
        String sql = "select new com.globits.hr.dto.HrSpecialityDto(entity) from HrSpeciality as entity where (1=1)  ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text )";
        }
        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, HrSpecialityDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<HrSpecialityDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }
}
