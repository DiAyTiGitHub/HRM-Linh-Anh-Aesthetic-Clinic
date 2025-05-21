package com.globits.hr.service.impl;

import com.globits.budget.domain.Budget;
import com.globits.hr.domain.ProductType;
import com.globits.hr.domain.SystemConfig;
import com.globits.hr.dto.ProductTypeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.ProductTypeRepository;
import com.globits.hr.service.ProductTypeService;
import com.globits.hr.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductTypeServiceImpl implements ProductTypeService {
    @Resource
    private ProductTypeRepository productTypeRepository;

    @PersistenceContext
    private EntityManager manager;

    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    public List<ProductTypeDto> getAll() {
        List<ProductType> productTypes = productTypeRepository.findAll();
        return productTypes.stream()
                .map(ProductTypeDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public ProductTypeDto getProductType(UUID id) {
        ProductType productType = productTypeRepository.findById(id).orElse(null);
        if (productType != null) {
            return new ProductTypeDto(productType);
        }
        return null;
    }

    @Override
    public ProductTypeDto saveProductType(ProductTypeDto dto) {
        if (dto == null) {
            return null;
        }
        ProductType productType = null;
        if (dto.getId() != null) {
            productType = productTypeRepository.findById(dto.getId()).orElse(null);
        }
        if (productType == null) {
            productType = new ProductType();
        }
        productType.setCode(dto.getCode());
        productType.setName(dto.getName());
        productType.setDescription(dto.getDescription());
        productType = productTypeRepository.save(productType);
        return new ProductTypeDto(productType);
    }

    @Override
    public Boolean deleteProductType(UUID id) {
        ProductType productType = productTypeRepository.findById(id).orElse(null);
        if (productType != null) {
            productTypeRepository.delete(productType);
            return true;
        }
        return false;
    }

    @Override
    public Page<ProductTypeDto> paging(SearchDto dto) {
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
        String hqlSelect = "select new com.globits.hr.dto.ProductTypeDto(e) from ProductType e ";
        String hqlCount = "select count(e.id) from ProductType e ";
        String whereClause = " where (1=1) ";
        String orderBy = " order by e.modifyDate desc";
        if (dto.getKeyword() != null) {
            whereClause += " and (e.code like :keyword or e.name like :keyword or e.description like :keyword)";
        }
        hqlSelect += whereClause + orderBy;
        hqlCount += whereClause;
        Query q = manager.createQuery(hqlSelect, ProductTypeDto.class);
        Query qCount = manager.createQuery(hqlCount);
        if (dto.getKeyword() != null) {
            q.setParameter("keyword", '%' + dto.getKeyword() + '%');
            qCount.setParameter("keyword", '%' + dto.getKeyword() + '%');
        }
        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<ProductTypeDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public Boolean checkCode(ProductTypeDto dto) {
        if (dto == null) return false;
        if (dto.getId() == null) {
            List<ProductType> entities = productTypeRepository.findByCode(dto.getCode().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        } else {
            List<ProductType> entities = productTypeRepository.findByCode(dto.getCode().strip());
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (ProductType entity : entities) {
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
            String maxCode = productTypeRepository.findMaxCodeByPrefix(systemConfig.getConfigValue(), systemConfig.getNumberOfZero());
            return systemConfigService.generateNextCode(systemConfig.getConfigValue(), systemConfig.getNumberOfZero(), maxCode);
        }
    }
}
