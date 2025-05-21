package com.globits.hr.service.impl;

import com.globits.hr.domain.ProductAttribute;
import com.globits.hr.dto.ProductAttributeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.ProductAttributeRepository;
import com.globits.hr.repository.ProductRepository;
import com.globits.hr.service.ProductAttributeService;
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
public class ProductAttributeServiceImpl implements ProductAttributeService {
    @Resource
    private ProductAttributeRepository productAttributeRepository;

    @Resource
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager manager;

    @Override
    public List<ProductAttributeDto> getAll() {
        List<ProductAttribute> productAttributes = productAttributeRepository.findAll();
        return productAttributes.stream().map(ProductAttributeDto::new).collect(Collectors.toList());
    }

    @Override
    public ProductAttributeDto getProductAttribute(UUID id) {
        ProductAttribute productAttribute = productAttributeRepository.findById(id).orElse(null);
        if (productAttribute != null) {
            return new ProductAttributeDto(productAttribute);
        }
        return null;
    }

    @Override
    public ProductAttributeDto saveProductAttribute(ProductAttributeDto dto) {
        if(dto == null) {
            return null;
        }
        ProductAttribute productAttribute = null;
        if(dto.getId() != null) {
            productAttribute = productAttributeRepository.findById(dto.getId()).orElse(null);
        }
        if(productAttribute == null) {
            productAttribute = new ProductAttribute();
        }
        productAttribute.setName(dto.getName());
        productAttribute.setDescription(dto.getDescription());
        if (dto.getProduct() != null && dto.getProduct().getId() != null) {
            productAttribute.setProduct(productRepository.findById(dto.getProduct().getId()).orElse(null));
        }
        productAttribute = productAttributeRepository.save(productAttribute);
        return new ProductAttributeDto(productAttribute);
    }

    @Override
    public Boolean deleteProductAttribute(UUID id) {
        ProductAttribute productAttribute = productAttributeRepository.findById(id).orElse(null);
        if (productAttribute != null) {
            productAttributeRepository.delete(productAttribute);
            return true;
        }
        return false;
    }

    @Override
    public Page<ProductAttributeDto> paging(SearchDto dto) {
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
        String hqlSelect = "select new com.globits.hr.dto.ProductAttributeDto(e, false) from ProductAttribute e ";
        String hqlCount = "select count(e.id) from ProductAttribute e ";
        String whereClause = " where (1=1) ";
        String orderBy = " order by e.modifyDate desc";
        if (dto.getKeyword() != null) {
            whereClause += " and (e.name like :keyword or e.description like :keyword)";
        }
        hqlSelect += whereClause + orderBy;
        hqlCount += whereClause;
        Query q = manager.createQuery(hqlSelect, ProductAttributeDto.class);
        Query qCount = manager.createQuery(hqlCount);
        if (dto.getKeyword() != null) {
            q.setParameter("keyword", '%' + dto.getKeyword() + '%');
            qCount.setParameter("keyword", '%' + dto.getKeyword() + '%');
        }
        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<ProductAttributeDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }
}
