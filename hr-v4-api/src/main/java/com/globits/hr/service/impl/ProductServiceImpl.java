package com.globits.hr.service.impl;

import com.globits.budget.domain.Budget;
import com.globits.hr.domain.Product;
import com.globits.hr.domain.ProductAttribute;
import com.globits.hr.domain.SystemConfig;
import com.globits.hr.dto.ProductAttributeDto;
import com.globits.hr.dto.ProductDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.ProductService;
import com.globits.hr.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    @Resource
    private ProductRepository productRepository;

    @Resource
    private ProductTypeRepository productTypeRepository;

    @Resource
    private ProductAttributeRepository productAttributeRepository;

    @Resource
    private HRDepartmentRepository hrDepartmentRepository;

    @Resource
    private AssetRepository assetRepository;

    @PersistenceContext
    private EntityManager manager;

    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    public List<ProductDto> getAll() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(e -> new ProductDto(e, false)).collect(Collectors.toList());
    }

    @Override
    public ProductDto getProduct(UUID id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            return new ProductDto(product, true);
        }
        return null;
    }

    @Override
    public ProductDto saveProduct(ProductDto dto) {
        if (dto == null) {
            return null;
        }
        Product product = null;
        if (dto.getId() != null) {
            product = productRepository.findById(dto.getId()).orElse(null);
        }
        if (product == null) {
            product = new Product();
        }
        product.setCode(dto.getCode());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setModel(dto.getModel());
        product.setSerialNumber(dto.getSerialNumber());
        product.setManufacturer(dto.getManufacturer());
        if (dto.getProductType() != null && dto.getProductType().getId() != null) {
            product.setProductType(productTypeRepository.findById(dto.getProductType().getId()).orElse(null));
        }
        if (dto.getDepartment() != null && dto.getDepartment().getId() != null) {
            product.setDepartment(hrDepartmentRepository.findById(dto.getDepartment().getId()).orElse(null));
        }

        Set<ProductAttribute> productAttributes = new HashSet<>();
        if (!CollectionUtils.isEmpty(dto.getAttributes())) {
            for (ProductAttributeDto attributeDto : dto.getAttributes()) {
                ProductAttribute attribute = null;
                if (attributeDto.getId() != null) {
                    attribute = productAttributeRepository.findById(attributeDto.getId()).orElse(null);
                }
                if (attribute == null) {
                    attribute = new ProductAttribute();
                }
                attribute.setName(attributeDto.getName());
                attribute.setDescription(attributeDto.getDescription());
                attribute.setProduct(product);
                productAttributes.add(attribute);
            }
        }
        if (!CollectionUtils.isEmpty(dto.getAttributes())) {
            product.getAttributes().clear();
            product.getAttributes().addAll(productAttributes);
        } else {
            product.getAttributes().clear();
        }
        product.setPrice(dto.getPrice());
        product = productRepository.save(product);
        return new ProductDto(product);
    }

    @Override
    public Boolean deleteProduct(UUID id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            productRepository.delete(product);
            return true;
        }
        return false;
    }

    @Override
    public Page<ProductDto> paging(SearchDto dto) {
        if (dto == null) {
            return null;
        }
        List<UUID> productIds = assetRepository.getListProducts(); // lay danh sach product duoc asset va dang duoc su dung
        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();
        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }
        String hqlSelect = "select new com.globits.hr.dto.ProductDto(e, false) from Product e ";
        String hqlCount = "select count(e.id) from Product e ";
        String whereClause = " where (1=1) ";
        String orderBy = " order by e.modifyDate desc";
        if (dto.getKeyword() != null) {
            whereClause += " and (e.model like :keyword or e.name like :keyword or e.description like :keyword)";
        }
        if (dto.getBeingUsed() != null && dto.getBeingUsed() && !CollectionUtils.isEmpty(productIds)) {
            whereClause += " and (e.id not in :productIds)";
        }
        hqlSelect += whereClause + orderBy;
        hqlCount += whereClause;
        Query q = manager.createQuery(hqlSelect, ProductDto.class);
        Query qCount = manager.createQuery(hqlCount);
        if (dto.getKeyword() != null) {
            q.setParameter("keyword", '%' + dto.getKeyword() + '%');
            qCount.setParameter("keyword", '%' + dto.getKeyword() + '%');
        }
        if (dto.getBeingUsed() != null && dto.getBeingUsed() && !CollectionUtils.isEmpty(productIds)) {
            q.setParameter("productIds", productIds);
            qCount.setParameter("productIds", productIds);
        }
        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<ProductDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public Boolean checkCode(ProductDto dto) {
        if (dto == null) return false;
        List<Product> entities = productRepository.findByCode(dto.getCode().strip());
        if (dto.getId() == null) {
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            return false;

        } else {
            if (entities == null || entities.isEmpty()) {
                return true;
            }
            for (Product entity : entities) {
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
            String maxCode = productRepository.findMaxCodeByPrefix(systemConfig.getConfigValue(), systemConfig.getNumberOfZero());
            return systemConfigService.generateNextCode(systemConfig.getConfigValue(), systemConfig.getNumberOfZero(), maxCode);
        }
    }
}
